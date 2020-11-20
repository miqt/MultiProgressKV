package com.miqt.multiprogresskv;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DataContentProvider extends ContentProvider {

    private final static short TYPE_RAM = 1;
    private final static short TYPE_SP = 2;
    private final static short TYPE_DB = 3;
    private static Map<String, Map<String, Object>> mapCache = new HashMap<>();
    private static Map<String, SharedPreferences> preferencesCache = new HashMap<>();
    private UriMatcher mUriMatcher;
    private Context mContext;

    @Override
    public boolean onCreate() {
        mContext = this.getContext();
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = mContext.getPackageName() + ".DataContentProvider";
        mUriMatcher.addURI(authority, "/ram", TYPE_RAM);
        mUriMatcher.addURI(authority, "/sp", TYPE_SP);
        mUriMatcher.addURI(authority, "/db", TYPE_DB);
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        try {
            if (Process.myUid() != Binder.getCallingUid()) {
                return null;
            }
            if (projection == null) {
                return null;
            }
            String name = projection[0];
            String key = projection[1];
            String type = projection[2];
            String def = projection[3];
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(key) || TextUtils.isEmpty(type)) {
                return null;
            }
            int code = mUriMatcher.match(uri);
            if (code == TYPE_RAM) {
                return getFromRaw(name, key);
            } else if (code == TYPE_SP) {
                return getFromSp(name, key, type, def);
            } else if (code == TYPE_DB) {
                return getFromDB(key, type, def, name);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private Cursor getFromDB(String key, String type, String def, String name) {
        String result = DBHelper.getInstance(mContext).get(key, def, type, name);
        if (result != null) {
            MatrixCursor cursor = new MatrixCursor(new String[]{"data"}, 1);
            cursor.addRow(new Object[]{result});
            return cursor;
        }
        return null;
    }

    private Cursor getFromRaw(String name, String key) {
        Map<String, Object> map = mapCache.get(name);
        if (map == null) {
            map = new HashMap<>();
            mapCache.put(name, map);
            return null;
        }
        Object o = map.get(key);
        if (o == null) {
            return null;
        }
        MatrixCursor cursor = new MatrixCursor(new String[]{"data"}, 1);
        cursor.addRow(new Object[]{o});
        return cursor;
    }

    private Cursor getFromSp(String name, String key, String type, String def) {
        if (preferencesCache.get(name) != null) {
            Object result = null;
            if (Integer.class.getName().equals(type)) {
                result = preferencesCache.get(name).getInt(key, Integer.parseInt(def));
            } else if (Boolean.class.getName().equals(type)) {
                result = preferencesCache.get(name).getBoolean(key, Boolean.parseBoolean(def));
            } else if (Float.class.getName().equals(type)) {
                result = preferencesCache.get(name).getFloat(key, Float.parseFloat(def));
            } else if (Long.class.getName().equals(type)) {
                result = preferencesCache.get(name).getLong(key, Long.parseLong(def));
            } else if (String.class.getName().equals(type)) {
                result = preferencesCache.get(name).getString(key, def);
            }
            if (result != null) {
                MatrixCursor cursor = new MatrixCursor(new String[]{"data"}, 1);
                cursor.addRow(new Object[]{String.valueOf(result)});
                return cursor;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        try {
            if (Process.myUid() != Binder.getCallingUid()) {
                return -1;
            }
            if (selectionArgs == null || selectionArgs.length != 2) {
                return 0;
            }
            String name = selectionArgs[0];
            String key = selectionArgs[1];
            int code = mUriMatcher.match(uri);
            if (code == TYPE_RAM) {
                Map<String, Object> map = mapCache.get(name);
                if (map != null) {
                    map.remove(key);
                }
            } else if (code == TYPE_SP) {
                SharedPreferences preferences = preferencesCache.get(name);
                if (preferences != null) {
                    preferences.edit().remove(key).apply();
                }
            } else if (code == TYPE_DB) {
                return (int) DBHelper.getInstance(mContext).remove(key, name);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        try {
            if (Process.myUid() != Binder.getCallingUid()) {
                return -1;
            }
            if (selectionArgs == null || selectionArgs.length != 4) {
                return -1;
            }
            String name = selectionArgs[0];
            String key = selectionArgs[1];
            String value = selectionArgs[2];
            String type = selectionArgs[3];
            int code = mUriMatcher.match(uri);
            if (code == TYPE_RAM) {
                Map<String, Object> map = mapCache.get(name);
                if (map == null) {
                    map = new HashMap<>();
                    mapCache.put(name, map);
                }
                map.put(key, value);
            } else if (code == TYPE_SP) {
                SharedPreferences preferences = preferencesCache.get(name);
                if (preferences == null) {
                    preferences = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
                    preferencesCache.put(name, preferences);
                }
                if (Integer.class.getName().equals(type)) {
                    preferences.edit().putInt(key, Integer.parseInt(value)).apply();
                    return 1;
                }
                if (Boolean.class.getName().equals(type)) {
                    preferences.edit().putBoolean(key, Boolean.parseBoolean(value)).apply();
                    return 1;
                }
                if (Float.class.getName().equals(type)) {
                    preferences.edit().putFloat(key, Float.parseFloat(value)).apply();
                    return 1;
                }
                if (Long.class.getName().equals(type)) {
                    preferences.edit().putLong(key, Long.parseLong(value)).apply();
                    return 1;
                }
                if (String.class.getName().equals(type)) {
                    preferences.edit().putString(key, String.valueOf(value)).apply();
                    return 1;
                }

            } else if (code == TYPE_DB) {
                DBHelper.getInstance(mContext).put(key, value, type, name);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }
}