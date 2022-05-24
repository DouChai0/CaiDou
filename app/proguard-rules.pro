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

-keep class bsh.* { *; }
-keep class bsh.*.* { *; }
-keep class com.ldq.connect.JavaPlugin.JavaPluginMethod { *; }
-keep class com.ldq.connect.JavaPlugin.JavaPluginMethod$RequestInfo { *; }
-keep class com.ldq.connect.MainWorker.WidgetHandler.Handler_Avatar_Long_Click_Common { *; }
-keep class com.ldq.connect.CInit { *; }
-keep class com.ldq.connect.JavaPlugin.JavaPlugin$MessageData { *; }
-keep class com.ldq.connect.JavaPlugin.JavaPluginUtils$GroupInfo { *; }
-keep class com.ldq.connect.JavaPlugin.JavaPluginUtils$GroupMemberInfo { *; }
-keep class com.ldq.connect.JavaPlugin.JavaPluginUtils$GroupBanInfo { *; }
-keep class com.ldq.connect.HookInstance.HookRecallMsg$TYSave { *; }
-keep class * implements java.io.Serializable { *; }
-keep class com.ldq.connect.Tools.MFixedDrawable{ *; }
-keep class com.android.** {*;}
-keep class com.google.zxing.** {*;}
-keep class com.tencent.** { *; }



-obfuscationdictionary MRules.txt
-classobfuscationdictionary MRules.txt
-packageobfuscationdictionary MRules.txt