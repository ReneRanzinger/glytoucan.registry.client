package org.grits.toolbox.glytoucan.registry.client.database;

import java.io.FileNotFoundException;

import org.grits.toolbox.glytoucan.registry.client.io.DatabaseIO;

import jakarta.xml.bind.JAXBException;

public class DatabaseIORun
{

    public static void main(String[] args) throws FileNotFoundException, JAXBException
    {
        DatabaseIO t_dbIO = new DatabaseIO();
        GlycanDatabase t_db = t_dbIO.loadGlycanDatabase("./doc/databases/N-Glycan.xml");

        t_dbIO.saveGlycanDatabase(t_db, "./doc/databases/N-Glycan2.xml");
    }

}
