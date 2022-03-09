package org.grits.toolbox.glytoucan.registry.client.ws;

import java.io.IOException;
import java.util.Properties;

import org.grits.toolbox.glytoucan.registry.client.AppRun;
import org.grits.toolbox.glytoucan.registry.client.om.HttpResponseSummary;

public class GlyTouCanRegistryRun
{
    // https://github.com/glygener/glygen-array-backend/blob/main/glygen-array-app/src/main/java/org/glygen/array/util/GlytoucanUtil.java
    public static void main(String[] args) throws IOException
    {
        Properties t_properties = AppRun.loadRegistryProperties();
        String t_glyTouCanApiKey = t_properties.getProperty(AppRun.PROPERTY_KEY_API);
        String t_glyTouCanUserId = t_properties.getProperty(AppRun.PROPERTY_KEY_USER);

        // utility classes
        GlyTouCanApiClient t_client = new GlyTouCanApiClient(t_glyTouCanUserId, t_glyTouCanApiKey);

        // G39326PP
        HttpResponseSummary t_summary = t_client.registerGlycan(
                "WURCS=2.0/4,7,6/[a2112h-1a_1-5_2*NCC/3=O][a5_2*NCC/3=O][a2222h-1b_1-5][a1221m-1a_1-5]/1-2-3-4-2-3-4/a3-b1_a6-e1_b4-c1_c2-d1_e4-f1_f2-g1");
        System.out.println(t_summary.getHttpCode());
        System.out.println(t_summary.getReasonPhrase());
        System.out.println(t_summary.getBody());
        // c458de60c1bf1bc8855b9775a6f1f2d41ba2f8643625e705ba2b132b16cd43c8
    }

}
