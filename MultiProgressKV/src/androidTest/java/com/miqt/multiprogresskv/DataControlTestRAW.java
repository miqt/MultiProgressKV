package com.miqt.multiprogresskv;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DataControlTestRAW {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    DataControl dataControl = new DataControl(appContext, "DataControlTestDB", DataControl.SaveType.RAM);

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
    }

    @Test
    public void getMap() {
    }

    @Test
    public void getCollection() {
    }

    @Test
    public void putMap() {
    }

    @Test
    public void putInt() {
    }

    @Test
    public void putBool() {
    }

    @Test
    public void putLong() {
    }

    @Test
    public void putFloat() {
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