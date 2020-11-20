
## About

Android 多进程同步的 Key - Value 结构存储工具，支持内存，SharedPreferences，DB 三种同步存储方式，支持存储集合。

## Use

```java
DataControl control  = new DataControl(this, DataControl.SaveType.DB);
control.putString("321","321");
control.putString("321","qqq");
control.putString("321","bbb");
```

支持以下方法：

- putCollection
- getMap
- getCollection
- putMap
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