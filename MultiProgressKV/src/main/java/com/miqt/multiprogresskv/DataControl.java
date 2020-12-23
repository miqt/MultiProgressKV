package com.miqt.multiprogresskv;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Copyright © 2020 MiQt Inc. All rights reserved.
 * @Description: 多进程内存存储 kv
 * @Version: 1.0
 * @CreateDate: 2020/11/2 18:42
 * @Author: miqt
 * @mail: miqtdev@163.com
 */
public class DataControl {

    final SaveType mSaveTye;
    final String mSpace;
    final ContentResolver mResolver;
    final Uri mUri;

    public DataControl(Context context) {
        this(context, "def", SaveType.SP);
    }

    public DataControl(Context context, String space) {
        this(context, space, SaveType.SP);
    }

    public DataControl(Context context, SaveType type) {
        this(context, "def", type);
    }

    /**
     * 构建数据读写工具
     *
     * @param space 命名空间，用于键值对隔离
     * @param type  存储类型
     */
    public DataControl(Context context, String space, SaveType type) {
        if (TextUtils.isEmpty(space)) {
            this.mSpace = "null";
        } else {
            this.mSpace = space;
        }
        this.mResolver = context.getContentResolver();
        this.mSaveTye = type;
        this.mUri = Uri.parse("content://" + context.getPackageName() + ".DataContentProvider" + type.path);
    }

