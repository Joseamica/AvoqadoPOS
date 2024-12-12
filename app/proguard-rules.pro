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

# Keep specific classes
-keep class com.google.android.material.** { *; }
-keep class com.google.android.material.R$* { *; }

-dontwarn com.decodelibrary.R$raw
-dontwarn com.thoughtworks.xstream.XStream

-dontwarn android.databinding.Bindable
-dontwarn android.databinding.BindingAdapter
-dontwarn android.databinding.BindingConversion
-dontwarn android.databinding.BindingMethod
-dontwarn android.databinding.BindingMethods
-dontwarn android.databinding.InverseBindingAdapter
-dontwarn android.databinding.InverseBindingMethod
-dontwarn android.databinding.InverseBindingMethods
-dontwarn android.databinding.InverseMethod
-dontwarn android.databinding.Untaggable
-dontwarn javax.lang.model.SourceVersion
-dontwarn javax.lang.model.element.AnnotationMirror
-dontwarn javax.lang.model.element.AnnotationValue
-dontwarn javax.lang.model.element.Element
-dontwarn javax.lang.model.element.ElementKind
-dontwarn javax.lang.model.element.ExecutableElement
-dontwarn javax.lang.model.element.Modifier
-dontwarn javax.lang.model.element.Name
-dontwarn javax.lang.model.element.PackageElement
-dontwarn javax.lang.model.element.TypeElement
-dontwarn javax.lang.model.element.VariableElement
-dontwarn javax.lang.model.type.ArrayType
-dontwarn javax.lang.model.type.DeclaredType
-dontwarn javax.lang.model.type.ExecutableType
-dontwarn javax.lang.model.type.IntersectionType
-dontwarn javax.lang.model.type.MirroredTypeException
-dontwarn javax.lang.model.type.NoType
-dontwarn javax.lang.model.type.PrimitiveType
-dontwarn javax.lang.model.type.TypeKind
-dontwarn javax.lang.model.type.TypeMirror
-dontwarn javax.lang.model.type.TypeVariable
-dontwarn javax.lang.model.type.TypeVisitor
-dontwarn javax.lang.model.type.UnionType
-dontwarn javax.lang.model.type.WildcardType
-dontwarn javax.lang.model.util.ElementFilter
-dontwarn javax.lang.model.util.Elements
-dontwarn javax.lang.model.util.SimpleTypeVisitor6
-dontwarn javax.lang.model.util.SimpleTypeVisitor7
-dontwarn javax.lang.model.util.Types
-dontwarn javax.tools.Diagnostic$Kind
-dontwarn javax.tools.JavaFileObject

# Keep specific libraries
-keep class retrofit2.** { *; }
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-keep class org.apache.commons.codec.** { *; }
-dontwarn retrofit2.**
-dontwarn com.squareup.okhttp3.**
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keep interface java.util.function.**
# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation


# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, keep it.
-keepattributes Signature

# This is also needed for R8 in compat mode since multiple
# optimizations will remove the generic signature such as class
# merging and argument removal. See:
# https://r8.googlesource.com/r8/+/refs/heads/main/compatibility-faq.md#troubleshooting-gson-gson
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Optional. For using GSON @Expose annotation
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations


# Keep specific packages
-keep class com.urovo.** { *; }
-keep class com.menta.** { *; }
-keep class android.device.DeviceManager { *; }


# Keep public methods in Room database
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public <init>(...);
    public abstract *;
}

# Keep all classes annotated with @androidx.room.* annotations
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.* class * { *; }

-keep public class * extends android.database.sqlite.SQLiteOpenHelper
-keep class android.database.sqlite.** { *; }
-keep class android.** { *; }

# Keep public methods in SQLiteOpenHelper
-keep public class * extends android.database.sqlite.SQLiteOpenHelper {
    public <init>(...);
    public abstract *;
}

-keep class org.bouncycastle.** { *; }

-dontwarn com.google.zxing.BarcodeFormat
-dontwarn com.google.zxing.EncodeHintType
-dontwarn com.google.zxing.MultiFormatWriter
-dontwarn com.google.zxing.WriterException
-dontwarn com.google.zxing.common.BitMatrix
-dontwarn com.google.zxing.qrcode.QRCodeWriter
-dontwarn dagger.Binds
-dontwarn dagger.Module
-dontwarn dagger.Provides
-dontwarn dagger.hilt.InstallIn
-dontwarn dagger.hilt.android.components.ActivityRetainedComponent
-dontwarn dagger.hilt.android.components.ViewModelComponent
-dontwarn dagger.hilt.android.internal.lifecycle.HiltViewModelMap$KeySet
-dontwarn dagger.hilt.android.internal.lifecycle.HiltViewModelMap
-dontwarn dagger.hilt.android.lifecycle.HiltViewModel
-dontwarn dagger.hilt.android.qualifiers.ApplicationContext
-dontwarn dagger.hilt.codegen.OriginatingElement
-dontwarn dagger.hilt.components.SingletonComponent
-dontwarn dagger.internal.DaggerGenerated
-dontwarn dagger.internal.Factory
-dontwarn dagger.internal.Preconditions
-dontwarn dagger.internal.QualifierMetadata
-dontwarn dagger.internal.ScopeMetadata
-dontwarn dagger.multibindings.IntoMap
-dontwarn dagger.multibindings.LazyClassKey
-dontwarn javax.inject.Inject
-dontwarn javax.inject.Provider
-dontwarn javax.inject.Singleton
-dontwarn javax.naming.Binding
-dontwarn javax.naming.NamingEnumeration
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.directory.DirContext
-dontwarn javax.naming.directory.InitialDirContext
-dontwarn javax.naming.directory.SearchControls
-dontwarn javax.naming.directory.SearchResult
-dontwarn kotlinx.android.parcel.Parcelize
-dontwarn kotlinx.parcelize.Parcelize
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile