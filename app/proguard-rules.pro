# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Room entities and data models used by Gson TypeConverters
-keep class com.daybrief.app.data.** { *; }
-keep class com.daybrief.app.viewmodel.ActionItem { *; }
-keep class com.daybrief.app.viewmodel.PendingIssue { *; }
-keep class com.daybrief.app.viewmodel.DayEvent { *; }
