package org.grits.toolbox.glytoucan.registry.client.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eurocarbdb.application.glycanbuilder.BuilderWorkspace;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.massutil.MassOptions;
import org.eurocarbdb.application.glycanbuilder.renderutil.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.util.GraphicOptions;
import org.glycoinfo.application.glycanbuilder.converterWURCS2.WURCS2Parser;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanInformation;
import org.grits.toolbox.glytoucan.registry.client.om.ImageInformation;

public class ImageGeneration
{
    private static final Double IMAGE_SCALING_FACTOR = 1D;
    private static BuilderWorkspace glycanWorkspace = new BuilderWorkspace(new GlycanRendererAWT());
    static
    {
        glycanWorkspace.initData();
        // Set orientation of glycan: RL - right to left, LR - left to right, TB
        // - top to bottom, BT - bottom to top
        glycanWorkspace.getGraphicOptions().ORIENTATION = GraphicOptions.RL;
        // Set flag to show information such as linkage positions and anomers
        glycanWorkspace.getGraphicOptions().SHOW_INFO = true;
        // Set flag to show mass
        glycanWorkspace.getGraphicOptions().SHOW_MASSES = false;
        // Set flag to show reducing end
        glycanWorkspace.getGraphicOptions().SHOW_REDEND = true;

        glycanWorkspace.setDisplay(GraphicOptions.DISPLAY_NORMALINFO);
        glycanWorkspace.setNotation(GraphicOptions.NOTATION_SNFG);
    }

    public void createImages(GlycanInformation a_glycan)
    {
        try
        {
            ImageInformation t_image = this.createImageFromGWS(a_glycan.getGws(),
                    GraphicOptions.NOTATION_CFG);
            a_glycan.setImageGWS(t_image);
        }
        catch (Exception e)
        {
            a_glycan.addWarnings("Unable to generate original GWS image: " + e.getMessage());
        }
        try
        {
            if (a_glycan.getGlycoCtRecoded() != null)
            {
                ImageInformation t_image = this
                        .createImageFromGlycoCT(a_glycan.getGlycoCtRecoded());
                a_glycan.setImageGlycoCT(t_image);
            }
            else if (a_glycan.getGlycoCt() != null)
            {
                ImageInformation t_image = this.createImageFromGlycoCT(a_glycan.getGlycoCt());
                a_glycan.setImageGlycoCT(t_image);
            }
        }
        catch (Exception e)
        {
            a_glycan.addWarnings("Unable to generate original GlycoCT image: " + e.getMessage());
        }
        try
        {
            if (a_glycan.getWurcs() != null)
            {
                ImageInformation t_image = this.createImageFromWURCS(a_glycan.getWurcs());
                a_glycan.setImageWURCS(t_image);
            }
        }
        catch (Exception e)
        {
            a_glycan.addWarnings("Unable to generate original WURCS image: " + e.getMessage());
        }
        try
        {
            if (a_glycan.getWurcsAfterRegistration() != null)
            {
                ImageInformation t_image = this
                        .createImageFromWURCS(a_glycan.getWurcsAfterRegistration());
                a_glycan.setImageWURCSAfterRegistration(t_image);
            }
        }
        catch (Exception e)
        {
            a_glycan.addWarnings(
                    "Unable to generate WURCS from GlyTouCan image: " + e.getMessage());
        }
        try
        {
            if (a_glycan.getGlycoCtAfterRegistration() != null)
            {
                ImageInformation t_image = this
                        .createImageFromGlycoCT(a_glycan.getGlycoCtAfterRegistration());
                a_glycan.setImageGlycoCTAfterRegistration(t_image);
            }
        }
        catch (Exception e)
        {
            a_glycan.addWarnings(
                    "Unable to generate GlycoCT based on GlyTouCan image: " + e.getMessage());
        }
        try
        {
            if (a_glycan.getGwsAfterRegistration() != null)
            {
                ImageInformation t_image = this.createImageFromGWS(
                        a_glycan.getGwsAfterRegistration(), GraphicOptions.NOTATION_SNFG);
                a_glycan.setImageGWSAfterRegistration(t_image);
            }
        }
        catch (Exception e)
        {
            a_glycan.addWarnings(
                    "Unable to generate GWS based on GlyTouCan image: " + e.getMessage());
        }
    }

    private ImageInformation createImageFromWURCS(String a_wurcs) throws Exception
    {
        ImageInformation t_image = null;
        Glycan glycanObject = null;
        WURCS2Parser t_wurcsparser = new WURCS2Parser();
        glycanObject = t_wurcsparser.readGlycan(a_wurcs, new MassOptions());
        if (glycanObject != null)
        {
            t_image = this.createImageForGlycan(glycanObject);
        }
        return t_image;
    }

    private ImageInformation createImageFromGlycoCT(String a_glycoCt) throws IOException
    {
        ImageInformation t_image = null;
        Glycan glycanObject = Glycan.fromGlycoCTCondensed(a_glycoCt);
        if (glycanObject != null)
        {
            t_image = this.createImageForGlycan(glycanObject);
        }
        return t_image;
    }

    private ImageInformation createImageFromGWS(String a_gws, String a_notation) throws IOException
    {
        ImageInformation t_image = null;
        Glycan glycanObject = Glycan.fromString(a_gws);
        if (glycanObject != null)
        {
            t_image = this.createImageForGlycan(glycanObject, a_notation);
        }
        return t_image;
    }

    private ImageInformation createImageForGlycan(Glycan a_glycan, String a_notation)
            throws IOException
    {
        ImageInformation t_imageInfo = new ImageInformation();
        boolean t_showMassInformation = false;
        boolean t_showReducingEnd = true;
        ImageGeneration.glycanWorkspace.setNotation(a_notation);
        BufferedImage t_imageBufferd = ImageGeneration.glycanWorkspace.getGlycanRenderer().getImage(
                a_glycan, true, t_showMassInformation, t_showReducingEnd, IMAGE_SCALING_FACTOR);
        byte[] t_imageData = this.imageToBytes(t_imageBufferd);
        t_imageInfo.setImage(t_imageBufferd);
        t_imageInfo.setImageData(t_imageData);
        return t_imageInfo;
    }

    private ImageInformation createImageForGlycan(Glycan a_glycan) throws IOException
    {
        return this.createImageForGlycan(a_glycan, GraphicOptions.NOTATION_SNFG);
    }

    private byte[] imageToBytes(BufferedImage a_image) throws IOException
    {
        ByteArrayOutputStream t_byteArrayStream = new ByteArrayOutputStream();
        ImageIO.write(a_image, "jpg", t_byteArrayStream);
        return t_byteArrayStream.toByteArray();
    }
}