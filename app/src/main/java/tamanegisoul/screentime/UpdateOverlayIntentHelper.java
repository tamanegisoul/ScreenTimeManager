package tamanegisoul.screentime;

import android.content.Intent;

public class UpdateOverlayIntentHelper {

    public static final String ACTION_UPDATE_OVERLAY = "ACTION_UPDATE_OVERLAY";

    private static final String KEY_TIME = "time";
    private static final String KEY_IS_RESTRICTED = "isRestricted";

    public static void replaceExtras(Intent intent, long usageTimeInMin, boolean isRestrictedAppInUse) {
        intent.putExtra(KEY_TIME, usageTimeInMin);
        intent.putExtra(KEY_IS_RESTRICTED, isRestrictedAppInUse);
        intent.setAction(ACTION_UPDATE_OVERLAY);
    }

    public static long getUsageTime(Intent intent) {
        return intent.getLongExtra(KEY_TIME, 0);
    }

    public static boolean isRestrictedAppInUse(Intent intent) {
        return intent.getBooleanExtra(KEY_IS_RESTRICTED, false);
    }
}
