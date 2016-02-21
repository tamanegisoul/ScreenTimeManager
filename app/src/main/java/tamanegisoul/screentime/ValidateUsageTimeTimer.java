package tamanegisoul.screentime;

import android.content.Context;
import android.content.Intent;

import java.util.TimerTask;

public class ValidateUsageTimeTimer extends TimerTask {

    private Context mContext;
    private ApplicationUsageStats mStats;
    private Intent mOverlayIntent;
    private Intent mDisplayLockIntent;

    public ValidateUsageTimeTimer(Context context) {
        super();
        mContext = context;
        mStats = new ApplicationUsageStats();
        mOverlayIntent = new Intent();
        mDisplayLockIntent = new Intent(mContext, ScreenLockActivity.class);
        mDisplayLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mDisplayLockIntent.putExtra(ScreenLockActivity.INTENT_EXTRA_SCREEN, ScreenLockActivity.INTENT_SCREEN_DISABLED_APP);
    }

    @Override
    public void run() {
        mStats.refreshUsageStatsMap(mContext);

        // オーバーレイ表示を更新
        // receiver側で判定しても良いのだが、receiverはmStatsを持っていないので。
        long usageTime = mStats.getTotalUsageTime() / 1000 / 60;
        boolean isRestricted = PreferenceHelper.isEnabledRestriction(mContext) && PreferenceHelper.isRestrictedApp(mContext, mStats.getLastUsedPackageName());
        UpdateOverlayIntentHelper.replaceExtras(mOverlayIntent, usageTime, isRestricted);
        mContext.sendBroadcast(mOverlayIntent);

        // Playストアまたは設定を禁止されているのに起動された場合はロック画面を表示
        if ((mStats.getLastUsedPackageName().equals("com.android.vending") && PreferenceHelper.isPlayStoreDisabled(mContext))
                || (mStats.getLastUsedPackageName().equals("com.android.settings") && PreferenceHelper.isSettingAppDisabled(mContext))) {
            mContext.startActivity(mDisplayLockIntent);
        }

        System.gc();
    }
}
