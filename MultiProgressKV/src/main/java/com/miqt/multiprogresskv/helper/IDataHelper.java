package com.miqt.multiprogresskv.helper;

import java.util.Map;
import java.util.Set;

/**
 * 键值存储帮助类
 */
public interface IDataHelper {
    /**
     * 存储一个键值
     */
    void put(String space, String key, String value, String type);

    /**
     * 获取存储的对象
     */
    Object get(String space, String key, String def, String type);

    /**
     * 删除该kv对
     */
    void remove(String space, String key);

    /**
     * 删除所有
     */
    void removeAll(String space);

    /**
     * 是否包含
     */
    boolean contains(String space, String key);

    /**
     * 取得所有记录的key
     */
    Set<String> keySet(String space);

    /**
     * 取得所有记录的kv对，暂未实现
     */
    Map<String, Object> getAll(String space);
}
