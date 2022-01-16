package org.grits.toolbox.glytoucan.registry.client.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.grits.toolbox.glytoucan.registry.client.database.GlycanDatabase;
import org.grits.toolbox.glytoucan.registry.client.database.GlycanStructure;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanFile;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanInformation;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class DatabaseIO
{
    private HashMap<String, GlycanStructure> m_glycanListAll = new HashMap<>();

    public void saveGlycanDatabase(GlycanDatabase a_database, String a_fileNamePath)
            throws JAXBException
    {
        JAXBContext t_jaxbContext = JAXBContext.newInstance(GlycanDatabase.class);
        Marshaller t_marshaller = t_jaxbContext.createMarshaller();
        t_marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // Write to File
        t_marshaller.marshal(a_database, new File(a_fileNamePath));
    }

    public GlycanDatabase loadGlycanDatabase(String a_fileNamePath)
            throws FileNotFoundException, JAXBException
    {
        JAXBContext t_jaxbContext = JAXBContext.newInstance(GlycanDatabase.class);
        Unmarshaller t_jaxbUnmarshaller = t_jaxbContext.createUnmarshaller();
        GlycanDatabase t_db = (GlycanDatabase) t_jaxbUnmarshaller
                .unmarshal(new File(a_fileNamePath));
        return t_db;
    }

    public void writeDatabases(List<GlycanFile> a_glycanFiles, String a_outputFolder)
            throws JAXBException
    {
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            this.writeGlycanFileToDatabase(t_glycanFile, a_outputFolder);
        }
        this.writeAllDatabase(a_outputFolder);
    }

    private void writeAllDatabase(String a_outputFolder) throws JAXBException
    {
        if (this.m_glycanListAll.size() > 0)
        {
            GlycanDatabase t_db = new GlycanDatabase();
            t_db.setName("All glycans");
            t_db.setDescription("Generated with glytoucan.registry.client.");
            t_db.setVersion("1.0.0");
            t_db.setStructureCount(this.m_glycanListAll.size());
            List<GlycanStructure> t_glycanList = new ArrayList<>();
            for (GlycanStructure t_glycanStructure : this.m_glycanListAll.values())
            {
                t_glycanList.add(t_glycanStructure);
            }
            t_db.setStructures(t_glycanList);
            this.saveGlycanDatabase(t_db, a_outputFolder + File.separator + "all-glycans.xml");
        }
    }

    private void writeGlycanFileToDatabase(GlycanFile a_glycanFile, String a_outputFolder)
            throws JAXBException
    {
        List<GlycanStructure> t_glycanList = new ArrayList<>();
        for (GlycanInformation t_glycan : a_glycanFile.getGlycans())
        {
            if (!t_glycan.isFailed() && t_glycan.getGlyTouCanId() != null)
            {
                GlycanStructure t_glycanStructure = new GlycanStructure();
                t_glycanStructure.setId(t_glycan.getGlyTouCanId());
                t_glycanStructure.setGWBSequence(t_glycan.getGws());
                t_glycanList.add(t_glycanStructure);
                this.m_glycanListAll.put(t_glycan.getGlyTouCanId(), t_glycanStructure);
            }
        }
        if (t_glycanList.size() > 0)
        {
            GlycanDatabase t_db = new GlycanDatabase();
            t_db.setName(a_glycanFile.getFileName());
            t_db.setDescription("Generated with glytoucan.registry.client.");
            t_db.setVersion("1.0.0");
            t_db.setStructureCount(t_glycanList.size());
            t_db.setStructures(t_glycanList);
            this.saveGlycanDatabase(t_db,
                    a_outputFolder + File.separator + a_glycanFile.getFileName() + ".xml");
        }
    }
}
