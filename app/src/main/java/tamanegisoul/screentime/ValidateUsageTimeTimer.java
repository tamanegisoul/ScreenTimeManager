package tamanegisoul.screentime;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

public class ValidateUsageTimeTimer extends TimerTask {

    private Context mContext;
    private Calendar mCalendar;
    private UsageStatsManager mUsageStatsManager;

    public ValidateUsageTimeTimer(Context context){
        super();
        mContext = context;
        mCalendar = Calendar.getInstance();
        mUsageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    @Override
    public void run() {
        // 今日の使用時間情報を取得
        // 使用時間が０になってしまう場合は、設定ーセキュリティー使用履歴にアクセスできるアプリを確認する。
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE), 0, 0, 0);
        List<UsageStats> usageStatsList = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, mCalendar.getTimeInMillis(), mCalendar.getTimeInMillis() + 24 * 60 * 60 * 1000);

        long totalTime = 0;
        String lastUsedPackageName = "";
        long lastUsedAppStartedTime = 0;
        for(UsageStats usageStats : usageStatsList){
            String packageName = usageStats.getPackageName();
            long usedTime = usageStats.getTotalTimeInForeground();
            //Logger.d(this, packageName + " is used for " + String.valueOf(usedTime) + " msec.");
            // 制限対象のアプリならカウント
            if (PreferenceHelper.isRestrictedApp(mContext, packageName)) {
                //Logger.d(this, packageName + " is restricted.");
                totalTime += usedTime;
            }
            // 最後に起動したアプリは上記に含まれないので自分で計算する
            long lastTimeUsed = usageStats.getLastTimeUsed();
            if(lastUsedAppStartedTime < lastTimeUsed){
                lastUsedAppStartedTime = lastTimeUsed;
                lastUsedPackageName = packageName;
            }
        }
        // 制限対象のアプリならカウント
        if(lastUsedAppStartedTime != 0 && PreferenceHelper.isRestrictedApp(mContext, lastUsedPackageName)){
            long lastAppUsedTime = Calendar.getInstance().getTimeInMillis() - lastUsedAppStartedTime;
            //Logger.d(this, lastUsedPackageName + " is now in use for " + String.valueOf(lastAppUsedTime) + " msec.");
            totalTime += lastAppUsedTime;
        }

        //Logger.d(this, "Total time is " + String.valueOf(totalTime));

        if ((lastUsedPackageName.equals("com.android.vending") && PreferenceHelper.isPlayStoreDisabled(mContext))
                || (lastUsedPackageName.equals("com.android.settings") && PreferenceHelper.isSettingAppDisabled(mContext))) {
            Logger.d(this, lastUsedPackageName + " is not allowed to use.");
            Intent i = new Intent(mContext, ScreenLockActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(ScreenLockActivity.INTENT_EXTRA_SCREEN, ScreenLockActivity.INTENT_SCREEN_DISABLED_APP);
            mContext.startActivity(i);
        } else if (PreferenceHelper.isEnabledRestriction(mContext) && PreferenceHelper.isRestrictedApp(mContext, lastUsedPackageName)) {
            // 制限対象のアプリが起動している場合にはロック画面表示
            if(totalTime > 1000 * 60 * PreferenceHelper.getRestrictedTime(mContext)) {
                Intent i = new Intent(mContext, ScreenLockActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(ScreenLockActivity.INTENT_EXTRA_SCREEN, ScreenLockActivity.INTENT_SCREEN_OVERUSE);
                i.putExtra(ScreenLockActivity.INTENT_EXTRA_TIME, totalTime / 1000 / 60);
                mContext.startActivity(i);
            }
        }

    }
}
