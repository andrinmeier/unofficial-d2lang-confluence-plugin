package ch.pricemeier.unofficial_d2lang_confluence_plugin.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import javax.inject.Named;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


@Named
public class D2LangMacro implements Macro {

    @ComponentImport
    private AttachmentManager attachmentManager;

    @ComponentImport
    private SpaceManager spaceManager;

    @ComponentImport
    private SettingsManager settingsManager;

    public D2LangMacro(final AttachmentManager attachmentManager, final SpaceManager spaceManager, final SettingsManager settingsManager) {
        this.attachmentManager = attachmentManager;
        this.spaceManager = spaceManager;
        this.settingsManager = settingsManager;
    }

    public String execute(Map<String, String> map, String bodyContent, ConversionContext conversionContext) {
        try {
            final String tmpdir = System.getProperty("java.io.tmpdir");
            final Path binaryPath = Paths.get(tmpdir, "d2");
            DiagramBinary.extract("/d2", binaryPath);
            final DiagramParameters parameters = DiagramParameters.fromMap(map);
            final DiagramText text = new DiagramText(bodyContent);
            final DiagramFilename filename = DiagramFilename.createWithoutExtension(parameters.asString(), text.getContent());
            final String svgFilename = filename.getNameWithoutExtension() + ".svg";
            final Attachment existingAttachment = attachmentManager.getAttachment(conversionContext.getPageContext().getEntity(), svgFilename);
            if (existingAttachment != null) {
                final String fullDownloadSvgPath = getFullDownloadSvgPath(existingAttachment);
                return String.format("<img src='%s'/>", fullDownloadSvgPath);
            } else {
                final File d2File = filename.getTempFile(".d2");
                try (FileWriter writer = new FileWriter(d2File)) {
                    writer.write(bodyContent);
                }
                final File svgFile = filename.getTempFile(".svg");
                D2LangCLI.generateD2Diagram(binaryPath, d2File.toPath(), svgFile.toPath(), parameters);
                final Attachment attachment = save(svgFilename, svgFile, conversionContext);
                final String fullDownloadSvgPath = getFullDownloadSvgPath(attachment);
                return String.format("<img src='%s'/>", fullDownloadSvgPath);
            }
        } catch (Exception e) {
            //logger.error("An error occurred while running the macro", e);
            return "Didn't work!";
        }
    }

    private String getFullDownloadSvgPath(Attachment attachment) {
        final String fullDownloadSvgPath = String.format("%s%s", settingsManager.getGlobalSettings().getBaseUrl(), attachment.getDownloadPath());
        return fullDownloadSvgPath;
    }

    private Attachment save(final String svgFilename, final File outputSvgFile, final ConversionContext conversionContext) {
        Attachment attachment = new Attachment();
        attachment.setFileName(svgFilename);
        attachment.setContentType("image/svg+xml");
        attachment.setContainer(conversionContext.getPageContext().getEntity());
        attachment.setFileSize(outputSvgFile.length());
        attachment.setCreator(conversionContext.getEntity().getCreator());
        attachment.setSpace(spaceManager.getSpace(conversionContext.getSpaceKey()));
        try (final InputStream stream = new FileInputStream(outputSvgFile)) {
            attachmentManager.saveAttachment(attachment, null, stream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return attachment;
    }

    public BodyType getBodyType() { return BodyType.PLAIN_TEXT; }

    public OutputType getOutputType() { return OutputType.BLOCK; }
}