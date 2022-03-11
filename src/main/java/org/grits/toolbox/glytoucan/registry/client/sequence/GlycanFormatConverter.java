package org.grits.toolbox.glytoucan.registry.client.sequence;

import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.validation.GlycoVisitorValidation;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.application.glycanbuilder.BuilderWorkspace;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.eurocarbdb.application.glycanbuilder.massutil.IonCloud;
import org.eurocarbdb.application.glycanbuilder.massutil.MassOptions;
import org.eurocarbdb.application.glycanbuilder.renderutil.GlycanRendererAWT;
import org.glycoinfo.WURCSFramework.io.GlycoCT.GlycoVisitorValidationForWURCS;
import org.glycoinfo.WURCSFramework.io.GlycoCT.WURCSExporterGlycoCT;
import org.glycoinfo.WURCSFramework.util.WURCSException;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanInformation;
import org.grits.toolbox.glytoucan.registry.client.util.Registry;

/**
 * Utility class to convert glycan sequence formats.
 *
 * The main task is to convert GWS to WURCS sequences and fill the
 * GlycanInformation object with the intermediates (GlycoCT). If in the process
 * an error happens or the validation code detects invalid sequences or warning
 * this message will be saved in the corresponding fields of the
 * GlycanInformation objects.
 *
 * @author Rene Ranzinger
 *
 */
public class GlycanFormatConverter
{
    protected BuilderWorkspace m_glycanWorkspace = null;
    private ArrayList<String> m_listErrors = new ArrayList<>();
    private ArrayList<String> m_listWarnings = new ArrayList<>();

    public GlycanFormatConverter()
    {
        // initialize the GlycanWorkspace
        this.m_glycanWorkspace = new BuilderWorkspace(new GlycanRendererAWT());
    }

    /**
     * Converts the GWS format into WURCS and stores sequence in
     * GlycanInformation
     *
     * First converts GWS to GlycoCT and corrects known conversion errors. If
     * that is the case warnings will be added in the GlycanInformation objects.
     * In the next step GlycoCT is validated. If there is an error this is
     * stored and failed flag is set to TRUE. If there are warnings, they are
     * stored as well but the sequence is further processed. After this the
     * format is converted to WURCS. Any exception in the process is caught and
     * the failed flag is set to TRUE and the error is stored in the
     * GlycanInformation objects.
     *
     * @param a_glycan
     *            GlycanInformation object containing the GWS sequence. This
     *            object will be updated with the converted sequences (GlycoCT,
     *            WURCS) and/or error messages and warnings.
     */
    public void gwsToWurcs(GlycanInformation a_glycan)
    {
        String t_gws = a_glycan.getGws();
        String t_glycoCT = null;
        try
        {
            // write canonical GWS
            Glycan t_glycanObject = Glycan.fromString(t_gws.trim());
            GlycanFormatConverter.adjustMassOption(t_glycanObject);
            a_glycan.setGwsOrdered(t_glycanObject.toStringOrdered());
            // convert GWS to GlycoCT
            t_glycoCT = this.gws2GlycoCT(t_gws);
            if (t_glycoCT != null)
            {
                // check for common problems and fix them
                FixGlycoCtUtil t_utilFix = new FixGlycoCtUtil();
                String t_glycoCTRecoded = t_utilFix.fixGlycoCT(t_glycoCT);
                a_glycan.setGlycoCt(t_glycoCT);
                if (!t_glycoCT.equals(t_glycoCTRecoded))
                {
                    // recoded GlycoCT did not match
                    a_glycan.setGlycoCtRecoded(t_glycoCTRecoded);
                    a_glycan.addWarnings("Recoded GlycoCT does not match.");
                }
                try
                {
                    // validate the sequence
                    if (!this.validGlycoCT(t_glycoCTRecoded))
                    {
                        // there are errors
                        String t_errorString = "";
                        for (String t_string : this.m_listErrors)
                        {
                            t_errorString += t_string + "\n";
                        }
                        a_glycan.setFailed(true);
                        a_glycan.setError("Error in GlycoCT sequence");
                        a_glycan.addErrorInfo(t_errorString);
                    }
                    else
                    {
                        // passed validation but have there been warning?
                        if (this.m_listWarnings.size() > 0)
                        {
                            String t_warningString = "";
                            for (String t_string : this.m_listWarnings)
                            {
                                t_warningString += t_string + "\n";
                            }
                            a_glycan.addWarnings(t_warningString);
                        }
                        // convert to wurcs
                        String t_wurcs = this.glycoCt2Wurcs(t_glycoCTRecoded);
                        a_glycan.setWurcs(t_wurcs);
                    }
                }
                catch (Exception e)
                {
                    // validation failed
                    String t_stackTrace = Registry.stackTrace2String(e);
                    a_glycan.setFailed(true);
                    a_glycan.setError("Error in GlycoCT validation");
                    a_glycan.addErrorInfo(t_stackTrace);
                }
            }
            else
            {
                a_glycan.setFailed(true);
                a_glycan.setError("Unable to create GlycoCT");
                a_glycan.addErrorInfo("Unable to create GlycoCT");
            }
        }
        catch (Exception e)
        {
            String t_stackTrace = Registry.stackTrace2String(e);
            a_glycan.setFailed(true);
            a_glycan.setError("Error converting to WURCS");
            a_glycan.addErrorInfo(t_stackTrace);
        }
    }

