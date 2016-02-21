package tamanegisoul.screentime;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class PreferenceHelper {

    /**
     * @param context context
     * @return 初期化済みの場合true
     */
    public static boolean isInitialized(Context context) {
        // KEY_CURRENT_USAGE_TIMEが設定されているタイミングがあったりするので
        // システムUIの除外設定有無で初期化済かどうかを判定する。
        String key = context.getString(R.string.key_pref_restricted_app_prefix) + "com.android.systemui";
        return PreferenceManager.getDefaultSharedPreferences(context).contains(key);
    }

    /**
     * 初期化する。システム系アプリを非制限対象に設定する。
     *
     * @param context context
     */
    public static void initializePreferences(Context context) {
        for (ApplicationInfo info : ApplicationUtils.getSystemApplicationInfoList(context)) {
            Log.d("PreferenceHelper", info.packageName + " is not restricted.");
            PreferenceHelper.setRestrictedApp(context, info.packageName, false);
        }
    }

    public static boolean isRestrictedApp(Context context, String packageName) {
        String key = context.getString(R.string.key_pref_restricted_app_prefix) + packageName;
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, true);
    }

    public static void setRestrictedApp(Context context, String packageName, boolean isRestricted) {
        String key = context.getString(R.string.key_pref_restricted_app_prefix) + packageName;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, isRestricted).commit();
    }

    public static boolean isEnabledRestriction(Context context) {
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
    public static int getRestrictedTime(Context context) {
        String key;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            key = context.getString(R.string.key_pref_restricted_time_holiday);
        } else {
            key = context.getString(R.string.key_pref_restricted_time);
        }
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(key, "60"));
    }

    public static int getAuthSessionTimeout(Context context) {
        String key = context.getString(R.string.key_pref_auth_session_timeout);
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(key, "3"));
    }

    public static boolean isPasscodeSet(Context context) {
        String key = context.getString(R.string.key_pref_passcode);
        return PreferenceManager.getDefaultSharedPreferences(context).contains(key);
    }

    public static boolean isPasscodeValid(Context context, String passcode) {
        String key = context.getString(R.string.key_pref_passcode);
        return passcode.equals(PreferenceManager.getDefaultSharedPreferences(context).getString(key, "0000"));
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
