package tamanegisoul.screentime;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

public class PreferenceHelper {

    public static void initializePreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.key_pref_restricted_app_prefix) + "com.android.systemui";
        // KEY_CURRENT_USAGE_TIMEが設定されているタイミングがあったりするので
        // システムUIの除外設定有無で初期化済かどうかを判定する。
        if (!preferences.contains(key)) {
            for (ApplicationInfo info : ApplicationUtils.getSystemApplicationInfoList(context)) {
                Log.d("PreferenceHelper", info.packageName + " is not restricted.");
                PreferenceHelper.setRestrictedApp(context, info.packageName, false);
            }
        }
    }

    public static boolean isRestrictedApp(Context context, String packageName){
        String key = context.getString(R.string.key_pref_restricted_app_prefix) + packageName;
        return Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(key, String.valueOf(true)));
    }

    public static void setRestrictedApp(Context context, String packageName, boolean isRestricted) {
        String key = context.getString(R.string.key_pref_restricted_app_prefix) + packageName;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, String.valueOf(isRestricted)).commit();
    }

    public static boolean isEnabledRestriction(Context context){
        String key = context.getString(R.string.key_pref_is_restriction_enabled);
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    public static boolean isPlayStoreDisabled(Context context) {
        String key = context.getString(R.string.key_pref_is_disable_app_store);
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    public static boolean isSettingAppDisabled(Context context) {
        String key = context.getString(R.string.key_pref_is_disable_app_settings);
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    /**
     * @param context context
     * @return restricted time in minutes
     */
    public static int getRestrictedTime(Context context){
        String key = context.getString(R.string.key_pref_restricted_time);
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(key, "60"));
    }
    public static int getAuthSessionTimeout(Context context){
        String key = context.getString(R.string.key_pref_auth_session_timeout);
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(key, "3"));
    }

    public static boolean isPasscodeSet(Context context) {
        String key = context.getString(R.string.key_pref_passcode);
        return PreferenceManager.getDefaultSharedPreferences(context).contains(key);
    }
    public static boolean isPasscodeValid(Context context, String passcode){
        String key = context.getString(R.string.key_pref_passcode);
        return passcode.equals(PreferenceManager.getDefaultSharedPreferences(context).getString(key, "0000"));
    }

    // 以下はPreferenceでないけど保存しているもの
    public static int getCurrentUsageTime(Context context){
        String key = context.getString(R.string.key_current_usage_time);
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(key, "0"));
    }
    public static void setCurrentUsageTime(Context context, int time){
        String key = context.getString(R.string.key_current_usage_time);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, String.valueOf(time)).commit();
    }

    public static void setAuthTimestamp(Context context) {
        String key = context.getString(R.string.key_auth_timestamp);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key, new Date().getTime()).commit();
    }
    public static long getAuthTimestamp(Context context) {
        String key = context.getString(R.string.key_auth_timestamp);
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, 0);
    }
}
