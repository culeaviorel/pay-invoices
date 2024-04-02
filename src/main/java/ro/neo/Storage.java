package ro.neo;

import java.util.HashMap;
import java.util.Map;

public class  Storage {

    private Storage() {
    }

    private static final Map<String, Object> cache = new HashMap<>();

    public static <T> T get(String key) {
        return (T) cache.get(key);
    }

    public static void set(String key, Object value) {
        cache.put(key, value);
    }

    public static Map<String, Object> getCache() {
        return cache;
    }
}
