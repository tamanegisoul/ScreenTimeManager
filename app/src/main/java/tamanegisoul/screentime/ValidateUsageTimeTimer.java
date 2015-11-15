package tamanegisoul.screentime;

import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.TimerTask;

public class ValidateUsageTimeTimer extends TimerTask {

    private Context mContext;
    private Calendar mCalendar;
    private ApplicationUsageStats mStats;

    public ValidateUsageTimeTimer(Context context) {
        super();
        mContext = context;
        mCalendar = Calendar.getInstance();
        mStats = new ApplicationUsageStats();
    }

    @Override
    public void run() {
        mStats.refreshUsageStatsMap(mContext, mCalendar);

        // TODO: refactor
        long a = mStats.getTotalUsageTime() / 1000 / 60;
        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra("time", a);
        broadcastIntent.setAction("MY_ACTION");
        mContext.sendBroadcast(broadcastIntent);

        // TODO: Activity表示はやめる。オーバーレイにする。
        if ((mStats.getLastUsedPackageName().equals("com.android.vending") && PreferenceHelper.isPlayStoreDisabled(mContext))
                || (mStats.getLastUsedPackageName().equals("com.android.settings") && PreferenceHelper.isSettingAppDisabled(mContext))) {
            Intent i = new Intent(mContext, ScreenLockActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(ScreenLockActivity.INTENT_EXTRA_SCREEN, ScreenLockActivity.INTENT_SCREEN_DISABLED_APP);
            mContext.startActivity(i);
        } else if (PreferenceHelper.isEnabledRestriction(mContext) && PreferenceHelper.isRestrictedApp(mContext, mStats.getLastUsedPackageName())) {
            // 制限対象のアプリが起動している場合にはロック画面表示
            if (mStats.getTotalUsageTime() > 1000 * 60 * PreferenceHelper.getRestrictedTime(mContext)) {
                Intent i = new Intent(mContext, ScreenLockActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(ScreenLockActivity.INTENT_EXTRA_SCREEN, ScreenLockActivity.INTENT_SCREEN_OVERUSE);
                i.putExtra(ScreenLockActivity.INTENT_EXTRA_TIME, mStats.getTotalUsageTime() / 1000 / 60);
                mContext.startActivity(i);
            }
        }

    }
}
