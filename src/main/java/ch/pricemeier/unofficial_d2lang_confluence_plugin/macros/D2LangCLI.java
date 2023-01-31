package ch.pricemeier.unofficial_d2lang_confluence_plugin.macros;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

public final class D2LangCLI {

    private static final Logger logger = LoggerFactory.getLogger(D2LangCLI.class);

    public static void generateD2Diagram(final Path binaryPath, final Path inputFile, final Path outputFile, final DiagramParameters parameters) {
        try {
            String d2langCLI = constructD2LangCLICommand(binaryPath, inputFile, outputFile, parameters);
            executeD2LangCommand(d2langCLI);
        } catch (IOException | InterruptedException e) {
            logger.error("An error occurred while running the macro", e);
        }
    }

    private static String constructD2LangCLICommand(final Path binaryPath, Path d2InputPath, Path svgOutputPath, final DiagramParameters parameters) {
        String d2langCLI = String.format("%s %s %s", binaryPath, d2InputPath, svgOutputPath);

        logger.info("Using theme: " + parameters.getThemeId());
        d2langCLI += String.format(" -t %s", parameters.getThemeId());

        logger.info("Using layout engine: " + parameters.getLayoutEngine());
        d2langCLI += String.format(" --layout=%s", parameters.getLayoutEngine());

        if (parameters.isSketchMode()) {
            logger.info("Using sketch mode");
            d2langCLI += " --sketch";
        } else {
            logger.info("Not using sketch mode");
        }
        logger.info("Executing: " + d2langCLI);
        return d2langCLI;
    }

    private static void executeD2LangCommand(String d2langCLI) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(d2langCLI);
        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();
        BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
        BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
        String line;
        logger.info("Standard Output:");
        while ((line = stdoutReader.readLine()) != null) {
           logger.info(line);
        }
        logger.debug("Standard Error:");
        while ((line = stderrReader.readLine()) != null) {
            logger.info(line);
        }
        int exitCode = process.waitFor();
        logger.info("Process exit code: " + exitCode);
    }
}
