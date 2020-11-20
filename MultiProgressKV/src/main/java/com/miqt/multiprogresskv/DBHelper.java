package com.miqt.multiprogresskv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    private static final String db_name = "miqt_kv_db.db";
    private static final int dv_version = 2;
    private static volatile DBHelper instance;
    private HashSet<String> tabNames;

    private DBHelper(Context context) {
        super(context, db_name, null, dv_version);
        tabNames = new HashSet<>();
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context);
                }
            }
        }
        return instance;
    }

    public long put(String key, String value, String type, String name) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Column.KEY, key);
        values.put(Column.VALUE, value);
        values.put(Column.TYPE, type);
        checkTable(name);
        long res = database.replace(name, null, values);
        values.clear();
        return res;
    }

    private void checkTable(String name) {
        if (!tabNames.contains(name)) {
            String create_tab = "create table if not exists "
                    + name + " (" +
                    Column.ID + Types.ID + "," +
                    Column.KEY + Types.KEY + "," +
                    Column.VALUE + Types.VALUE + "," +
                    Column.TYPE + Types.TYPE + "," +
                    Column.RESERVE_1 + Types.RESERVE_1 + "," +
                    Column.RESERVE_2 + Types.RESERVE_2 + "," +
                    Column.RESERVE_3 + Types.RESERVE_3 +
                    " )";
            getWritableDatabase().execSQL(create_tab);
            tabNames.add(name);
        }

    }

    public String get(String key, String def, String type, String name) {
        checkTable(name);
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.query(name, new String[]{Column.KEY, Column.VALUE, Column.TYPE}, Column.KEY + " = ?", new String[]{key}, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) {
            return def;
        }
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndex(Column.VALUE));
        cursor.close();
        return result;
    }

    public long remove(String key, String name) {
        checkTable(name);
        return getWritableDatabase().delete(name, Column.KEY + " = ?", new String[]{key});
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAllTable(db, oldVersion, newVersion);
    }

    private void dropAllTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            try (Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)) {
                List<String> tables = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    tables.add(cursor.getString(0));
                }
                for (String table : tables) {
                    if (table.startsWith("sqlite_")) {
                        continue;
                    }
                    if (table.startsWith("android_")) {
                        continue;
                    }
                    db.execSQL("DROP TABLE IF EXISTS " + table);
                }
            } catch (Throwable ignored) {

            }
        }
    }

    // 列名
    static class Column {
        static final String ID = "id";
        static final String KEY = "k";
        static final String VALUE = "v";
        static final String TYPE = "t";
        static final String RESERVE_1 = "r1";
        static final String RESERVE_2 = "r2";
        static final String RESERVE_3 = "r3";
    }

    // 类型
    static class Types {
        static final String ID = " Integer Primary Key Autoincrement ";
        static final String KEY = " TEXT NOT NULL UNIQUE";
        static final String VALUE = " TEXT ";
        static final String TYPE = " VARCHAR(50) ";
        static final String RESERVE_1 = " TEXT ";
        static final String RESERVE_2 = " TEXT ";
        static final String RESERVE_3 = " TEXT ";
    }
}
