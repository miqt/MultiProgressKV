package com.miqt.datacontrol;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.miqt.multiprogresskv.DataControl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class SpControlTest {
    @Test
    public void test() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DataControl control = new DataControl(appContext,"hellotab", DataControl.SaveType.DB);

        control.putString("k1", "hello");
        control.putInt("k2", 10);
        control.putFloat("k3", 10.0f);
        control.putBool("k4", true);
        control.putLong("k5", 100L);

        Assert.assertEquals(control.getString("k1", null), "hello");
        Assert.assertEquals((int) control.getInt("k2", -1), 10);
        Assert.assertEquals(10, control.getFloat("k3", -1), 0.0);
        Assert.assertEquals((boolean) control.getBool("k4", true), true);
        Assert.assertEquals((long) control.getLong("k5", -1), 100L);

        control.putString("k1", "hello1");
        control.putInt("k2", 100000);
        control.putFloat("k3", 10.1f);
        control.putBool("k4", false);
        control.putLong("k5", -100);

        Assert.assertEquals(control.getString("k1", null), "hello1");
        Assert.assertEquals((int) control.getInt("k2", -1), 100000);
        Assert.assertEquals(10.1f, control.getFloat("k3", -1), 0.0);
        Assert.assertEquals((boolean) control.getBool("k4", true), false);
        Assert.assertEquals((long) control.getLong("k5", -1), -100);

        control.remove("k1");
        control.remove("k2");
        control.remove("k3");
        control.remove("k4");
        control.remove("k5");

        Assert.assertEquals(control.getString("k1", "null"), "null");
        Assert.assertEquals((int) control.getInt("k2", -1), -1);
        Assert.assertEquals(-1, control.getFloat("k3", -1), -1);
        Assert.assertEquals((boolean) control.getBool("k4", true), true);
        Assert.assertEquals((long) control.getLong("k5", -1), -1);

        Collection<Object> list = new ArrayList<>();
        list.add("aabb");
        list.add(false);
        list.add(true);
        list.add(1.15);
        list.add(10000);
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", false);
        map.put("c", 1.5);
        map.put("d", "fdsaf");
        list.add(map);
        control.putCollection("list", list);
        control.putMap("map", map);

        Collection<Object> listRes = control.getCollection("list");
        Map<String, Object> mapRes = control.getMap("map");

        Assert.assertEquals(list, listRes);
        Assert.assertEquals(map, mapRes);

    }
}