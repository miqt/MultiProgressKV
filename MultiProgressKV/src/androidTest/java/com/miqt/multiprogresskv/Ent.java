package com.miqt.multiprogresskv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Ent {
    int anInt = 1;
    float aFloat = 1;
    double aDouble = 1;
    String string = "abc";
    char aChar = 'c';
    short aShort = 1;
    byte aByte = 1;
    boolean aBoolean = true;
    int anInt2 = 1;
    Float aFloat2 = 1F;
    Double aDouble2 = 1D;
    String string2 = "abc";
    Character aChar2 = 'c';
    Short aShort2 = 1;
    Byte aByte2 = 1;
    Boolean aBoolean2 = true;
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    Set<Object> set = new HashSet<>();
    List<Object> list = new ArrayList<>();
    Map<Object, Object> map = new HashMap<>();

    public Ent() {
    }

    public void init() {
        try {
            jsonObject.put("xxx", "xxx");
            jsonObject.put("ccc", 123);
            jsonObject.put("aaa", true);
            jsonArray.put(jsonObject);
            jsonArray.put("123");

            map.put("xxx",123);
            map.put("aaa",123);
            map.put("bbb","123");
            map.put("ccc",123);

            set.add("123");
            set.add(3);
            set.add(1);
            set.add(2);
            set.add(5);

            list.add("123");
            list.add(3);
            list.add(1);
            list.add(2);
            list.add(5);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ent ent = (Ent) o;
        return anInt == ent.anInt &&
                Float.compare(ent.aFloat, aFloat) == 0 &&
                Double.compare(ent.aDouble, aDouble) == 0 &&
                aChar == ent.aChar &&
                aShort == ent.aShort &&
                aByte == ent.aByte &&
                aBoolean == ent.aBoolean &&
                anInt2 == ent.anInt2 &&
                Objects.equals(string, ent.string) &&
                Objects.equals(aFloat2, ent.aFloat2) &&
                Objects.equals(aDouble2, ent.aDouble2) &&
                Objects.equals(string2, ent.string2) &&
                Objects.equals(aChar2, ent.aChar2) &&
                Objects.equals(aShort2, ent.aShort2) &&
                Objects.equals(aByte2, ent.aByte2) &&
                Objects.equals(aBoolean2, ent.aBoolean2) &&
                jsonObject.length() == ent.jsonObject.length() &&
//                set.size() == ent.set.size() &&
                list.size() == ent.list.size() &&
                map.size() == ent.map.size() &&
                jsonArray.length() == ent.jsonArray.length();
    }

    @Override
    public int hashCode() {
        return Objects.hash(anInt, aFloat, aDouble, string, aChar, aShort, aByte, aBoolean, anInt2, aFloat2, aDouble2, string2, aChar2, aShort2, aByte2, aBoolean2, jsonObject, jsonArray);
    }
}
