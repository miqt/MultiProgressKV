package com.miqt.multiprogresskv;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataControlTestDB {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    DataControl dataControl = new DataControl(appContext, "DataControlTestDB", DataControl.SaveType.DB);

    @Before
    public void setUp() throws Exception {
        dataControl.removeAll();
    }

    @Test
    public void contains() {
        Assert.assertFalse(dataControl.contains("testcontains"));
        dataControl.putString("testcontains", "xxx");
        Assert.assertTrue(dataControl.contains("testcontains"));
    }

    @Test
    public void putEntity() {
        Assert.assertFalse(dataControl.contains("testputEntity"));
        dataControl.putEntity("testputEntity", new Ent());
        Assert.assertTrue(dataControl.contains("testputEntity"));
        Ent ent  = dataControl.getEntity("testputEntity",null,Ent.class);
        Assert.assertEquals(new Ent(),ent);
    }


    @Test
    public void putCollection() {
        Collection<Object> list = new ArrayList<Object>();
        list.add("123");
        list.add("123");
        list.add("123");
        Assert.assertFalse(dataControl.contains("putCollection"));
        dataControl.putCollection("putCollection", list);
        Assert.assertTrue(dataControl.contains("putCollection"));
        List<Object> ent  = (List<Object>) dataControl.getCollection("putCollection");
        Assert.assertEquals(list,ent);
    }

    @Test
    public void getMap() {
        Map<String,Object> map = new HashMap<>();
        map.put("","");
        map.put("123","123");
        map.put("321",123);
        map.put("fdsaf",false);

        Assert.assertFalse(dataControl.contains("getMap"));
        dataControl.putMap("getMap", map);
        Assert.assertTrue(dataControl.contains("getMap"));
        Map<String,Object> ent  = (Map<String,Object>) dataControl.getMap("getMap");
        Assert.assertEquals(map,ent);
    }




    @Test
    public void putInt() {
        Assert.assertFalse(dataControl.contains("putInt"));
        dataControl.putInt("putInt", Integer.MAX_VALUE);
        Assert.assertTrue(dataControl.contains("putInt"));
        int map = dataControl.getInt("putInt",0);
        Assert.assertEquals(Integer.MAX_VALUE,map);
    }

    @Test
    public void putBool() {
        Assert.assertFalse(dataControl.contains("putBool"));
        dataControl.putBool("putBool", true);
        Assert.assertTrue(dataControl.contains("putBool"));
        boolean map = dataControl.getBool("putBool",false);
        Assert.assertTrue(map);
    }

    @Test
    public void putLong() {
        Assert.assertFalse(dataControl.contains("putLong"));
        dataControl.putLong("putLong", Long.MAX_VALUE);
        Assert.assertTrue(dataControl.contains("putLong"));
        long map = dataControl.getLong("putLong",0);
        Assert.assertEquals(Long.MAX_VALUE,map);
    }

    @Test
    public void putFloat() {
        Assert.assertFalse(dataControl.contains("putFloat"));
        dataControl.putFloat("putFloat", Float.MAX_VALUE);
        Assert.assertTrue(dataControl.contains("putFloat"));
        float map = dataControl.getFloat("putFloat",0);
        Assert.assertEquals(Float.MAX_VALUE, map, 0.0);
    }

    @Test
    public void putString() {
    }

    @Test
    public void getInt() {
    }

    @Test
    public void getBool() {
    }

    @Test
    public void getLong() {
    }

    @Test
    public void getFloat() {
    }

    @Test
    public void getString() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void removeAll() {
    }

    @Test
    public void keySet() {
    }
}