    private boolean validGlycoCT(String a_glycoCTRecoded)
            throws SugarImporterException, GlycoVisitorException
    {
        // reset old errors and warnings
        this.m_listErrors = new ArrayList<>();
        this.m_listWarnings = new ArrayList<>();
        // parse the GlycoCT and create sugar object
        SugarImporterGlycoCTCondensed t_importerGlycoCT = new SugarImporterGlycoCTCondensed();
        Sugar t_sugar = t_importerGlycoCT.parse(a_glycoCTRecoded);

        // Validate sugar with GlycoCT validator
        GlycoVisitorValidation t_validation = new GlycoVisitorValidation();
        t_validation.start(t_sugar);
        ArrayList<String> t_listErrors = t_validation.getErrors();
        ArrayList<String> t_listWarning = t_validation.getWarnings();

        // Remove error "Sugar has more than one root residue."
        for (String t_string : t_listErrors)
        {
            if (!t_string.equals("Sugar has more than one root residue."))
            {
                this.m_listErrors.add(t_string);
            }
        }
        this.m_listWarnings.addAll(t_listWarning);
        // Validate for WURCS
        GlycoVisitorValidationForWURCS t_validationWURCS = new GlycoVisitorValidationForWURCS();
        t_validationWURCS.start(t_sugar);

        // Merge errors and warnings
        this.m_listErrors.addAll(t_validationWURCS.getErrors());
        this.m_listWarnings.addAll(t_validationWURCS.getWarnings());

        if (this.m_listErrors.size() > 0)
        {
            return false;
        }
        return true;
    }

    /**
     * Convert GWS to GlycoCT
     *
     * @param a_gws
     *            GWS sequence
     * @return GlycoCT sequence or NULL if there was an error
     */
    private String gws2GlycoCT(String a_gws)
    {
        Glycan glycanObject = Glycan.fromString(a_gws.trim());
        if (glycanObject != null)
        {
            return glycanObject.toGlycoCTCondensed();
        }
        return null;
    }

    /**
     * Convert GlycoCT to WURCS
     *
     * @param a_glycoCT
     *            GlycoCT sequence
     * @return WURCS sequence
     * @throws SugarImporterException
     *             Thrown if the reading of the GlycoCT sequence fails
     * @throws GlycoVisitorException
     *             Thrown if the writing to WURCS fails
     * @throws WURCSException
     *             Thrown if the object model contains errors
     */
    private String glycoCt2Wurcs(String a_glycoCT)
            throws SugarImporterException, GlycoVisitorException, WURCSException
    {
        WURCSExporterGlycoCT t_exporter = new WURCSExporterGlycoCT();
        t_exporter.start(a_glycoCT);
        return t_exporter.getWURCS();
    }

    /**
     * Convert WURCS to GlycoCT
     *
     * @param a_wurcs
     *            WURCS sequence
     * @return GlycoCT sequence
     * @throws SugarImporterException
     *             Thrown if the reading of WURCS fails
     * @throws GlycoVisitorException
     *             Thrown if the exporting to GlycoCT fails
     * @throws WURCSException
     *             Thrown if the WURCS object contains errors
     */
    public String wurcs2GlycoCt(String a_wurcs)
            throws SugarImporterException, GlycoVisitorException, WURCSException
    {
        WURCSExporterGlycoCT t_exporter = new WURCSExporterGlycoCT();
        t_exporter.start(a_wurcs);
        return t_exporter.getGlycoCT();
    }

    /**
     * Sets a predefined set of mass options for a Glycan object.
     *
     * This is to ensure a unique GWS string can be generated.
     *
     * @param a_glycan
     *            Glycan object that should get the mass option set
     */
    public static void adjustMassOption(Glycan a_glycan)
    {
        MassOptions t_massOptions = new MassOptions();
        t_massOptions.setDerivatization(MassOptions.NO_DERIVATIZATION);
        t_massOptions.setReducingEndType(ResidueType.createFreeReducingEnd());
        a_glycan.setMassOptions(t_massOptions);
        a_glycan.setCharges(new IonCloud());
        a_glycan.setNeutralExchanges(new IonCloud());
    }

}
