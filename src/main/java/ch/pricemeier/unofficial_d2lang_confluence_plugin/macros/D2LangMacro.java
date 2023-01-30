package ch.pricemeier.unofficial_d2lang_confluence_plugin.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


@Named
public class D2LangMacro implements Macro {

    private final Logger logger = Logger.getLogger(D2LangMacro.class.getName());

    @ComponentImport
    private final AttachmentManager attachmentManager;
    @ComponentImport
    private final SpaceManager spaceManager;

    @ConfluenceImport
    private SettingsManager settingsManager;

    @Inject
    public void setSpaceManager(SettingsManager settingsManager){
        this.settingsManager = settingsManager;
    }

    public D2LangMacro(final AttachmentManager attachmentManager, final SpaceManager spaceManager) {
        this.attachmentManager = attachmentManager;
        this.spaceManager = spaceManager;

    }

    public String execute(Map<String, String> map, String bodyContent, ConversionContext conversionContext) {
        try {
            final String tmpdir = System.getProperty("java.io.tmpdir");
            final Path binaryPath = Paths.get(tmpdir, "d2");
            if (!Files.exists(binaryPath)) {
                InputStream inputStream = getClass().getResourceAsStream("/d2");
                Files.copy(inputStream, binaryPath, StandardCopyOption.REPLACE_EXISTING);
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-xr-x");
                Files.setPosixFilePermissions(binaryPath, permissions);
            }
            final String filename = UUID.randomUUID().toString();
            File inputD2File = File.createTempFile(filename, ".d2");
            try (FileWriter writer = new FileWriter(inputD2File)) {
                writer.write(bodyContent);
            }
            File outputSvgFile = File.createTempFile(filename, ".svg");

            Integer themeId = null;
            if (map.get("themeId") != null) {
                final String themeIdUnparsed = map.get("themeId");
                try {
                    themeId = Integer.parseInt(themeIdUnparsed);
                } catch (NumberFormatException e) {
                    return String.format("The theme id %s is invalid. Please consult https://d2lang.com/tour/themes for a list of valid theme ids.", themeIdUnparsed);
                }
            }

            generateD2Diagram(binaryPath, inputD2File.toPath(), outputSvgFile.toPath(), themeId, map.get("customArgs"));
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
            final String fullDownloadSvgPath = String.format("%s%s", settingsManager.getGlobalSettings().getBaseUrl(), attachment.getDownloadPath());
            return String.format("<img src='%s'/>", fullDownloadSvgPath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred while running the macro", e);
            return "Didn't work!";
            // return e.getMessage();
        }
    }

    public BodyType getBodyType() { return BodyType.PLAIN_TEXT; }

    public OutputType getOutputType() { return OutputType.BLOCK; }

    private void generateD2Diagram(final Path binaryPath, final Path inputFile, final Path outputFile, final Integer themeId, final String customArgs) {
        try {
            String d2langCLI = constructD2LangCLICommand(binaryPath, inputFile, outputFile, themeId, customArgs);
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

    private String constructD2LangCLICommand(final Path binaryPath, Path d2InputPath, Path svgOutputPath, final Integer themeId, final String customArgs) {
        String d2langCLI = String.format("%s %s %s", binaryPath, d2InputPath, svgOutputPath);
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