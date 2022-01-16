package org.grits.toolbox.glytoucan.registry.client.sequence;

import java.io.IOException;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.glycoinfo.WURCSFramework.util.WURCSException;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanInformation;

public class GlycanFormatConverterRun
{
    public static void main(String[] args)
            throws IOException, SugarImporterException, GlycoVisitorException, WURCSException
    {
        GlycanInformation t_glycanInfo = new GlycanInformation();
        t_glycanInfo.setGws(
                "freeEnd--??1D-GlcNAc,p--??1D-GlcNAc,p--??1D-Man,p(--??1D-Man,p--??1D-GlcNAc,p--??1D-Gal,p)--??1D-Man,p--??1D-GlcNAc,p--??1D-Gal,p}--??2D-NeuAc,p$MONO,Und,0,0,freeEnd");
        GlycanFormatConverter t_converter = new GlycanFormatConverter();
        t_converter.gwsToWurcs(t_glycanInfo);

        System.out.println(t_glycanInfo.getGlycoCt());
        System.out.println(t_glycanInfo.getWurcs());
        System.out.println(t_glycanInfo.getError());
        System.out.println(t_glycanInfo.getErrorInfo());

        t_converter.wurcs2GlycoCt(
                "WURCS=2.0/6,12,11/[a2122h-1b_1-5_2*NCC/3=O][a1122h-1b_1-5][a1122h-1a_1-5][a2112h-1b_1-5][Aad21122h-2a_2-6_5*NCC/3=O][a1221m-1a_1-5]/1-1-2-3-1-4-5-3-1-4-5-6/a4-b1_a6-l1_b4-c1_c3-d1_c6-h1_d2-e1_e4-f1_f3-g2_h2-i1_i4-j1_j3-k2");

    }
}
