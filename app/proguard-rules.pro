# 移除所有日志代码
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# 允许 R8 移除未使用的类、方法、字段
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-allowaccessmodification

# 保留必要的注解（避免反射异常）
-keepattributes *Annotation*

# 合并重复类
-mergeinterfacesaggressively