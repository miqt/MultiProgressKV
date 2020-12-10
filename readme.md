
## About

Android 多进程同步的 Key - Value 结构存储工具，支持内存，SharedPreferences，DB 三种同步存储方式，支持存储集合、实体类。

## Use

```java
DataControl control  = new DataControl(this, DataControl.SaveType.DB);
control.putString("key","value");
class Entity{
    int a = 1;
    String b = "2";
}
control.putEntity("bean", new Entity());
Entity test = control.getEntity("bean", null, new Entity());
```

支持以下方法：

- contains
- putEntity
- getEntity
- getMap
- putMap
- getCollection
- putCollection
- putInt
- putBool
- putLong
- putFloat
- putString
- getInt
- getBool
- getLong
- getFloat
- getString
- remove