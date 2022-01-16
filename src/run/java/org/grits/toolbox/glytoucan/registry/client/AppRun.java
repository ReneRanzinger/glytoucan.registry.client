package org.grits.toolbox.glytoucan.registry.client;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class AppRun
{
    /**
     * Keys used in the properties file
     */
    public static final String PROPERTY_KEY_USER = "glytoucan_user";
    public static final String PROPERTY_KEY_API = "glytoucan_api_key";
    private static final String PROPERTY_KEY_INPUT = "input_folder";
    private static final String PROPERTY_KEY_OUTPUT = "output_folder";

    public static void main(String[] args) throws IOException
    {
        Properties t_properties = AppRun.loadRegistryProperties();
        String t_comandLine = "-u " + t_properties.getProperty(AppRun.PROPERTY_KEY_USER) + " -k "
                + t_properties.getProperty(AppRun.PROPERTY_KEY_API) + " -i "
                + t_properties.getProperty(AppRun.PROPERTY_KEY_INPUT) + " -o "
                + t_properties.getProperty(AppRun.PROPERTY_KEY_OUTPUT);
/*
        t_comandLine = "-u " + t_properties.getProperty(AppRun.PROPERTY_KEY_USER) + " -k "
                + t_properties.getProperty(AppRun.PROPERTY_KEY_API)
                + " -f D:\\Java\\workspace-GlyGen\\glytoucan.registry.client\\data\\test\\glytoucan-registry-result.json"
                + " -o " + t_properties.getProperty(AppRun.PROPERTY_KEY_OUTPUT);
*/
        System.out.println(t_comandLine);
        String[] t_args = t_comandLine.split(" ");
        App.main(t_args);
    }

    /**
     * Load the input parameter from a properties file.
     *
     * @return Properties object with the values from the file
     * @throws Exception
     *             If the loading of the properties file fails
     */
    public static Properties loadRegistryProperties() throws IOException
    {
        // open the file
        FileReader t_reader = new FileReader("registry.properties");
        // read properties
        Properties t_properties = new Properties();
        t_properties.load(t_reader);
        // close file
        t_reader.close();
        return t_properties;
    }
}
