package org.example.server.user.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserUtils {
    private static final Map<String,String> cache = new HashMap<>();

    public static String getNickname(String userId) {
        return Optional.ofNullable(userId).map(i->cache.getOrDefault(i, null)).orElse(null);
    }

    public static void updateCache(Map<String,String> mappings) {
        cache.putAll(mappings);
    }
}
