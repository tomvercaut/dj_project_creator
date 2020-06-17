package org.tv.project_creator;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class App {

    private static final String OPT_SHORT_OUTPUT_DIR = "d";
    private static final String OPT_OUTPUT_DIR = "dir";


    public static void main(String[] args) {
        var state = parse(args);

    }

    /**
     * Print usage information for the user in case something went wrong. Function closes the application.
     *
     * @param options optional and required commandline options
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        String header = "Create a project for a customer.";
        String footer = "";
        formatter.printHelp("<executable>", header, options, footer, true);
        System.exit(0);
    }

    /**
     * Build the commandline argument options.
     *
     * @return List of commandline argument options.
     */
    private static Options buildOptions() {
        Options options = new Options();
        Option outputDir = Option.builder(OPT_SHORT_OUTPUT_DIR).longOpt(OPT_OUTPUT_DIR).argName("directory").hasArg(true).required(true).numberOfArgs(1).desc("Directory where the project data is written.").build();
        options.addOption(outputDir);
        return options;
    }

    /**
     * Parse and validate the commandline arguments. If an exception is thrown, the program exists and prints the usage information.
     *
     * @param args arguments given at the commandline
     * @return State of the application based on the input on the commandline.
     */
    private static ArgState parse(String[] args) {
        CommandLineParser parser = new DefaultParser();
        var options = buildOptions();
        ArgState state = new ArgState();
        try {
            CommandLine cl = parser.parse(options, args);
            if (cl.hasOption(OPT_SHORT_OUTPUT_DIR)) {
                var path = Paths.get(cl.getOptionValue(OPT_SHORT_OUTPUT_DIR));
                if (!Files.isDirectory(path)) {
                    try {
                        Files.createDirectories(path);
                    } catch (IOException e) {
                        log.error(String.format("Unable to create output directory: {}", path.toAbsolutePath().toString()));
                        System.exit(0);
                    }
                }
                state.setOutputDirectory(path);
            }
        } catch (ParseException e) {
            log.error(e.getMessage());
            printHelp(options);
        }
        return state;
    }
}
