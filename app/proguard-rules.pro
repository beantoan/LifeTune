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
