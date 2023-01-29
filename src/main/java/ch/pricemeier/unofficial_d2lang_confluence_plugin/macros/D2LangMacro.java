package ch.pricemeier.unofficial_d2lang_confluence_plugin.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class D2LangMacro implements Macro {

    private final Logger logger = Logger.getLogger(D2LangMacro.class.getName());

    @ComponentImport
    private final AttachmentManager attachmentManager;
    @ComponentImport
    private final SpaceManager spaceManager;

    public D2LangMacro(final AttachmentManager attachmentManager, final SpaceManager spaceManager) {
        this.attachmentManager = attachmentManager;
        this.spaceManager = spaceManager;

    }

    public String execute(Map<String, String> map, String bodyContent, ConversionContext conversionContext) {
        try {
            final String filename = UUID.randomUUID().toString();
            File inputD2File = File.createTempFile(filename, ".d2");
            try (FileWriter writer = new FileWriter(inputD2File)) {
                writer.write(bodyContent);
            }
            File outputSvgFile = File.createTempFile(filename, ".svg");
            final Integer themeId = map.get("themeId") != null ? Integer.parseInt(map.get("themeId")) : null;
            generateD2Diagram(inputD2File.toPath(), outputSvgFile.toPath(), themeId, map.get("customArgs"));
            logger.info("Created file: " + inputD2File.getAbsolutePath());
            Attachment attachment = new Attachment();
            attachment.setFileName(filename + ".svg");
            attachment.setContentType("image/svg+xml");
            attachment.setContainer(conversionContext.getPageContext().getEntity());
            attachment.setFileSize(outputSvgFile.length());
            attachment.setCreator(conversionContext.getEntity().getCreator());
            attachment.setSpace(spaceManager.getSpace(conversionContext.getSpaceKey()));
            try (final InputStream stream = new FileInputStream(outputSvgFile)) {
                attachmentManager.saveAttachment(attachment, null, stream);
            }
            return "<img src='/confluence" + attachment.getDownloadPath() + "'/>";
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while running the macro", e);
            return "Didn't work!";
            // return e.getMessage();
        }
    }

    public BodyType getBodyType() { return BodyType.PLAIN_TEXT; }

    public OutputType getOutputType() { return OutputType.BLOCK; }

    private void generateD2Diagram(final Path inputFile, final Path outputFile, final Integer themeId, final String customArgs) {
        try {
            String d2langCLI = constructD2LangCLICommand(inputFile, outputFile, themeId, customArgs);
            executeD2LangCommand(d2langCLI);
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "An error occurred while running the macro", e);
        }
    }

    private void executeD2LangCommand(String d2langCLI) throws IOException, InterruptedException {
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
        logger.info("Standard Error:");
        while ((line = stderrReader.readLine()) != null) {
            logger.info(line);
        }
        int exitCode = process.waitFor();
        logger.info("Process exit code: " + exitCode);
    }

    private String constructD2LangCLICommand(Path d2InputPath, Path svgOutputPath, final Integer themeId, final String customArgs) {
        String d2langCLI = String.format("d2 %s %s", d2InputPath, svgOutputPath);
        if (themeId != null) {
            logger.info("Using themeId: " + themeId);
            d2langCLI += String.format(" -t %d", themeId);
        }
        if (customArgs != null && customArgs.trim().length() > 0) {
            logger.info("Using customArgs: " + customArgs);
            d2langCLI += String.format(" %s", customArgs);
        }
        logger.info("Executing: " + d2langCLI);
        return d2langCLI;
    }
}