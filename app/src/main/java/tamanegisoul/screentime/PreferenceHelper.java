package tamanegisoul.screentime;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;

public class PreferenceHelper {

    private static String KEY_PREFIX_RESTRICTED_APP = PreferenceHelper.class.getCanonicalName() + "/KEY_PREFIX_RESTRICTED_APP/";
    private static String KEY_ENABLE_RESTRICTION = PreferenceHelper.class.getCanonicalName() + "/KEY_ENABLE_RESTRICTION/";
    private static String KEY_RESTRICTED_TIME = PreferenceHelper.class.getCanonicalName() + "/KEY_RESTRICTED_TIME/";

    private static String getRestrictedAppKey(String packageName){
        return KEY_PREFIX_RESTRICTED_APP + packageName;
    }

    public static boolean isRestrictedApp(Context context, String packageName){
        String key = PreferenceHelper.getRestrictedAppKey(packageName);
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, true);
    }

    public static void setRestrictedApp(Context context, String packageName, boolean isRestricted) {
        String key = PreferenceHelper.getRestrictedAppKey(packageName);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, isRestricted).commit();
    }

    public static boolean isEnabledRestriction(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_ENABLE_RESTRICTION, false);
    }
    public static void setEnabledRestriction(Context context, boolean isRestricted){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_ENABLE_RESTRICTION, isRestricted).commit();
    }

    public static int getRestrictedTime(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_RESTRICTED_TIME, 60);
    }
    public static void setRestrictedTime(Context context, int time){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(KEY_RESTRICTED_TIME, time).commit();
    }
}
