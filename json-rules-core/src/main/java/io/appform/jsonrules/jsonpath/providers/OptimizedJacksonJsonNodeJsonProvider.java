package io.appform.jsonrules.jsonpath.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.annotations.VisibleForTesting;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import lombok.Getter;

import java.util.Set;

public class OptimizedJacksonJsonNodeJsonProvider extends JacksonJsonNodeJsonProvider {
    @Getter
    private boolean shouldReturnPathAsList;

    public OptimizedJacksonJsonNodeJsonProvider(Set<Option> options) {
        this.shouldReturnPathAsList = options.contains(Option.AS_PATH_LIST);
    }

    @Override
    public void setArrayIndex(Object array, int index, Object newValue) {
        if (!isArray(array)) {
            throw new UnsupportedOperationException();
        } else {
            ArrayNode arrayNode = toJsonArray(array);
            if (index == arrayNode.size()){
                arrayNode.add(createJsonElement(newValue));
            }else {
                arrayNode.set(index, createJsonElement(newValue));
            }
        }
    }

    @VisibleForTesting
    JsonNode createJsonElement(Object o) {
        if (o != null) {
            // jlolling: avoid creating a cloned node: bug #211
            if (o instanceof JsonNode) {
                return (JsonNode) o;
            } else {
                // [PATCHED]
                // Convert the object to a JsonNode only if Option.AS_PATH_LIST is set
                return shouldReturnPathAsList ? objectMapper.valueToTree(o) : null;
            }
        } else {
            return null;
        }
    }

    private ArrayNode toJsonArray(Object o) {
        return (ArrayNode) o;
    }
}
