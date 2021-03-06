package org.grits.toolbox.glytoucan.registry.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.grits.toolbox.glytoucan.registry.client.io.DatabaseIO;
import org.grits.toolbox.glytoucan.registry.client.io.ImageWriter;
import org.grits.toolbox.glytoucan.registry.client.om.GlycanFile;
import org.grits.toolbox.glytoucan.registry.client.util.ExcelReport;
import org.grits.toolbox.glytoucan.registry.client.util.Registry;

import jakarta.xml.bind.JAXBException;

public class App
{
    private static final String DATABASE_FOLDER = "GRITS_databases";
    private static final String IMAGE_FOLDER = "images";

    public static void main(String[] a_args)
    {
        // parse the command line arguments and store them
        Options t_options = App.buildComandLineOptions();
        AppArguments t_arguments = App.processCommandlineArguments(a_args, t_options);
        if (t_arguments == null)
        {
            // error messages and command line options have been printed already
            return;
        }
        // create all output folders that are needed
        App.createFolders(t_arguments);
        // utility class
        Registry t_registry = new Registry();
        List<GlycanFile> t_glycanFiles = new ArrayList<>();
        // start workflow from GWS files
        if (t_arguments.getInputFolder() != null)
        {
            // Load GWS files, only GWS format is filled in GlycanInformation
            try
            {
                t_glycanFiles = t_registry.loadGwsFiles(t_arguments.getInputFolder());
            }
            catch (Exception e)
            {
                System.out.println("Unable to read GWS files: " + e.getMessage());
                System.out.println("Please provide valid GWS files.");
                App.printComandParameter(t_options);
                return;
            }
            // Convert all glycans to WURCS
            t_registry.convertToWurcs(t_glycanFiles);
            // Check if glycan exists in GlyTouCan and fill the sequences
            t_registry.findInGlyTouCan(t_glycanFiles, t_arguments.getGlyTouCanUserId(),
                    t_arguments.getGlyTouCanApiKey());
            // Register as needed
            t_registry.registerInGlyTouCan(t_glycanFiles, t_arguments.getGlyTouCanUserId(),
                    t_arguments.getGlyTouCanApiKey());
        }
        // start workflow from existing result file
        if (t_arguments.getInputFile() != null)
        {
            // load from input file
            try
            {
                t_glycanFiles = t_registry.readResultFile(t_arguments.getInputFile());
            }
            catch (Exception e)
            {
                System.out.println("Unable to parse input file: " + e.getMessage());
                App.printComandParameter(t_options);
                return;
            }
            t_registry.updateFromGlyTouCan(t_glycanFiles, t_arguments.getGlyTouCanUserId(),
                    t_arguments.getGlyTouCanApiKey());
        }
        // Write into result file
        try
        {
            t_registry.writeResultFile(t_glycanFiles, t_arguments.getOutputFolder());
        }
        catch (Exception e)
        {
            System.out.println("Error writing the result file: " + e.getMessage());
            System.out.println(
                    "Please ensure the result file is not open by another program and you have write permission to the output folder.");
            e.printStackTrace(System.err);
            return;
        }
        // Write into report file
        try
        {
            ExcelReport t_excelReportGenerator = new ExcelReport();
            t_excelReportGenerator.writeReport(t_glycanFiles, t_arguments.getOutputFolder());
        }
        catch (Exception e)
        {
            System.out.println("Error writing the Excel file: " + e.getMessage());
            System.out.println(
                    "Please ensure the result file is not open by another program and you have write permission to the output folder.");
            e.printStackTrace(System.err);
            return;
        }
        // write images
        try
        {
            ImageWriter t_writer = new ImageWriter();
            t_writer.writeImages(t_glycanFiles,
                    t_arguments.getOutputFolder() + File.separator + App.IMAGE_FOLDER);
        }
        catch (Exception e)
        {
            System.out.println("Error writing the image files: " + e.getMessage());
            System.out.println(
                    "Please ensure the result file is not open by another program and you have write permission to the output folder.");
            e.printStackTrace(System.err);
            return;
        }
        // write database
        DatabaseIO t_databaseIO = new DatabaseIO();
        try
        {
            t_databaseIO.writeDatabases(t_glycanFiles,
                    t_arguments.getOutputFolder() + File.separator + App.DATABASE_FOLDER);
        }
        catch (JAXBException e)
        {
            System.out.println("Error writing GRITS databases: " + e.getMessage());
            System.out.println(
                    "Please ensure the result file is not open by another program and you have write permission to the output folder.");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Process the command line options and create the AppArgument object.
     *
     * If the processing failed the error messages and command line options have
     * been printed.
     *
     * @param a_args
     *            Command line arguments given by the user
     * @param a_options
     *            Configuration object for the command line parameters
     * @return AppArguments object with the extracted command line options or
     *         NULL if parsing/validation failed. In this case error messages
     *         and valid command line options have been printed to System.out.
     */
    private static AppArguments processCommandlineArguments(String[] a_args, Options a_options)
    {
        // initialize the arguments from command line
        AppArguments t_arguments = null;
        try
        {
            t_arguments = App.parseArguments(a_args, a_options);
            if (t_arguments == null)
            {
                // failed, message was printed, time to go
                App.printComandParameter(a_options);
                return null;
            }
        }
        catch (ParseException e)
        {
            System.out.println("Invalid commandline arguments: " + e.getMessage());
            App.printComandParameter(a_options);
            return null;
        }
        catch (Exception e)
        {
            System.out.println(
                    "There was an error processing the command line arguments: " + e.getMessage());
            App.printComandParameter(a_options);
            return null;
        }
        return t_arguments;
    }

    /**
     * Create additional folders inside the output folder for images and GRITS
     * database
     *
     * @param a_arguments
     *            Command line parameter object
     */
    private static void createFolders(AppArguments a_arguments)
    {
        // Database folder
        File t_folder = new File(
                a_arguments.getOutputFolder() + File.separator + App.DATABASE_FOLDER);
        t_folder.mkdirs();
        // Image folder
        t_folder = new File(a_arguments.getOutputFolder() + File.separator + App.IMAGE_FOLDER);
        t_folder.mkdirs();
    }

    /**
     * Parse the command line parameters or load the values from a properties
     * file. Values are validated as well.
     *
     * @param a_args
     *            Command line parameters handed down to the application.
     * @return Validated parameter object or null if loading/validation fails.
     *         In that case corresponding error message are printed to console.
     * @throws ParseException
     *             Thrown if the command line parsing fails
     */
    private static AppArguments parseArguments(String[] a_args, Options a_options)
            throws ParseException
    {
        // create the command line parser
        CommandLineParser t_parser = new DefaultParser();
        // parse the command line arguments
        CommandLine t_commandLine = t_parser.parse(a_options, a_args);
        AppArguments t_arguments = new AppArguments();
        // overwrite from arguments
        t_arguments.setGlyTouCanUserId(t_commandLine.getOptionValue("u"));
        t_arguments.setGlyTouCanApiKey(t_commandLine.getOptionValue("k"));
        t_arguments.setInputFolder(t_commandLine.getOptionValue("i"));
        t_arguments.setInputFile(t_commandLine.getOptionValue("f"));
        t_arguments.setOutputFolder(t_commandLine.getOptionValue("o"));
        // check settings
        if (!App.checkArguments(t_arguments))
        {
            return null;
        }
        return t_arguments;
    }

    /**
     * Check the command line arguments.
     *
     * @param a_arguments
     *            Argument object filled with the parsed command line parameters
     * @return TRUE if the parameters are valid. FALSE if at least one parameter
     *         is incorrect. In that case a message is printed to System.out
     */
    private static boolean checkArguments(AppArguments a_arguments)
    {
        boolean t_valid = true;
        if (a_arguments.getInputFile() != null && a_arguments.getInputFolder() == null)
        {
            // input file must exist
            File t_file = new File(a_arguments.getInputFile());
            if (t_file.exists())
            {
                if (t_file.isDirectory())
                {
                    System.out.println("Input file  (-f) can not be a directory.");
                    t_valid = false;
                }
            }
            else
            {
                System.out.println("Input file (-f) does not exist.");
                t_valid = false;
            }
        }
        else if (a_arguments.getInputFile() == null && a_arguments.getInputFolder() != null)
        {
            // Input folder needs to be an existing folder in the file system
            File t_file = new File(a_arguments.getInputFolder());
            if (t_file.exists())
            {
                if (!t_file.isDirectory())
                {
                    System.out.println("Input folder (-i) is not a directory.");
                    t_valid = false;
                }
            }
            else
            {
                System.out.println("Input folder (-i) does not exist.");
                t_valid = false;
            }
        }
        else if (a_arguments.getInputFile() == null && a_arguments.getInputFolder() == null)
        {
            System.out
                    .println("One of the Options -f or -i must be given to run the registration.");
            t_valid = false;
        }
        else
        {
            System.out.println("Options -f and -i can not be used together.");
            t_valid = false;
        }
        // output folder
        File t_file = new File(a_arguments.getOutputFolder());
        if (!t_file.exists())
        {
            if (!t_file.mkdirs())
            {
                System.out.println("Unable to create output folder.");
                t_valid = false;
            }
        }
        // check user and API key
        if (a_arguments.getGlyTouCanApiKey() == null)
        {
            System.out.println("GlyTouCan API key (-k) is required.");
            t_valid = false;
        }
        if (a_arguments.getGlyTouCanUserId() == null)
        {
            System.out.println("GlyTouCan user ID (-u) is required.");
            t_valid = false;
        }
        return t_valid;
    }

    /**
     * Print out the command line parameter.
     */
    private static void printComandParameter(Options a_options)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("<command> -u <user> -k <api key> -i <input folder> -o <output folder>",
                a_options);
    }

    /**
     * Build the command line argument object that contains all options
     *
     * @return Object with the command line options
     */
    private static Options buildComandLineOptions()
    {
        // create the Options
        Options t_options = new Options();
        // GlyTouCan user ID
        Option t_option = new Option("u", "user", true,
                "GlyTouCan user ID that has permission to use API.");
        t_option.setArgs(1);
        t_option.setRequired(true);
        t_options.addOption(t_option);
        // GlyTouCan API key
        t_option = new Option("k", "key", true,
                "API key for this GlyTouCan user from GlyTouCan webpage.");
        t_option.setArgs(1);
        t_option.setRequired(true);
        t_options.addOption(t_option);
        // input folder
        t_option = new Option("i", "input", true, "Input folder with the GWS files.");
        t_option.setArgs(1);
        t_option.setRequired(false);
        t_options.addOption(t_option);
        // input file
        t_option = new Option("f", "file", true,
                "A registry result file to rerun the program and update missing accessions.");
        t_option.setArgs(1);
        t_option.setRequired(false);
        t_options.addOption(t_option);
        // output folder
        t_option = new Option("o", "output", true,
                "Output folder that will be used to store the output.");
        t_option.setArgs(1);
        t_option.setRequired(true);
        t_options.addOption(t_option);

        return t_options;
    }
}
