package tamanegisoul.screentime;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;

public class PreferenceHelper {

    private static String KEY_PREFIX_RESTRICTED_APP = PreferenceHelper.class.getCanonicalName() + "/KEY_PREFIX_RESTRICTED_APP/";
    private static String KEY_ENABLE_RESTRICTION = PreferenceHelper.class.getCanonicalName() + "/KEY_ENABLE_RESTRICTION/";
    private static String KEY_RESTRICTED_TIME = PreferenceHelper.class.getCanonicalName() + "/KEY_RESTRICTED_TIME/";
    private static String KEY_CURRENT_USAGE_TIME = PreferenceHelper.class.getCanonicalName() + "/KEY_CURRENT_USAGE_TIME/";

    private static String getRestrictedAppKey(String packageName){
        return KEY_PREFIX_RESTRICTED_APP + packageName;
    }

    public static boolean isRestrictedApp(Context context, String packageName){
        String key = PreferenceHelper.getRestrictedAppKey(packageName);
        return Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(key, String.valueOf(true)));
    }

    public static void setRestrictedApp(Context context, String packageName, boolean isRestricted) {
        String key = PreferenceHelper.getRestrictedAppKey(packageName);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, String.valueOf(isRestricted)).commit();
    }

    public static boolean isEnabledRestriction(Context context){
        String key = context.getString(R.string.key_pref_is_restriction_enabled);
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }
    public static void setEnabledRestriction(Context context, boolean isRestricted){
        String key = context.getString(R.string.key_pref_is_restriction_enabled);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, isRestricted).commit();
    }

    public static int getRestrictedTime(Context context){
        String key = context.getString(R.string.key_pref_restricted_time);
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(key, "60"));
    }

    public static void setRestrictedTime(Context context, int time){
        String key = context.getString(R.string.key_pref_restricted_time);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, String.valueOf(time)).commit();
    }

    public static int getCurrentUsageTime(Context context){
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_CURRENT_USAGE_TIME, "0"));
    }
    public static void setCurrentUsageTime(Context context, int time){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_CURRENT_USAGE_TIME, String.valueOf(time)).commit();
    }

    public static void setPasscode(Context context, String passcode){
        String key = context.getString(R.string.key_pref_passcode);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, passcode).commit();
    }
    public static boolean isPasscodeVaid(Context context, String passcode){
        String key = context.getString(R.string.key_pref_passcode);
        return passcode.equals(PreferenceManager.getDefaultSharedPreferences(context).getString(key, ""));
    }

}
