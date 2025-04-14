package io.appform.jsonrules.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import io.appform.jsonrules.jsonpath.providers.OptimizedJacksonJsonNodeJsonProvider;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

public final class JacksonConfiguration implements Configuration.Defaults {
    @Getter
    private static final JacksonConfiguration instance = new JacksonConfiguration();

    private final JacksonJsonNodeJsonProvider jsonProvider = new OptimizedJacksonJsonNodeJsonProvider(options());
    private final JacksonMappingProvider mappingProvider = new JacksonMappingProvider();

    @Override
    public JsonProvider jsonProvider() {
        return jsonProvider;
    }

    @Override
    public MappingProvider mappingProvider() {
        return mappingProvider;
    }

    @Override
    public Set<Option> options() {
        return EnumSet.noneOf(Option.class);
    }

    public ObjectMapper getObjectMapper() {
        return jsonProvider.getObjectMapper();
    }

    public Configuration getConfiguration() {
        return Configuration.builder()
                .jsonProvider(jsonProvider)
                .mappingProvider(mappingProvider)
                .options(options())
                .build();
    }
}
