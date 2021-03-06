package com.miqt.multiprogresskv;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DataControlTest {
    @Test
    public void test() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DataControl control = new DataControl(appContext, "hellotab", DataControl.SaveType.SP);

        control.putString("k1", "hello");
        control.putInt("k2", 10);
        control.putFloat("k3", 10.0f);
        control.putBool("k4", true);
        control.putLong("k5", 100L);

        Assert.assertTrue(control.contains("k1"));
        Assert.assertTrue(control.contains("k2"));
        Assert.assertTrue(control.contains("k3"));
        Assert.assertTrue(control.contains("k4"));
        Assert.assertTrue(control.contains("k5"));

        Assert.assertEquals(control.getString("k1", null), "hello");
        Assert.assertEquals(control.getInt("k2", -1), 10);
        Assert.assertEquals(10, control.getFloat("k3", -1), 0.0);
        Assert.assertEquals(control.getBool("k4", true), true);
        Assert.assertEquals(control.getLong("k5", -1), 100L);

        control.putString("k1", "hello1");
        control.putInt("k2", 100000);
        control.putFloat("k3", 10.1f);
        control.putBool("k4", false);
        control.putLong("k5", -100);

        Assert.assertEquals(control.getString("k1", null), "hello1");
        Assert.assertEquals(control.getInt("k2", -1), 100000);
        Assert.assertEquals(10.1f, control.getFloat("k3", -1), 0.0);
        Assert.assertEquals(control.getBool("k4", true), false);
        Assert.assertEquals(control.getLong("k5", -1), -100);

        control.remove("k1");
        control.remove("k2");
        control.remove("k3");
        control.remove("k4");
        control.remove("k5");
        Assert.assertFalse(control.contains("k1"));
        Assert.assertFalse(control.contains("k2"));
        Assert.assertFalse(control.contains("k3"));
        Assert.assertFalse(control.contains("k4"));
        Assert.assertFalse(control.contains("k5"));
        Assert.assertEquals(control.getString("k1", "null"), "null");
        Assert.assertEquals(control.getInt("k2", -1), -1);
        Assert.assertEquals(-1, control.getFloat("k3", -1), -1);
        Assert.assertEquals(control.getBool("k4", true), true);
        Assert.assertEquals(control.getLong("k5", -1), -1);

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

    @Test
    public void testEntity() {
        class TestEntity {
            static final int anInt = 1;
            final Float aFloat = 10.0F;
            final Double aDouble = 10.0d;

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                TestEntity that = (TestEntity) o;
                return Objects.equals(aFloat, that.aFloat) &&
                        Objects.equals(aDouble, that.aDouble);
            }

            @Override
            public int hashCode() {
                return Objects.hash(aFloat, aDouble);
            }
        }
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TestEntity testEntity = new TestEntity();
        DataControl control = new DataControl(appContext, DataControl.SaveType.DB);
        control.putEntity("a1", testEntity);
        TestEntity testEntity2 = control.getEntity("a1", null, new TestEntity());
        Assert.assertEquals(testEntity2, testEntity);
    }


    @Test
    public void test2MSize() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DataControl control = new DataControl(appContext, DataControl.SaveType.DB);
        byte[] bytes = new byte[2048 * 1024];
        Arrays.fill(bytes, (byte) 100);
        control.putString("he", new String(bytes));

        String result = control.getString("he", null);

        Assert.assertNull(result);


    }

    @Test
    public void test2MSizeSp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DataControl control = new DataControl(appContext, DataControl.SaveType.SP);
        byte[] bytes = new byte[2048 * 1024];
        Arrays.fill(bytes, (byte) 100);
        control.putString("he", new String(bytes));
        String result = control.getString("he", null);
        Assert.assertNotNull(result);
    }

    @Test
    public void keyset() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DataControl control1 = new DataControl(appContext, "keyset", DataControl.SaveType.SP);
        DataControl control2 = new DataControl(appContext, "keyset", DataControl.SaveType.RAM);
        DataControl control3 = new DataControl(appContext, "keyset", DataControl.SaveType.DB);

        control1.putString("1", "1");
        control1.putString("2", "2");
        control1.putString("3", "3");
        control2.putString("1", "1");
        control2.putString("2", "2");
        control2.putString("3", "3");
        control3.putString("1", "1");
        control3.putString("2", "2");
        control3.putString("3", "3");

        Set<String> set1 = control1.keySet();
        Set<String> set2 = control2.keySet();
        Set<String> set3 = control3.keySet();

        Assert.assertTrue(set1 != null && set1.size() > 0);

        Assert.assertEquals(set1, set2);
        Assert.assertEquals(set1, set3);


    }
    @Test
    public void getAll() {
        // 暫未支持
    }

    @Test
    public void removeall() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DataControl control1 = new DataControl(appContext, "removeall", DataControl.SaveType.SP);
        DataControl control2 = new DataControl(appContext, "removeall", DataControl.SaveType.RAM);
        DataControl control3 = new DataControl(appContext, "removeall", DataControl.SaveType.DB);

        control1.putString("1", "1");
        control1.putString("2", "2");
        control1.putString("3", "3");
        control2.putString("1", "1");
        control2.putString("2", "2");
        control2.putString("3", "3");
        control3.putString("1", "1");
        control3.putString("2", "2");
        control3.putString("3", "3");

        Set<String> set1 = control1.keySet();
        Set<String> set2 = control2.keySet();
        Set<String> set3 = control3.keySet();

        Assert.assertTrue(set1 != null && set1.size() > 0);

        Assert.assertEquals(set1, set2);
        Assert.assertEquals(set1, set3);

        control1.removeAll();
        control2.removeAll();
        control3.removeAll();

        set1 = control1.keySet();
        set2 = control2.keySet();
        set3 = control3.keySet();

        Assert.assertNull(set1);
        Assert.assertNull(set2);
        Assert.assertNull(set3);

    }
}