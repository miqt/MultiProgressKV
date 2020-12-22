package com.miqt.multiprogresskv.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RamHelper implements IDataHelper {

    private final Map<String, Map<String, Object>> mapCache = new HashMap<>();

    private static volatile RamHelper instance;

    private RamHelper() {
    }

    public static RamHelper getInstance() {
        if (instance == null) {
            synchronized (RamHelper.class) {
                if (instance == null) {
                    instance = new RamHelper();
                }
            }
        }
        return instance;
    }

    @Override
    public void put(String space, String key, String value, String type) {
        Map<String, Object> map = mapCache.get(space);
        if (map == null) {
            map = new HashMap<>();
            mapCache.put(space, map);
        }
        map.put(key, value);
    }

    @Override
    public Object get(String space, String key, String def, String type) {
        Map<String, Object> map = mapCache.get(space);
        if (map == null) {
            map = new HashMap<>();
            mapCache.put(space, map);
        }
        return map.get(key);
    }

    @Override
    public void remove(String space, String key) {
        Map<String, Object> map = mapCache.get(space);
        if (map == null) {
            map = new HashMap<>();
            mapCache.put(space, map);
        }
        map.remove(key);
    }

    @Override
    public void removeAll(String space) {
        Map<String, Object> map = mapCache.get(space);
        if (map != null) {
            mapCache.remove(space);
        }
    }

    @Override
    public boolean contains(String space, String key) {
        Map<String, Object> map = mapCache.get(space);
        if (map != null) {
            return map.containsKey(key);
        }
        return false;
    }

    @Override
    public Set<String> keySet(String space) {
        Map<String, Object> map = mapCache.get(space);
        if (map != null) {
            return map.keySet();
        }
        return null;
    }
    @Override
    public Map<String, Object> getAll(String space) {
        return mapCache.get(space);
    }
}
