# XML Pull Parser
-dontwarn org.xmlpull.v1.**
-keep class org.xmlpull.v1.** { *; }
-keep class android.content.res.XmlResourceParser { *; }
-keep class org.xmlpull.v1.XmlPullParser { *; }
-keep class org.xmlpull.v1.XmlPullParserFactory { *; }
-keep class org.kxml2.io.** { *; }

# Other existing rules...
#-keep class com.gallerymodul.* { ; }
#-dontwarn android.graphics.Canvas
#-dontwarn com.gallerymodul.**
#-dontwarn org.apache.**

-keep class org.wysaid.nativePort.CGENativeLibrary { *; }
-keepclassmembers class org.wysaid.nativePort.CGENativeLibrary { *; }
-keepclassmembers class com.gallery.photos.editpic.ModelClass { *; }

-keep class org.wysaid.nativePort.CGENativeLibrary { *; }
-keepclassmembers class org.wysaid.nativePort.CGENativeLibrary { *; }
-keep class org.wysaid.nativePort.CGEImageHandler { *; }
-keepclassmembers class org.wysaid.nativePort.CGEImageHandler { *; }


# Keep Fragment and Activity names
-keepnames class * extends androidx.fragment.app.Fragment
-keepnames class * extends android.app.Activity

# Picasso
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# RenderScript
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class androidx.renderscript.* {  }

# Reprint
-keep class com.github.ajalt.reprint.module.* {  }

# Gallery
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

#todo -------------- Encrypt All Code -----------------

-obfuscationdictionary "C:\Users\DREAMWORLD\Desktop\progard\encrypted.txt"
-packageobfuscationdictionary "C:\Users\DREAMWORLD\Desktop\progard\encrypted.txt"
-classobfuscationdictionary "C:\Users\DREAMWORLD\Desktop\progard\encrypted.txt"

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString

#Common
-renamesourcefileattribute SourceFile
-keepattributes SourceFile, LineNumberTable

-dontwarn com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
-dontwarn com.bumptech.glide.load.resource.bitmap.Downsampler
-dontwarn com.bumptech.glide.load.resource.bitmap.HardwareConfigState
-dontwarn com.bumptech.glide.manager.RequestManagerRetriever

-keep public class * extends java.lang.Exception

-keep class android.support.v7.widget.SearchView { *; }
-keep class com.gallerymodul.gallerycommon.models.PhoneNumber { *; }

# Joda
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.* {  }
-keep interface org.joda.time.* {  }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn javax.swing.tree.TreeNode

#Gson https://github.com/google/gson/blob/main/gson/src/main/resources/META-INF/proguard/gson.pro
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

-if class com.google.gson.reflect.TypeToken
-keep,allowobfuscation class com.google.gson.reflect.TypeToken

-keep,allowobfuscation class * extends com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowoptimization @com.google.gson.annotations.JsonAdapter class *

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.Expose <fields>;
  @com.google.gson.annotations.JsonAdapter <fields>;
  @com.google.gson.annotations.Since <fields>;
  @com.google.gson.annotations.Until <fields>;
}

-keepclassmembers class * extends com.google.gson.TypeAdapter {
  <init>();
}
-keepclassmembers class * implements com.google.gson.TypeAdapterFactory {
  <init>();
}
-keepclassmembers class * implements com.google.gson.JsonSerializer {
  <init>();
}
-keepclassmembers class * implements com.google.gson.JsonDeserializer {
  <init>();
}

-if class *
-keepclasseswithmembers,allowobfuscation class <1> {
  @com.google.gson.annotations.SerializedName <fields>;
}
-if class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers,allowobfuscation,allowoptimization class <1> {
  <init>();
}

# Firebase Analytics
-keep class com.google.firebase.analytics.* {  }
-keep class com.google.firebase.messaging.* {  }
-keep class com.google.firebase.auth.* {  }
-keep class com.google.firebase.firestore.* {  }
-keep class com.google.firebase.database.* {  }
-keep class com.google.firebase.crashlytics.* {  }

# Prevent Firebase and its internal classes from being removed
-keep class com.google.android.gms.common.internal.safeparcel.SafeParcelable { *; }
-keep class com.google.firebase.iid.FirebaseInstanceId { *; }
-keep class com.google.firebase.messaging.FirebaseMessaging { *; }

# Firebase Analytics
-keep class com.google.firebase.analytics.FirebaseAnalytics { *; }

# Firebase Proguard Rules

# Firebase Analytics
-keep class com.google.firebase.analytics.* { }

# Firebase Firestore
-keep class com.google.firebase.firestore.* {  }

# Firebase Authentication
-keep class com.google.firebase.auth.* {  }

# Firebase Cloud Messaging
-keep class com.google.firebase.messaging.* { }

# Firebase Crashlytics
-keep class com.google.firebase.crashlytics.* {  }

# Firebase Instance ID
-keep class com.google.firebase.iid.FirebaseInstanceId { *; }

# Preventing Google Play services classes from being obfuscated
-keep class com.google.android.gms.common.internal.safeparcel.SafeParcelable { *; }
-keep class com.google.android.gms.measurement.AppMeasurement { *; }

# Don't obfuscate Firebase's private classes
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Keep XML parsing classes
-keep class org.xmlpull.** { *; }
-keep class android.content.res.XmlResourceParser { *; }
-keep class org.kxml2.io.KXmlParser { *; }
-keep class org.kxml2.io.KXmlSerializer { *; }

# Ensure XmlPullParser is treated as a library class

# Prevent R8 from removing XML parsing-related classes

# Ensure META-INF services are not stripped
-keepattributes *Annotation*

# Keep kxml2 classes
-keep class org.kxml2.** { *; }

# Avoid conflicts with Android's built-in XmlPullParser
-dontwarn org.kxml2.**

# Keep kxml2 classes

# Avoid conflicts with Android's built-in XmlPullParser

# Keep META-INF/services files

# Other existing rules...


#todo -------------- All Log gone -----------------
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
    public static int wtf(...);
}


# Keep XML parsing classes
-keep class org.xmlpull.** { *; }
-keep class android.content.res.XmlResourceParser { *; }
-keep class org.kxml2.io.KXmlParser { *; }
-keep class org.kxml2.io.KXmlSerializer { *; }

# Prevent R8 from treating XmlPullParser as a user-defined class
-dontwarn org.xmlpull.v1.**
-keep class org.xmlpull.v1.** { *; }

# Prevent R8 from stripping XML parsing classes
-keep class org.xmlpull.v1.XmlPullParserFactory { *; }
-keep class org.kxml2.io.** { *; }

# Ensure META-INF services are not stripped
-keepattributes *Annotation*



-keep class org.wysaid.nativePort.CGENativeLibrary { *; }