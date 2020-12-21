package com.miqt.multiprogresskv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "miqt_kv_db.db";
    private static final String TB_NAME = "miqt_kv";
    private static final int DV_VERSION = 2;
    private static volatile DBHelper instance;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DV_VERSION);
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

    private void createTable(String name, SQLiteDatabase db) {
        String create_tab = "CREATE TABLE IF NOT EXISTS "
                + name + " (\n\t" +
                Column.ID + Types.ID + ",\n\t" +
                Column.NAME + Types.NAME + ",\n\t" +
                Column.KEY + Types.KEY + ",\n\t" +
                Column.VALUE + Types.VALUE + ",\n\t" +
                Column.TYPE + Types.TYPE + ",\n\t" +
                Column.TIME + Types.TIME +
                "\n);";
        db.execSQL(create_tab);
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS [" + TB_NAME + "_rep]" +
                "ON [" + TB_NAME + "](\n" +
                "\t[" + Column.NAME + "],\n" +
                "\t[" + Column.KEY + "]" +
                "\n);");
    }

    public String get(String key, String def, String type, String name) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(TB_NAME, new String[]{Column.NAME, Column.KEY, Column.VALUE, Column.TYPE}, Column.NAME + " = ? AND " + Column.KEY + " = ? ", new String[]{name, key}, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) {
            return def;
        }
        cursor.moveToFirst();
        String result = cursor.getString(cursor.getColumnIndex(Column.VALUE));
        cursor.close();
        return result;
    }

    public void put(String key, String value, String type, String name) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Column.NAME, name);
        values.put(Column.KEY, key);
        values.put(Column.VALUE, value);
        values.put(Column.TYPE, type);
        long res = database.replace(TB_NAME, null, values);
        values.clear();
    }

    public long remove(String key, String name) {
        return getWritableDatabase().delete(TB_NAME, Column.NAME + " = ? AND " + Column.KEY + " = ? ", new String[]{name, key});
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(TB_NAME, db);
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
        static final String NAME = "name";
        static final String KEY = "key";
        static final String VALUE = "value";
        static final String TYPE = "type";
        static final String TIME = "time";
    }

    /*
    CREATE TABLE [entity6](
      [id] INTEGER PRIMARY KEY AUTOINCREMENT,
      [name] TEXT NOT NULL,
      [key] TEXT NOT NULL,
      [value] TEXT NOT NULL,
      [type] TEXT ,
      [time] BIGINT NOT NULL DEFAULT ((strftime('%s','now') - strftime('%S','now') + strftime('%f','now'))*1000));

    CREATE UNIQUE INDEX [rep2]
    ON [entity6](
      [name],
      [key]);

     */
    // 类型
    static class Types {
        static final String ID = " INTEGER PRIMARY KEY AUTOINCREMENT ";
        static final String NAME = " TEXT NOT NULL ";
        static final String KEY = " TEXT NOT NULL ";
        static final String VALUE = " TEXT NOT NULL ";
        static final String TYPE = " TEXT ";
        static final String TIME = " BIGINT NOT NULL DEFAULT ((strftime('%s','now') - strftime('%S','now') + strftime('%f','now'))*1000) ";
    }
}
