package org.grits.toolbox.glytoucan.registry.client.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.grits.toolbox.glytoucan.registry.client.om.GlycanFile;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanInformation;

public class ImageWriter
{
    public void writeImages(List<GlycanFile> a_glycanFiles, String a_folderPath) throws IOException
    {
        for (GlycanFile t_glycanFile : a_glycanFiles)
        {
            for (GlycanInformation t_glycan : t_glycanFile.getGlycans())
            {
                if (!t_glycan.isFailed() && t_glycan.getGlyTouCanId() != null
                        && t_glycan.getImageWURCS() != null)
                {
                    Path t_path = Paths.get(
                            a_folderPath + File.separator + t_glycan.getGlyTouCanId() + ".jpg");
                    Files.write(t_path, t_glycan.getImageWURCS().getImageData());
                }
            }
        }
    }

}
