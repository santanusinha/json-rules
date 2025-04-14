package io.appform.jsonrules.jsonpath.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableSet;
import com.jayway.jsonpath.Option;
import lombok.val;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OptimizedJacksonJsonNodeJsonProviderTest {

    private OptimizedJacksonJsonNodeJsonProvider provider;
    private ObjectMapper objectMapper;

    public OptimizedJacksonJsonNodeJsonProviderTest() {
        objectMapper = mock(ObjectMapper.class);
        provider = new OptimizedJacksonJsonNodeJsonProvider(ImmutableSet.of(Option.AS_PATH_LIST));
    }

    @Test
    public void testConstructorWithOptionAsPathList() {
        provider = new OptimizedJacksonJsonNodeJsonProvider(ImmutableSet.of(Option.AS_PATH_LIST));
        assertTrue(provider.isShouldReturnPathAsList());
    }

    @Test
    public void testConstructorWithoutOptionAsPathList() {
        provider = new OptimizedJacksonJsonNodeJsonProvider(Collections.emptySet());
        assertFalse(provider.isShouldReturnPathAsList());
    }

    @Test
    public void testSetArrayIndexWithNonArrayThrowsException() {
        try {
            provider.setArrayIndex("notAnArray", 0, "value");
            fail("Expected UnsupportedOperationException to be thrown");
        } catch (UnsupportedOperationException e) {
            // Test passes
        }
    }

    @Test
    public void testSetArrayIndexWithValidArrayAndIndex() {
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.size()).thenReturn(1);

        provider.setArrayIndex(arrayNode, 0, "value");

        verify(arrayNode).set(eq(0), any(JsonNode.class));
    }

    @Test
    public void testSetArrayIndexWithValidArrayAndIndexEqualToSize() {
        ArrayNode arrayNode = mock(ArrayNode.class);
        when(arrayNode.size()).thenReturn(1);

        provider.setArrayIndex(arrayNode, 1, "value");

        verify(arrayNode).add(any(JsonNode.class));
    }

    @Test
    public void testCreateJsonElementWithNullValue() {
        JsonNode result = provider.createJsonElement(null);
        assertNull(result);
    }

    @Test
    public void testCreateJsonElementWithJsonNode() {
        JsonNode jsonNode = mock(JsonNode.class);
        JsonNode result = provider.createJsonElement(jsonNode);
        assertSame(jsonNode, result);
    }

    @Test
    public void testCreateJsonElementWithNonJsonNode() {
        provider = new OptimizedJacksonJsonNodeJsonProvider(ImmutableSet.of());
        Object value = "testValue";

        JsonNode result = provider.createJsonElement(value);
        assertNull(result);
        verifyNoInteractions(objectMapper);

        provider = new OptimizedJacksonJsonNodeJsonProvider(ImmutableSet.of(Option.AS_PATH_LIST));
        val mockNode = mock(JsonNode.class);
        when(objectMapper.valueToTree(value)).thenReturn(mockNode);
        when(mockNode.toString()).thenReturn("\"testValue\"");

        JsonNode result1 = provider.createJsonElement(value);
        assertNotNull(result1);
        assertEquals(mockNode.toString(), result1.toString());
    }
}