    private <T> T get(String key, T defValue, Class<T> type) {
        if (key == null) {
            return defValue;
        }
        if (type == null) {
            return defValue;
        }
        String[] projection = new String[4];
        projection[0] = mSpace;
        projection[1] = key;
        projection[2] = type.getName();
        projection[3] = defValue != null ? String.valueOf(defValue) : null;
        try (Cursor cursor = mResolver.query(mUri, projection, null, null, null, null)) {
            if (cursor == null) {
                return defValue;
            }
            if (cursor.getCount() <= 0) {
                return defValue;
            }
            if (cursor.moveToPosition(0)) {
                String values = cursor.getString(0);
                if (type == String.class) {
                    return (T) values;
                }
                return (T) type.getMethod("valueOf", new Class[]{String.class}).invoke(null, values);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defValue;
    }

    private <T> void put(String key, T value, Class<T> type) {
        if (key == null) {
            return;
        }
        try {
            String[] args = new String[4];
            args[0] = mSpace;
            args[1] = key;
            args[2] = String.valueOf(value);
            args[3] = type.getName();
            //DB 方式单行存储超过 2M 会引发容量异常
            if (mSaveTye == SaveType.DB && args[2].length() >= 2048 * 1024) {
                return;
            }
            mResolver.update(mUri, null, null, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否包含指定键值对
     * @param key Key
     * @return true 包含
     */
    public boolean contains(String key) {
        Bundle extras = new Bundle();
        extras.putString("space", mSpace);
        extras.putString("key", key);
        return mResolver.call(mUri, "contains", mSaveTye.path, extras) != null;
    }
    /**
     * 设置对应类型的数据
     * @param key Key
     * @param value 默认值
     */
    public <T> void putEntity(String key, T value) {
        if (value == null) {
            return;
        }
        Field[] fields = value.getClass().getDeclaredFields();
        Map<String, Object> map = new HashMap<>();
        for (Field field : fields) {
            if (field == null) {
                continue;
            }
            if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                continue;
            }
            String name = field.getName();
            Object object;
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                object = field.get(value);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (object == null) {
                continue;
            }
            if (isAllowType(object))
                map.put(name, object);
        }
        putMap(key, map);
    }

    private boolean isAllowType(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof JSONArray || o instanceof JSONObject) {
            return true;
        }
        try {
            if (o instanceof List) {
                return ((List) o).size()>0;
            }
            if (o instanceof Map) {
                return ((Map) o).size()>0;
            }
            if (o instanceof Boolean ||
                    o instanceof Byte ||
                    o instanceof Character ||
                    o instanceof Double ||
                    o instanceof Float ||
                    o instanceof Integer ||
                    o instanceof Long ||
                    o instanceof Short ||
                    o instanceof String) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }
    /**
     * 获取对应类型的数据
     * @param key Key
     * @param defValue 默认值
     * @return Value
     */
    public <T> T getEntity(String key, T defValue, Class<T> valueType) {
        T tmp = null;
        try {
            tmp = valueType.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return getEntity(key, defValue, tmp);

    }
    /**
     * 获取对应类型的数据
     * @param key Key
     * @param defValue 默认值
     * @return Value
     */
    public <T> T getEntity(String key, T defValue, T tmp) {
        if (tmp == null) {
            return defValue;
        }
        Map<String, Object> map = getMap(key);
        if (map == null || map.isEmpty()) {
            return defValue;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry == null) {
                continue;
            }
            String name = entry.getKey();
            Object value = entry.getValue();
            if (TextUtils.isEmpty(name))
                continue;
            if (value == null)
                continue;
            Field field;
            try {
                field = tmp.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                continue;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                field.set(tmp, tryCase(value, field.getType()));
            } catch (IllegalAccessException ignored) {

            }
        }
        return tmp;
    }

    private Object tryCase(java.lang.Object value, Class<?> type) {
        if (Float.class.isAssignableFrom(type)||float.class.isAssignableFrom(type)) {
            return Float.valueOf(String.valueOf(value));
        }
        if (Double.class.isAssignableFrom(type)||double.class.isAssignableFrom(type)) {
            return Double.valueOf(String.valueOf(value));
        }
        if (char.class.isAssignableFrom(type)||Character.class.isAssignableFrom(type)) {
            return ((String) value).toCharArray()[0];
        }
        if (short.class.isAssignableFrom(type)||Short.class.isAssignableFrom(type)) {
            return Short.valueOf(String.valueOf(value));
        }
        if (long.class.isAssignableFrom(type)||Long.class.isAssignableFrom(type)) {
            return Long.valueOf(String.valueOf(value));
        }
        if (byte.class.isAssignableFrom(type)||Byte.class.isAssignableFrom(type)) {
            return Byte.valueOf(String.valueOf(value));
        }
        if (boolean.class.isAssignableFrom(type)||Boolean.class.isAssignableFrom(type)) {
            return Boolean.valueOf(String.valueOf(value));
        }
        if (JSONObject.class.isAssignableFrom(type)) {
            return new JSONObject((Map<String,Object>) value);
        }
        if (JSONArray.class.isAssignableFrom(type)) {
            return new JSONArray((List<Object>) value);
        }


        return value;
    }
    /**
     * 设置对应类型的数据
     * @param key Key
     * @param value 默认值
     */
    public void putCollection(String key, Collection<Object> value) {
        if (key == null) {
            return;
        }
        try {
            String[] args = new String[4];
            args[0] = mSpace;
            args[1] = key;
            args[2] = new JSONArray(value).toString();
            args[3] = String.class.getName();
            mResolver.update(mUri, null, null, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取对应类型的数据
     * @param key Key
     * @return Value
     */
    public Map<String, Object> getMap(String key) {
        String json = get(key, "", String.class);
        try {
            if (TextUtils.isEmpty(json)) {
                return null;
            }
            return json2Map(new JSONObject(json));
        } catch (Throwable ignored) {

        }
        return null;
    }

    private Collection<Object> json2Array(JSONArray array) {
        Collection<Object> arrayList = new ArrayList<>();
        int length = array.length();
        for (int i = 0; i < length; i++) {
            Object next = array.opt(i);
            if (next instanceof JSONArray) {
                arrayList.add(json2Array((JSONArray) next));
            } else if (next instanceof JSONObject) {
                arrayList.add(json2Map((JSONObject) next));
            } else {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    private Map<String, Object> json2Map(JSONObject object) {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> iterator = object.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = object.opt(key);
            if (value instanceof JSONArray) {
                map.put(key, json2Array((JSONArray) value));
            } else if (value instanceof JSONObject) {
                map.put(key, json2Map((JSONObject) value));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }
    /**
     * 获取对应类型的数据
     * @param key Key
     * @return Value
     */
    public Collection<Object> getCollection(String key) {
        String json = get(key, "", String.class);
        try {
            if (TextUtils.isEmpty(json)) {
                return null;
            }
            return json2Array(new JSONArray(json));
        } catch (Throwable ignored) {

        }
        return null;
    }
    /**
     * 设置对应类型的数据
     * @param key Key
     * @param value 默认值
     */
    public void putMap(String key, Map<String, Object> value) {
        if (key == null) {
            return;
        }
        try {
            String[] args = new String[4];
            args[0] = mSpace;
            args[1] = key;
            args[2] = new JSONObject(value).toString();
            args[3] = String.class.getName();
            mResolver.update(mUri, null, null, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    /**
     * 设置对应类型的数据
     * @param key Key
     */
    public void putInt(String key, int value) {
        put(key, value, Integer.class);
    }
    /**
     * 设置对应类型的数据
     * @param key Key
     */
    public void putBool(String key, boolean value) {
        put(key, value, Boolean.class);
    }
    /**
     * 设置对应类型的数据
     * @param key Key
     */
    public void putLong(String key, long value) {
        put(key, value, Long.class);
    }
    /**
     * 设置对应类型的数据
     * @param key Key
     */
    public void putFloat(String key, float value) {
        put(key, value, Float.class);
    }
    /**
     * 设置对应类型的数据
     * @param key Key
     */
    public void putString(String key, String value) {
        put(key, value, String.class);
    }
    /**
     * 获取对应类型的数据
     * @param key Key
     * @param def 默认值
     * @return Value
     */
    public int getInt(String key, int def) {
        return get(key, def, Integer.class);
    }
    /**
     * 获取对应类型的数据
     * @param key Key
     * @param def 默认值
     * @return Value
     */
    public boolean getBool(String key, boolean def) {
        return get(key, def, Boolean.class);
    }
    /**
     * 获取对应类型的数据
     * @param key Key
     * @param def 默认值
     * @return Value
     */
    public long getLong(String key, long def) {
        return get(key, def, Long.class);
    }
    /**
     * 获取对应类型的数据
     * @param key Key
     * @param def 默认值
     * @return Value
     */
    public float getFloat(String key, float def) {
        return get(key, def, Float.class);
    }

    /**
     * 获取对应类型的数据
     * @param key Key
     * @param def 默认值
     * @return Value
     */
    public String getString(String key, String def) {
        return get(key, def, String.class);
    }

    /**
     * 删除指定的键值对
     * @param key 要删除的键值对key
     */
    public void remove(String key) {
        try {
            String[] args = new String[2];
            args[0] = mSpace;
            args[1] = key;
            mResolver.delete(mUri, null, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除所有
     */
    public void removeAll() {
        Bundle extras = new Bundle();
        extras.putString("space", mSpace);
        mResolver.call(mUri, "removeAll", mSaveTye.path, extras);
    }

    /**
     * 取得当前已经存储的所有key集合
     */
    public Set<String> keySet() {
        Bundle extras = new Bundle();
        extras.putString("space", mSpace);
        Bundle bundle = mResolver.call(mUri, "keySet", mSaveTye.path, extras);
        if (bundle == null) {
            return null;
        }
        String[] keys = bundle.getStringArray("result");
        return new HashSet<>(Arrays.asList(keys));
    }

    /**
     * 保存方式
     */
    public enum SaveType {
        /**
         * SharedPreferences 方式存储 kv 对，适合数据量较小的场景
         */
        SP("/sp"),
        /**
         * 内存 方式存储 kv 对，适合仅单次冷启动生效的场景
         */
        RAM("/ram"),
        /**
         * 数据库方式存储 kv 对，适合存储数据比较多的场景
         */
        DB("/db");

        public String path;

        SaveType(String uri) {
            this.path = uri;
        }
    }

}
