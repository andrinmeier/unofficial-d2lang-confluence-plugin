package ch.pricemeier.unofficial_d2lang_confluence_plugin.macros;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class DiagramFilename {
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
            // logger.error("An error occurred while creating temp file", e);
            throw new RuntimeException(e);
        }
    }

    public static DiagramFilename createWithoutExtension(final String parameterAsString, final String textAsString) {
        try {
            return new DiagramFilename(URLEncoder.encode(ContentHash.sha256(parameterAsString + "-" + textAsString), StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
