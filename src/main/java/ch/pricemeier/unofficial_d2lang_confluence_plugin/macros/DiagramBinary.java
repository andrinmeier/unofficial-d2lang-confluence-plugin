package ch.pricemeier.unofficial_d2lang_confluence_plugin.macros;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public final class DiagramBinary {
    private static final Logger logger = LoggerFactory.getLogger(DiagramBinary.class);

    private DiagramBinary() {
    }

    public static void extract(final String resourcePath, final Path outputPath) {
        try {
            if (!Files.exists(outputPath)) {
                InputStream inputStream = DiagramBinary.class.getResourceAsStream(resourcePath);
                Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-xr-x");
                Files.setPosixFilePermissions(outputPath, permissions);
            }
        } catch (IOException e) {
            logger.error("An error occurred while extracting d2 binary", e);
            throw new RuntimeException(e);
        }
    }
}
