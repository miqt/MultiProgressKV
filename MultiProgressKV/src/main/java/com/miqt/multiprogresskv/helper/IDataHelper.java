package com.miqt.multiprogresskv.helper;

import java.util.Map;
import java.util.Set;

public interface IDataHelper {
    void put(String space, String key, String value, String type);

    Object get(String space, String key, String def, String type);

    void remove(String space, String key);

    void removeAll(String space);

    boolean contains(String space, String key);

    Set<String> keySet(String space);

    Map<String, Object> getAll(String space);
}
