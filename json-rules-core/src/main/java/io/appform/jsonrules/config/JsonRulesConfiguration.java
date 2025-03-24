package io.appform.jsonrules.config;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.cache.CacheProvider;
import io.appform.jsonrules.jsonpath.caches.UnboundedCache;
import lombok.Getter;
import lombok.val;

public class JsonRulesConfiguration {
    @Getter
    private static Configuration configuration;

    static {
        val jacksonConfiguration = JacksonConfiguration.getInstance();
        configuration = new Configuration.ConfigurationBuilder()
                .jsonProvider(jacksonConfiguration.jsonProvider())
                .mappingProvider(jacksonConfiguration.mappingProvider())
                .options(jacksonConfiguration.options())
                .build();
    }

    public static void configure(final PerformanceSafetyPreference performanceSafetyPreference) {
        if (performanceSafetyPreference == PerformanceSafetyPreference.SPEED) {
            CacheProvider.setCache(new UnboundedCache());
        }
        // if performanceSafetyPreference is set to SAFETY, we don't override the cache implementation provided by jsonpath library
    }

    public enum PerformanceSafetyPreference {
        SPEED,
        SAFETY
    }
}
