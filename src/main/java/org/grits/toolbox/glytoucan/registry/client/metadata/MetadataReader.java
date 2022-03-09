package org.grits.toolbox.glytoucan.registry.client.metadata;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

public class MetadataReader
{
    public HashMap<String, Metadata> readMetadata(String a_fileNamePath)
            throws IOException, CsvException
    {
        HashMap<String, Metadata> t_data = new HashMap<>();
        // open the file
        FileReader t_reader = new FileReader(a_fileNamePath);
        // create csvReader object and skip first Line
        CSVReader t_csvReader = new CSVReaderBuilder(t_reader).withSkipLines(1).build();
        List<String[]> t_allData = t_csvReader.readAll();

        // for each row in the file
        for (String[] t_row : t_allData)
        {
            if (t_row.length > 0)
            {
                if (t_row[0].trim().length() > 0)
                {
                    // not an empty line
                    Metadata t_metadata = new Metadata();
                    t_metadata.setTaxon(this.readTaxon(t_row));
                    t_metadata.setSpecies(this.readSpecies(t_row));
                    t_metadata.setUeberon(this.readUeberon(t_row));
                    t_metadata.setTissue(this.readTissue(t_row));
                    t_metadata.setCellLine(this.readCellLine(t_row));
                    t_metadata.setStrain(this.readStrain(t_row));
                    t_data.put(t_row[0].trim(), t_metadata);
                }
            }
        }
        return t_data;
    }

    private String readStrain(String[] a_row)
    {
        if (a_row.length > 6)
        {
            if (a_row[6].trim().length() > 0)
            {
                return a_row[6].trim();
            }
        }
        return null;
    }

    private String readCellLine(String[] a_row)
    {
        if (a_row.length > 5)
        {
            if (a_row[5].trim().length() > 0)
            {
                return a_row[5].trim();
            }
        }
        return null;
    }

    private String readTissue(String[] a_row)
    {
        if (a_row.length > 4)
        {
            if (a_row[4].trim().length() > 0)
            {
                return a_row[4].trim();
            }
        }
        return null;
    }

    private String readUeberon(String[] a_row)
    {
        if (a_row.length > 3)
        {
            if (a_row[3].trim().length() > 0)
            {
                return a_row[3].trim();
            }
        }
        return null;
    }

    private String readSpecies(String[] a_row)
    {
        if (a_row.length > 2)
        {
            if (a_row[2].trim().length() > 0)
            {
                return a_row[2].trim();
            }
        }
        return null;
    }

    private Integer readTaxon(String[] a_row)
    {
        if (a_row.length > 1)
        {
            if (a_row[1].trim().length() > 0)
            {
                return Integer.parseInt(a_row[1].trim());
            }
        }
        return null;
    }
}
