package tamanegisoul.screentime;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

public class ValidateUsageTimeTimer extends TimerTask {

    public static String ACTION_UPDATE_USAGE_TIME = ValidateUsageTimeTimer.class.getCanonicalName() + "/ACTION_UPDATE_USAGE_TIME";
    public static String TIME = ValidateUsageTimeTimer.class.getCanonicalName() + "/TIME";

    private Context mContext;

    public ValidateUsageTimeTimer(Context context){
        super();
        mContext = context;
    }

    @Override
    public void run() {
        // 今日の使用時間情報を取得
        UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.getTimeInMillis(), calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);

        long totalTime = 0;
        String lastUsedPackageName = "";
        long lastUsedAppStartedTime = 0;
        for(UsageStats usageStats : usageStatsList){
            String packageName = usageStats.getPackageName();
            long usedTime = usageStats.getTotalTimeInForeground();
            Logger.d(this, packageName + " is used for " + String.valueOf(usedTime) + " msec.");
            // 制限対象のアプリならカウント
            if(PreferenceHelper.isRestrictedApp(mContext,packageName)){
                Logger.d(this, packageName + " is restricted.");
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
        if(PreferenceHelper.isRestrictedApp(mContext, lastUsedPackageName)){
            long lastAppUsedTime = Calendar.getInstance().getTimeInMillis() - lastUsedAppStartedTime;
            Logger.d(this, lastUsedPackageName + " is now in use for " + String.valueOf(lastAppUsedTime) + " msec.");
            totalTime += lastAppUsedTime;
        }

        Intent intent = new Intent(ACTION_UPDATE_USAGE_TIME);
        intent.putExtra(TIME, totalTime / 1000 / 60);
        mContext.sendBroadcast(intent);

        Logger.d(this, "Total time is " + String.valueOf(totalTime));

        // 制限対象のアプリが起動している場合にはロック画面表示
        if(PreferenceHelper.isEnabledRestriction(mContext) && PreferenceHelper.isRestrictedApp(mContext, lastUsedPackageName)){
            if(totalTime > 1000 * 60 * PreferenceHelper.getRestrictedTime(mContext)) {
                Intent i = new Intent(mContext, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        }

    }
}
