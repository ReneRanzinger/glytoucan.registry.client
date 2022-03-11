package org.grits.toolbox.glytoucan.registry.client.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.grits.toolbox.glytoucan.registry.client.io.FolderProcessor;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanFile;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanInformation;
import org.grits.toolbox.glytoucan.registry.client.om.HttpResponseSummary;
import org.grits.toolbox.glytoucan.registry.client.sequence.GlycanFormatConverter;
import org.grits.toolbox.glytoucan.registry.client.ws.GetAccessionResponse;
import org.grits.toolbox.glytoucan.registry.client.ws.GlyTouCanApiClient;
import org.grits.toolbox.glytoucan.registry.client.ws.ResponseUtil;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class that takes care of sequence conversion and interaction with
 * GlyTouCan API
 *
 * @author Rene Ranzinger
 *
 */
public class Registry
{
    private static final String OUTPUT_FILENAME = "glytoucan-registry-result.json";

    private GlycanFormatConverter m_converter = new GlycanFormatConverter();
    private ResponseUtil m_responseUtil = new ResponseUtil();

    /**
     * Convert the GWS sequences in the GlycanInformation objects to WURCS
     *
     * @param a_glycanFiles
     *            List of GlycanFiles with GlycanInformation objects
     */
    public void convertToWurcs(List<GlycanFile> a_glycanFiles)
    {
        // for each file
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            // for each GlycanInformation in the file
            List<GlycanInformation> t_glycanList = t_glycanFile.getGlycans();
            for (GlycanInformation t_glycan : t_glycanList)
            {
                // convert to WURCS
                this.m_converter.gwsToWurcs(t_glycan);
            }
        }
    }

    /**
     * Load GWS file from the provided input folder
     *
     * @param a_inputFolder
     *            Folder with GWS files
     * @return List of GlycanFile objects that contain the sequences from teh
     *         GWS files
     * @throws IOException
     *             Thrown if the loading of the files failed
     */
    public List<GlycanFile> loadGwsFiles(String a_inputFolder) throws IOException
    {
        // create utility class and load the files
        FolderProcessor t_gwsFolderProcessor = new FolderProcessor();
        return t_gwsFolderProcessor.loadFiles(a_inputFolder);
    }

    /**
     * Search for glycans in GlyTouCan
     *
     * Uses the WURCS sequence in the GlycanInformation object to trigger a
     * search in GlyTouCan for the glycan. If successful the GlycanInformation
     * object is updated with the information retrieved from GlyTouCan. This is
     * only performed for sequences that have not failed previously (failed flag
     * in GlycanInformation)
     *
     * @param a_glycanFiles
     *            List of GlycanFile objects that contains the GlycanInformation
     *            with the WURCS sequences.
     * @param a_glyTouCanUserID
     *            GlyTouCan User ID
     * @param a_glyTouCanApiKey
     *            GlyTouCan API Key
     */
    public void findInGlyTouCan(List<GlycanFile> a_glycanFiles, String a_glyTouCanUserID,
            String a_glyTouCanApiKey)
    {
        // create client
        GlyTouCanApiClient t_client = new GlyTouCanApiClient(a_glyTouCanUserID, a_glyTouCanApiKey);
        // for each glycan in the glycan file objects
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            List<GlycanInformation> t_glycanList = t_glycanFile.getGlycans();
            for (GlycanInformation t_glycan : t_glycanList)
            {
                // make sure it did not fail
                if (!t_glycan.isFailed())
                {
                    this.retrieveGlycan(t_glycan, t_client);
                    // if the glycan was found without error check the sequences
                    if (!t_glycan.isFailed() && t_glycan.getGlyTouCanId() != null)
                    {
                        this.checkSequences(t_glycan);
                    }
                }
            }
        }
    }

    private void retrieveGlycan(GlycanInformation a_glycan, GlyTouCanApiClient a_client)
    {
        try
        {
            HttpResponseSummary t_response = a_client.getAccessionNumber(a_glycan.getWurcs());
            if (t_response.getHttpCode() >= 400)
            {
                // there was an error
                a_glycan.setError("GlyTouCan find glycan request failed with HTTP code "
                        + t_response.getHttpCode().toString() + ":" + t_response.getReasonPhrase());
                a_glycan.setFailed(true);
                a_glycan.addErrorInfo(t_response.getBody());
            }
            else
            {
                try
                {
                    GetAccessionResponse t_responseAccession = this.m_responseUtil
                            .processGetAccessionNumber(t_response.getBody());
                    if (t_responseAccession.isJsonError())
                    {
                        // we made some JSON GlyTouCan does not
                        // understand
                        a_glycan.setFailed(true);
                        a_glycan.setError("Invalid JSON error from GlyTouCan.");
                        a_glycan.addErrorInfo(t_responseAccession.getMessage());
                    }
                    else if (t_responseAccession.isError())
                    {
                        // some error happened
                        a_glycan.setFailed(true);
                        a_glycan.setError("GlyTouCan reported error.");
                        a_glycan.addErrorInfo(t_responseAccession.getMessage());
                    }
                    else if (t_responseAccession.isAccessionFound())
                    {
                        // we found the ID
                        a_glycan.setGlyTouCanId(t_responseAccession.getGlycanId());
                        a_glycan.setWurcsAfterRegistration(t_responseAccession.getWurcs());
                        // convert back to GlycoCT
                        try
                        {
                            String t_glycoCT = this.m_converter
                                    .wurcs2GlycoCt(t_responseAccession.getWurcs());
                            a_glycan.setGlycoCtAfterRegistration(t_glycoCT);
                            // convert to GWS
                            Glycan t_glycanConverted = Glycan.fromGlycoCTCondensed(t_glycoCT);
                            a_glycan.setGwsAfterRegistration(t_glycanConverted.toString());
                            GlycanFormatConverter.adjustMassOption(t_glycanConverted);
                            a_glycan.setGwsOrderedAfterRegistration(
                                    t_glycanConverted.toStringOrdered());
                        }
                        catch (Exception e)
                        {
                            String t_stackTrace = Registry.stackTrace2String(e);
                            a_glycan.addWarnings("Unable to translate back to GlycoCT and GWS: "
                                    + e.getMessage());
                            a_glycan.addWarnings(t_stackTrace);
                        }
                    }
                }
                catch (Exception e)
                {
                    String t_stackTrace = Registry.stackTrace2String(e);
                    a_glycan.setFailed(true);
                    a_glycan.setError(
                            "Error in processing of GlyTouCan response: " + e.getMessage());
                    a_glycan.addErrorInfo(t_stackTrace);
                }
            }
        }
        catch (Exception e)
        {
            String t_stackTrace = Registry.stackTrace2String(e);
            a_glycan.setFailed(true);
            a_glycan.setError("Error in finding GlyTouCan ID: " + e.getMessage());
            a_glycan.addErrorInfo(t_stackTrace);
        }
    }

    public void registerInGlyTouCan(List<GlycanFile> a_glycanFiles, String a_glyTouCanUserID,
            String a_glyTouCanApiKey)
    {
        System.out.println("Registration not implemented.");
    }

    public static String stackTrace2String(Exception a_exception)
    {
        StringWriter t_stringWriter = new StringWriter();
        PrintWriter t_printWriter = new PrintWriter(t_stringWriter);
        a_exception.printStackTrace(t_printWriter);
        return t_stringWriter.toString();
    }

    public void writeResultFile(List<GlycanFile> a_glycanFiles, String a_outputFolder)
            throws StreamWriteException, DatabindException, IOException
    {
        ObjectMapper t_mapper = new ObjectMapper();
        t_mapper.writeValue(
                new FileWriter(
                        new File(a_outputFolder + File.separator + Registry.OUTPUT_FILENAME)),
                a_glycanFiles);

    }

    public List<GlycanFile> readResultFile(String a_fileNamePath)
            throws StreamReadException, DatabindException, FileNotFoundException, IOException
    {
        ObjectMapper t_mapper = new ObjectMapper();
        GlycanFile[] t_responseList = t_mapper.readValue(new FileReader(new File(a_fileNamePath)),
                GlycanFile[].class);
        List<GlycanFile> t_result = Arrays.asList(t_responseList);
        return t_result;
    }

    public void updateFromGlyTouCan(List<GlycanFile> a_glycanFiles, String a_glyTouCanUserID,
            String a_glyTouCanApiKey)
    {
        // create client
        GlyTouCanApiClient t_client = new GlyTouCanApiClient(a_glyTouCanUserID, a_glyTouCanApiKey);
        // for each glycan in the glycan file objects
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            List<GlycanInformation> t_glycanList = t_glycanFile.getGlycans();
            for (GlycanInformation t_glycan : t_glycanList)
            {
                // make sure it did not fail
                if (!t_glycan.isFailed() && t_glycan.getGlyTouCanId() == null)
                {
                    this.retrieveGlycan(t_glycan, t_client);
                    // if the glycan was found without error check the sequences
                    if (!t_glycan.isFailed() && t_glycan.getGlyTouCanId() != null)
                    {
                        this.checkSequences(t_glycan);
                    }
                }
            }
        }
    }

    private void checkSequences(GlycanInformation a_glycan)
    {
        // WURCS first
        String t_sequence = a_glycan.getWurcs();
        String t_sequenceAfterRegistration = a_glycan.getWurcsAfterRegistration();
        if (t_sequence == null || t_sequenceAfterRegistration == null)
        {
            a_glycan.setError("WURCS sequence after registration is null.");
            a_glycan.isFailed();
            return;
        }
        if (!t_sequence.equals(t_sequenceAfterRegistration))
        {
            a_glycan.setError("WURCS sequences before and after registration do not match.");
            a_glycan.isFailed();
            return;
        }
        // GlycoCT
        t_sequence = a_glycan.getGlycoCtRecoded();
        if (t_sequence == null)
        {
            t_sequence = a_glycan.getGlycoCt();
        }
        t_sequenceAfterRegistration = a_glycan.getGlycoCtAfterRegistration();
        if (t_sequence != null && t_sequenceAfterRegistration != null)
        {
            if (!t_sequence.equals(t_sequenceAfterRegistration))
            {
                a_glycan.addWarnings("GlycoCT sequences do not match.");
            }
        }
        // GWS
        t_sequence = a_glycan.getGwsOrdered();
        t_sequenceAfterRegistration = a_glycan.getGwsOrderedAfterRegistration();
        if (t_sequence != null && t_sequenceAfterRegistration != null)
        {
            if (!t_sequence.equals(t_sequenceAfterRegistration))
            {
                a_glycan.addWarnings("GWS sequences do not match.");
            }
        }
    }
}
