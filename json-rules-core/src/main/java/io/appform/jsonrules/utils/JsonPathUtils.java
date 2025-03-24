package io.appform.jsonrules.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import io.appform.jsonrules.config.JsonRulesConfiguration;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonPathUtils {

    public static <T> T read(final JsonNode node, final String path) {
        return JsonPath
                .using(JsonRulesConfiguration.getConfiguration())
                .parse(node)
                .read(path);
    }

    public static <T> T read(final Configuration configuration, final JsonNode node, final String path) {
        return JsonPath
                .using(configuration)
                .parse(node)
                .read(path);
    }
}
