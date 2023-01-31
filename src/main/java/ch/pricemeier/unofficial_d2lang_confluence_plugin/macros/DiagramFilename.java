package ch.pricemeier.unofficial_d2lang_confluence_plugin.macros;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class DiagramFilename {
    private static final Logger logger = LoggerFactory.getLogger(DiagramFilename.class);
    private final String nameWithoutExtension;

    public DiagramFilename(final String nameWithoutExtension) {
        this.nameWithoutExtension = nameWithoutExtension;
    }

    public String getNameWithoutExtension() {
        return nameWithoutExtension;
    }

    public File getTempFile(final String extension) {
        try {
            return File.createTempFile(this.nameWithoutExtension, extension);
        } catch (IOException e) {
            logger.error("An error occurred while creating temp file", e);
            throw new RuntimeException(e);
        }
    }

    public static DiagramFilename createWithoutExtension(final String parameterAsString, final String textAsString) {
        return new DiagramFilename(ContentHash.sha256(parameterAsString + "-" + textAsString));
    }
}
