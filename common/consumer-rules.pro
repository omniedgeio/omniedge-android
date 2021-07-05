# Removed log calls in proguard
-assumenosideeffects class android.util.Log {
public static *** v(...);
public static *** d(...);
public static *** i(...);
public static *** w(...);
#public static *** e(...);
}
# Removed OmniLog call cause it has no side effects
-assumenosideeffects class io.omniedge.OmniLog {
public static *** v(...);
public static *** d(...);
public static *** i(...);
public static *** w(...);
#public static *** e(...);
}