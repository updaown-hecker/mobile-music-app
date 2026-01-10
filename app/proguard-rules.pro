# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in 'proguard-android-optimize.txt' which is shipped with the Android plugin.

# Keep Room entities and DAOs
-keep class com.updaown.musicapp.data.** { *; }

# Keep ViewModel
-keep class com.updaown.musicapp.ui.** { *; }

# Serialization (if any)
-keepattributes Signature
-keepattributes *Annotation*
