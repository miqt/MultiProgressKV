package com.miqt.multiprogresskv.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.LruCache;

import java.util.Map;
import java.util.Set;

public class SpHelper implements IDataHelper {
    private final LruCache<String, SharedPreferences> preferencesCache = new LruCache<>(10);

    private static volatile SpHelper instance;
    private final Context mContext;

    private SpHelper(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static SpHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SpHelper.class) {
                if (instance == null) {
                    instance = new SpHelper(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void put(String space, String key, String value, String type) {
        SharedPreferences preferences = preferencesCache.get(space);
        if (preferences == null) {
            preferences = mContext.getSharedPreferences(space, Context.MODE_PRIVATE);
            preferencesCache.put(space, preferences);
        }
        if (Integer.class.getName().equals(type)) {
            preferences.edit().putInt(key, Integer.parseInt(value)).apply();
        } else if (Boolean.class.getName().equals(type)) {
            preferences.edit().putBoolean(key, Boolean.parseBoolean(value)).apply();
        } else if (Float.class.getName().equals(type)) {
            preferences.edit().putFloat(key, Float.parseFloat(value)).apply();
        } else if (Long.class.getName().equals(type)) {
            preferences.edit().putLong(key, Long.parseLong(value)).apply();
        } else if (String.class.getName().equals(type)) {
            preferences.edit().putString(key, String.valueOf(value)).apply();
        }
    }

    @Override
    public Object get(String space, String key, String def, String type) {
        SharedPreferences preference = preferencesCache.get(space);
        if (preference == null) {
            preference = mContext.getSharedPreferences(space, Context.MODE_PRIVATE);
            preferencesCache.put(space, preference);
        }
        Object result = null;
        if (Integer.class.getName().equals(type)) {
            result = preference.getInt(key, Integer.parseInt(def));
        } else if (Boolean.class.getName().equals(type)) {
            result = preference.getBoolean(key, Boolean.parseBoolean(def));
        } else if (Float.class.getName().equals(type)) {
            result = preference.getFloat(key, Float.parseFloat(def));
        } else if (Long.class.getName().equals(type)) {
            result = preference.getLong(key, Long.parseLong(def));
        } else if (String.class.getName().equals(type)) {
            result = preference.getString(key, def);
        }
        return result;
    }

    @Override
    public void remove(String space, String key) {
        SharedPreferences preference = preferencesCache.get(space);
        if (preference == null) {
            preference = mContext.getSharedPreferences(space, Context.MODE_PRIVATE);
            preferencesCache.put(space, preference);
        }
        preference.edit().remove(key).apply();
    }

    @Override
    public void removeAll(String space) {
        SharedPreferences preference = preferencesCache.get(space);
        if (preference == null) {
            preference = mContext.getSharedPreferences(space, Context.MODE_PRIVATE);
            preferencesCache.put(space, preference);
        }
        preference.edit().clear().apply();
    }

    @Override
    public boolean contains(String space, String key) {
        SharedPreferences preference = preferencesCache.get(space);
        if (preference == null) {
            preference = mContext.getSharedPreferences(space, Context.MODE_PRIVATE);
            preferencesCache.put(space, preference);
        }
        return preference.contains(key);
    }

    @Override
    public Set<String> keySet(String space) {
        SharedPreferences preference = preferencesCache.get(space);
        if (preference == null) {
            preference = mContext.getSharedPreferences(space, Context.MODE_PRIVATE);
            preferencesCache.put(space, preference);
        }
        return preference.getAll().keySet();
    }
    @Override
    public Map<String, Object> getAll(String space) {
        SharedPreferences preference = preferencesCache.get(space);
        if (preference == null) {
            preference = mContext.getSharedPreferences(space, Context.MODE_PRIVATE);
            preferencesCache.put(space, preference);
        }
        return (Map<String, Object>) preference.getAll();
    }
}
