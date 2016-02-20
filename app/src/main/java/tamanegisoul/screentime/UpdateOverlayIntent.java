package tamanegisoul.screentime;

import android.content.Intent;

public class UpdateOverlayIntent extends Intent {

    public static final String ACTION_UPDATE_OVERLAY = "ACTION_UPDATE_OVERLAY";

    private static final String KEY_TIME = "time";
    private static final String KEY_IS_RESTRICTED = "isRestricted";

    public UpdateOverlayIntent(long usageTimeInMin, boolean isRestrictedAppInUse) {
        super();
        putExtra(KEY_TIME, usageTimeInMin);
        putExtra(KEY_IS_RESTRICTED, isRestrictedAppInUse);
        setAction(ACTION_UPDATE_OVERLAY);
    }

    public UpdateOverlayIntent(Intent intent) {
        super();
        putExtra(KEY_TIME, intent.getLongExtra(KEY_TIME, 0));
        putExtra(KEY_IS_RESTRICTED, intent.getBooleanExtra(KEY_IS_RESTRICTED, false));
        setAction(ACTION_UPDATE_OVERLAY);
    }

    public long getUsageTime() {
        return getLongExtra(KEY_TIME, 0);
    }

    public boolean isRestrictedAppInUse() {
        return getBooleanExtra(KEY_IS_RESTRICTED, false);
    }
}
