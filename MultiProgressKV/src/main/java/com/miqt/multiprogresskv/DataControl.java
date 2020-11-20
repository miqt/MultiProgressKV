package com.miqt.multiprogresskv;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Copyright © 2020 MiQt Inc. All rights reserved.
 * @Description: 多进程内存存储 kv
 * @Version: 1.0
 * @CreateDate: 2020/11/2 18:42
 * @Author: miqt
 * @mail: miqtdev@163.com
 */
public class DataControl {

    private String mName;
    private Context mContext;
    private ContentResolver mResolver;
    private Uri mUri;

    public DataControl(Context context) {
        this(context, "def", SaveType.SP);
    }

    public DataControl(Context context, String name) {
        this(context, name, SaveType.SP);
    }

    public DataControl(Context context, SaveType type) {
        this(context, "def", type);
    }

    /**
     * 构建数据读写工具
     *
     * @param context
     * @param name    命名控件，隔离用，注意这个name对应不同的存储方式分别为数据库表名和sp文件名
     * @param type    存储类型
     */
    public DataControl(Context context, String name, SaveType type) {
        if (TextUtils.isEmpty(name)) {
            this.mName = "null";
        } else {
            this.mName = name;
        }
        this.mContext = context;
        this.mResolver = context.getContentResolver();
        this.mUri = Uri.parse("content://" + mContext.getPackageName() + ".DataContentProvider" + type.path);
    }

    private <T> T get(String key, T defValue, Class<T> type) {
        if (key == null) {
            return defValue;
        }
        if (type == null) {
            return defValue;
        }
        String[] projection = new String[4];
        projection[0] = mName;
        projection[1] = key;
        projection[2] = type.getName();
        projection[3] = String.valueOf(defValue);
        Cursor cursor = null;
        try {
            cursor = mResolver.query(mUri, projection, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() >= 1) {
                    if (cursor.moveToPosition(0)) {
                        String values = cursor.getString(0);
                        return ReflectUtils.reflect(type).method("valueOf", values).get();
                    }
                }
                cursor.close();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return defValue;
    }


    private <T> void put(String key, T value, Class<T> type) {
        if (key == null) {
            return;
        }
        try {
            String[] args = new String[4];
            args[0] = mName;
            args[1] = key;
            args[2] = String.valueOf(value);
            args[3] = type.getName();
            mResolver.update(mUri, null, null, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void putCollection(String key, Collection<Object> value) {
        if (key == null) {
            return;
        }
        try {
            String[] args = new String[4];
            args[0] = mName;
            args[1] = key;
            args[2] = new JSONArray(value).toString();
            args[3] = String.class.getName();
            mResolver.update(mUri, null, null, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

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

    public void putMap(String key, Map<String, Object> value) {
        if (key == null) {
            return;
        }
        try {
            String[] args = new String[4];
            args[0] = mName;
            args[1] = key;
            args[2] = new JSONObject(value).toString();
            args[3] = String.class.getName();
            mResolver.update(mUri, null, null, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void putInt(String key, int value) {
        put(key, value, Integer.class);
    }

    public void putBool(String key, boolean value) {
        put(key, value, Boolean.class);
    }

    public void putLong(String key, long value) {
        put(key, value, Long.class);
    }

    public void putFloat(String key, float value) {
        put(key, value, Float.class);
    }

    public void putString(String key, String value) {
        put(key, value, String.class);
    }

    public int getInt(String key, int def) {
        return get(key, def, Integer.class);
    }

    public boolean getBool(String key, boolean def) {
        return get(key, def, Boolean.class);
    }

    public long getLong(String key, long def) {
        return get(key, def, Long.class);
    }

    public float getFloat(String key, float def) {
        return get(key, def, Float.class);
    }

    public String getString(String key, String def) {
        return get(key, def, String.class);
    }


    public void remove(String key) {
        try {
            String[] args = new String[2];
            args[0] = mName;
            args[1] = key;
            mResolver.delete(mUri, null, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public enum SaveType {
        SP("/sp"),
        RAM("/ram"),
        DB("/db");

        private String path;

        SaveType(String uri) {
            this.path = uri;
        }
    }

}