package io.appform.jsonrules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.jsonrules.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test to check rule functionality
 */
public class RuleTest {
    final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testRule() throws Exception {
        final String ruleRepr = TestUtils.read("/simple.rule");
        Rule rule = Rule.create(ruleRepr, mapper);
        JsonNode node = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\" }");
        Assert.assertTrue(rule.matches(node));
    }

}