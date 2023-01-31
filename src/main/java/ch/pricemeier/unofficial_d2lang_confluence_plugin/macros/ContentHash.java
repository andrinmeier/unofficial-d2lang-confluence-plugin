package ch.pricemeier.unofficial_d2lang_confluence_plugin.macros;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ContentHash {

    private static final Logger logger = LoggerFactory.getLogger(ContentHash.class);
    private ContentHash() {
    }

    public static String sha256(final String unhashed) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA256 failed", e);
            throw new RuntimeException(e);
        }
        byte[] hash = messageDigest.digest(unhashed.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(hash);
    }
}
