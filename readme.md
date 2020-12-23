
## About

为了解决 Android 多进程同步问题的 Key - Value 结构存储工具，支持内存，SharedPreferences，DB 三种同步存储方式，支持存储集合、实体类。

## Use

```gradle
implementation 'com.miqt:MultiProgressKV:1.0.1'
```

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

|类型|方法和说明|
|:--|---|
|boolean	|contains(java.lang.String key)<br/>判断是否包含指定键值对|
|boolean	|getBool(java.lang.String key, boolean def)<br/>获取对应类型的数据|
|java.util.Collection<java.lang.Object>	|getCollection(java.lang.String key)<br/>获取对应类型的数据|
|<T> T	|getEntity(java.lang.String key, T defValue, java.lang.Class<T> valueType)<br/>获取对应类型的数据|
|<T> T	|getEntity(java.lang.String key, T defValue, T tmp)<br/>获取对应类型的数据|
|float	|getFloat(java.lang.String key, float def)<br/>获取对应类型的数据|
|int	|getInt(java.lang.String key, int def)<br/>获取对应类型的数据|
|long	|getLong(java.lang.String key, long def)<br/>获取对应类型的数据|
|java.util.Map<java.lang.String,java.lang.Object>	|getMap(java.lang.String key)<br/>获取对应类型的数据|
|java.lang.String	|getString(java.lang.String key, java.lang.String def)<br/>获取对应类型的数据|
|java.util.Set<java.lang.String>	|keySet()<br/>取得当前已经存储的所有key集合|
|void	|putBool(java.lang.String key, boolean value)<br/>设置对应类型的数据|
|void	|putCollection(java.lang.String key, java.util.Collection<java.lang.Object> value)<br/>设置对应类型的数据|
|void	|putEntity(java.lang.String key, T value)<br/>设置对应类型的数据|
|void	|putFloat(java.lang.String key, float value)<br/>设置对应类型的数据|
|void	|putInt(java.lang.String key, int value)<br/>设置对应类型的数据|
|void	|putLong(java.lang.String key, long value)<br/>设置对应类型的数据|
|void	|putMap(java.lang.String key, java.util.Map<java.lang.String,java.lang.Object> value)<br/>设置对应类型的数据|
|void	|putString(java.lang.String key, java.lang.String value)<br/>设置对应类型的数据|
|void	|remove(java.lang.String key)<br/>删除指定的键值对|
|void	|removeAll()<br>删除所有|

