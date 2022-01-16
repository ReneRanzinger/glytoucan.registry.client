package org.grits.toolbox.glytoucan.registry.client.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.grits.toolbox.glytoucan.registry.client.om.GlycanFile;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanInformation;

public class FolderProcessor
{

    public List<GlycanFile> loadFiles(String a_inputFolder) throws IOException
    {
        List<GlycanFile> t_fileList = new ArrayList<>();
        // find all files in the folder
        File t_folder = new File(a_inputFolder);
        String[] t_files = t_folder.list();
        // load the file content
        for (String t_fileName : t_files)
        {
            // check for gws file
            if (t_fileName.endsWith("gws")
                    && this.isFile(a_inputFolder + File.separator + t_fileName))
            {
                // create and file glycan file object
                GlycanFile t_glycanFile = new GlycanFile();
                t_glycanFile.setFileName(t_fileName);
                t_fileList.add(t_glycanFile);
                // load the file content
                String t_fileContent = this
                        .loadFileContent(a_inputFolder + File.separator + t_fileName);
                // split the sequences and create objects
                String[] t_sequences = t_fileContent.split(";");
                for (String t_sequence : t_sequences)
                {
                    GlycanInformation t_glycan = new GlycanInformation();
                    t_glycan.setGws(t_sequence.trim());
                    t_glycanFile.getGlycans().add(t_glycan);
                }
            }
        }
        return t_fileList;
    }

    private boolean isFile(String a_filePath)
    {
        File t_file = new File(a_filePath);
        return t_file.isFile();
    }

    private String loadFileContent(String a_filePath) throws IOException
    {
        Path t_filePath = Path.of(a_filePath);
        return Files.readString(t_filePath);
    }

}
