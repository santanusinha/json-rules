package io.appform.jsonrules.jsonpath.caches;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.cache.Cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UnboundedCache implements Cache  {
    private final Map<String, JsonPath> map = new ConcurrentHashMap<>(1024);

    @Override
    public JsonPath get(final String s) {
        return map.get(s);
    }

    @Override
    public void put(final String s,
                    final JsonPath jsonPath) {
        map.computeIfAbsent(s, k -> jsonPath);
    }
}
