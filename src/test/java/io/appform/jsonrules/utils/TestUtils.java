package io.appform.jsonrules.utils;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utilities required for testing
 */
public class TestUtils {
    public static String read(final String resource) throws Exception {
        return new String(Files.readAllBytes(Paths.get(TestUtils.class.getResource(resource).toURI())));
    }
}
