package com.miqt.multiprogresskv;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miqt.multiprogresskv.helper.DBHelper;
import com.miqt.multiprogresskv.helper.RamHelper;
import com.miqt.multiprogresskv.helper.SpHelper;

import java.util.Objects;

public class DataContentProvider extends ContentProvider {

    private final static short TYPE_RAM = 1;
    private final static short TYPE_SP = 2;
    private final static short TYPE_DB = 3;

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
    public Bundle call(@NonNull String authority, @NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        if (Process.myUid() != Binder.getCallingUid()) {
            return null;
        }
        if (extras == null) {
            return null;
        }
        if ("contains".equals(method)) {
            String space = extras.getString("space");
            String key = extras.getString("key");
            if (key == null) {
                return null;
            }
            if (space == null) {
                return null;
            }
            boolean contains = false;
            if (Objects.equals(arg, DataControl.SaveType.RAM.path)) {
                contains = RamHelper.getInstance().contains(space, key);
            } else if (Objects.equals(arg, DataControl.SaveType.SP.path)) {
                contains = SpHelper.getInstance(mContext).contains(space, key);
            } else if (Objects.equals(arg, DataControl.SaveType.DB.path)) {
                contains = DBHelper.getInstance(mContext).contains(space, key);
            }
            if (contains) {
                return extras;
            } else {
                return null;
            }
        } else if ("removeAll".equals(method)) {
            String space = extras.getString("space");
            if (space == null) {
                return null;
            }
            if (Objects.equals(arg, DataControl.SaveType.RAM.path)) {
                RamHelper.getInstance().removeAll(space);
            } else if (Objects.equals(arg, DataControl.SaveType.SP.path)) {
                SpHelper.getInstance(mContext).removeAll(space);
            } else if (Objects.equals(arg, DataControl.SaveType.DB.path)) {
                DBHelper.getInstance(mContext).removeAll(space);
            }
        } else if ("keySet".equals(method)) {
            String space = extras.getString("space");
            if (space == null) {
                return null;
            }
            String[] keys = null;
            if (Objects.equals(arg, DataControl.SaveType.RAM.path)) {
                keys = RamHelper.getInstance().keySet(space).toArray(new String[0]);
            } else if (Objects.equals(arg, DataControl.SaveType.SP.path)) {
                keys = SpHelper.getInstance(mContext).keySet(space).toArray(new String[0]);
            } else if (Objects.equals(arg, DataControl.SaveType.DB.path)) {
                keys = DBHelper.getInstance(mContext).keySet(space).toArray(new String[0]);
            }
            if (keys!=null&&keys.length>0){
                Bundle bundle = new Bundle();
                bundle.putStringArray("result",keys);
                return bundle;
            }
        }
        return null;
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
            String space = projection[0];
            String key = projection[1];
            String type = projection[2];
            String def = projection[3];
            if (TextUtils.isEmpty(space) || TextUtils.isEmpty(key) || TextUtils.isEmpty(type)) {
                return null;
            }
            int code = mUriMatcher.match(uri);
            Object o = null;
            if (code == TYPE_RAM) {
                o = RamHelper.getInstance().get(space, key, def, type);
            } else if (code == TYPE_SP) {
                o = SpHelper.getInstance(mContext).get(space, key, def, type);
            } else if (code == TYPE_DB) {
                o = DBHelper.getInstance(mContext).get(space, key, def, type);
            }
            if (o != null) {
                MatrixCursor cursor = new MatrixCursor(new String[]{"data"}, 1);
                cursor.addRow(new Object[]{o});
                return cursor;
            }
        } catch (Throwable e) {
            e.printStackTrace();
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
            String space = selectionArgs[0];
            String key = selectionArgs[1];
            int code = mUriMatcher.match(uri);
            if (code == TYPE_RAM) {
                RamHelper.getInstance().remove(space, key);
            } else if (code == TYPE_SP) {
                SpHelper.getInstance(mContext).remove(space, key);
            } else if (code == TYPE_DB) {
                DBHelper.getInstance(mContext).remove(space, key);
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
            String space = selectionArgs[0];
            String key = selectionArgs[1];
            String value = selectionArgs[2];
            String type = selectionArgs[3];
            int code = mUriMatcher.match(uri);
            if (code == TYPE_RAM) {
                RamHelper.getInstance().put(space, key, value, type);
            } else if (code == TYPE_SP) {
                SpHelper.getInstance(mContext).put(space, key, value, type);
            } else if (code == TYPE_DB) {
                DBHelper.getInstance(mContext).put(space, key, value, type);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }
}
