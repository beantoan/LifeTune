# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

####################################################################################################
# Preserve all annotations
####################################################################################################
-keepattributes *Annotation*

####################################################################################################
# Firebase Authentication 11.4.2
# https://firebase.google.com/docs/auth/android/start/
####################################################################################################
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

####################################################################################################
# Firebase Authentication Facebook 11.4.2
# https://firebase.google.com/docs/auth/android/facebook-login?utm_source=studio
####################################################################################################
-dontwarn com.facebook.**

####################################################################################################
# OkHttp 3.9.0
# https://github.com/square/okhttp
####################################################################################################
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

####################################################################################################
# Picasso 2.5.2
# https://github.com/square/picasso
####################################################################################################
-dontwarn com.squareup.okhttp.**

####################################################################################################
# MPAndroidChart 3.0.3
# https://github.com/PhilJay/MPAndroidChart/wiki/Proguard
####################################################################################################
-keep class com.github.mikephil.charting.** { *; }


# Keep public classes and methods.

-keep public class org.simpleframework.**{ *; }
-keep public class org.simpleframework.xml.**{ *; }
-keep public class org.simpleframework.xml.core.**{ *; }
-keep public class org.simpleframework.xml.util.**{ *; }
-keep public class org.simpleframework.xml.stream.**{ *; }
-keep public class javax.** { *; }
-keep public class javax.xml.stream.**{ *; }

-keep public class org.simpleframework.** {
  public void set*(***);
  public *** get*();
}

-dontwarn com.bea.xml.stream.**
-dontwarn org.simpleframework.xml.stream.**
-keep class org.simpleframework.xml.**{ *; }
-keepclassmembers,allowobfuscation class * {
    @org.simpleframework.xml.* <fields>;
    @org.simpleframework.xml.* <init>(...);
}

# Preserve all public classes, and their public and protected fields and
# methods.

-keep public class * {
    public protected *;
}

# Preserve all .class method names.

-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your library doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Your library may contain more items that need to be preserved;
# typically classes that are dynamically created using Class.forName:

-keep interface org.simpleframework.xml.core.Label {
   public *;
}
-keep class * implements org.simpleframework.xml.core.Label {
   public *;
}
-keep interface org.simpleframework.xml.core.Parameter {
   public *;
}
-keep class * implements org.simpleframework.xml.core.Parameter {
   public *;
}
-keep interface org.simpleframework.xml.core.Extractor {
   public *;
}
-keep class * implements org.simpleframework.xml.core.Extractor {
   public *;
}