package io.appform.jsonrules.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import io.appform.jsonrules.config.JacksonConfiguration;

import java.util.LinkedHashSet;
import java.util.Set;

public class JsonUtils {

    public static Set<Object> convertToJsonNode(Set<Object> values) {
        if (values.stream().allMatch(value -> value instanceof JsonNode)) {
            return values;
        }
        ObjectMapper mapper = JacksonConfiguration.getInstance().getObjectMapper();
        Set<Object> valuesAsJsonNodes = new LinkedHashSet<>();
        values.forEach(value -> valuesAsJsonNodes.add(mapper.valueToTree(value)));
        return valuesAsJsonNodes;
    }

    public static String convertToString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof TextNode) {
            return ((TextNode) obj).asText();
        }
        return obj.toString();
    }
}
