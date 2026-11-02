# Raqeem ProGuard & R8 Optimization Rules

# Keep Domain Entities & Enums
-keep class org.moh.raqeem.project.model.** { *; }
-keep class org.moh.raqeem.project.domain.model.** { *; }
-keepclassmembers enum org.moh.raqeem.project.model.ExpenseCategory { *; }

# Keep Coroutines & Compose Multiplatform
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class androidx.compose.** { *; }

# Strip Debug Logging in Production
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}
