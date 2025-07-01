package io.appform.jsonrules.config;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.cache.CacheProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import io.appform.jsonrules.jsonpath.caches.UnboundedCache;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class JsonRulesConfiguration {
    @Getter
    private static Configuration configuration;
    private static JacksonConfiguration defaultJacksonConfiguration = JacksonConfiguration.getInstance();

    static {
        synchronized (JsonRulesConfiguration.class) {
            configuration = getConfiguration(
                    defaultJacksonConfiguration.jsonProvider(),
                    defaultJacksonConfiguration.mappingProvider(),
                    defaultJacksonConfiguration.options());
        }
    }

    public static void configure(final PerformanceSafetyPreference performanceSafetyPreference) {
        if (performanceSafetyPreference == PerformanceSafetyPreference.SPEED) {
            log.info("Json Rules configured for speed. Using an unbounded cache for JSONPath evaluations.");
            CacheProvider.setCache(new UnboundedCache());
        }
        // if performanceSafetyPreference is set to SAFETY, we don't override the cache implementation provided by jsonpath library
    }

    // Enable support for complex JSONPath expressions like UDFs, filter expressions, etc.
    // See supported operators here - https://github.com/json-path/JsonPath?tab=readme-ov-file#operators
    // See supported UDFs here - https://github.com/json-path/JsonPath?tab=readme-ov-file#functions
    public static void enableSupportForComplexJsonPathExpressions(final boolean flag) {
        if (flag) {
            log.info("Enabling support for complex JSONPath expressions (UDFs, predicate queries, etc.)");
            synchronized (JsonRulesConfiguration.class) {
                configuration = getConfiguration(
                        new JacksonJsonNodeJsonProvider(),
                        defaultJacksonConfiguration.mappingProvider(),
                        defaultJacksonConfiguration.options());
            }
        }
    }

    public enum PerformanceSafetyPreference {
        SPEED,
        SAFETY
    }

    private static Configuration getConfiguration(final JsonProvider jsonProvider,
                                                  final MappingProvider mappingProvider,
                                                  final Set<Option> options) {
        return new Configuration.ConfigurationBuilder()
                .jsonProvider(jsonProvider)
                .mappingProvider(mappingProvider)
                .options(options)
                .build();
    }
}
