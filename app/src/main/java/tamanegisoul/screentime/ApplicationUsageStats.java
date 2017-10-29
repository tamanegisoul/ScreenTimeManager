package tamanegisoul.screentime;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 一日の利用統計情報を扱う。GMT基準になってしまう。
 */
public class ApplicationUsageStats {

    /**
     * パッケージ名と利用時間のMap
     */
    private Map<String, Long> mMap = new HashMap<>();

    /**
     * 最後に利用されたアプリの名称
     */
    private String mLastUsedPackageName = "";

    /**
     * 総利用時間
     */
    private long mTotalUsageTime = 0;

    /**
     * @return 最後に利用されたアプリのパッケージ名
     */
    public String getLastUsedPackageName() {
        return mLastUsedPackageName;
    }

    /**
     * @param context Context
     * @return 各アプリの使用時間表示文字列
     */
    public String getDisplayString(Context context) {
        StringBuilder buffer = new StringBuilder();
        for (String packageName : mMap.keySet()) {
            buffer.append(ApplicationUtils.getApplicationLabel(context, packageName)).append("/").append(ApplicationUtils.getTimeString(mMap.get(packageName))).append("\n");
        }
        buffer.append("合計").append("/").append(ApplicationUtils.getTimeString(getTotalUsageTime())).append("\n");
        return buffer.toString();
    }

    /**
     * @return 総利用時間
     */
    public long getTotalUsageTime() {
        if (mTotalUsageTime == 0) {
            for (String key : mMap.keySet()) {
                mTotalUsageTime += mMap.get(key);
            }
        }
        return mTotalUsageTime;
    }

    /**
     * 最新の統計情報を取得する。
     * UsageStatsManager#queryUsageStats()では正しい値が取得できない（タイムゾーンのせい？）ので
     * UsageStatsManager#queryEvents()で取得したイベントから自分で計算する。
     * @param mContext context
     */
    public void refreshUsageStatsMap(Context mContext) {
        mMap.clear();
        mLastUsedPackageName = "";
        mTotalUsageTime = 0;

        // 今日の使用時間情報を取得
        // 使用時間が０になってしまう場合は、設定ーセキュリティー使用履歴にアクセスできるアプリを確認する。
        UsageStatsManager manager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);

        // 取得するイベントの開始と終了日時。
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR, 0);
        long from = mCalendar.getTimeInMillis();
        mCalendar.add(Calendar.DATE, 1);
        long to = mCalendar.getTimeInMillis();

        UsageEvents events = manager.queryEvents(from, to);
        String currentPackage = null;
        long currentTime = 0;
        while(events.hasNextEvent()){
            UsageEvents.Event event = new UsageEvents.Event();
            events.getNextEvent(event);
            Logger.d(this, event.getPackageName());
            if(event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                currentPackage = event.getPackageName();
                currentTime = event.getTimeStamp();
            } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                if(!event.getPackageName().equals(currentPackage)){
                    throw new RuntimeException();
                }else{
                    long time = 0;
                    if(mMap.containsKey(currentPackage)){
                        time = mMap.get(currentPackage);
                    }
                    if (PreferenceHelper.isRestrictedApp(mContext, currentPackage)) {
                        //Logger.d(this, packageName + " is restricted.");
                        mMap.put(currentPackage, time + event.getTimeStamp() - currentTime);
                    }
                }
            }
        }
    }

    /**
     * 最新の統計情報を取得する。
     *
     * @param mContext Context
    public void refreshUsageStatsMap_old(Context mContext) {
        mMap.clear();
        mLastUsedPackageName = "";
        mTotalUsageTime = 0;

        // 今日の使用時間情報を取得
        // 使用時間が０になってしまう場合は、設定ーセキュリティー使用履歴にアクセスできるアプリを確認する。
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
        long endTime = System.currentTimeMillis();
        List<UsageStats> usageStatsList = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, endTime - PreferenceHelper.getRestrictedTime(mContext) * 60 * 1000, endTime);

        String lastUsedPackageName = "";
        long lastUsedAppStartedTime = 0;

        // UsageStatsListが何故か二重に出る。たぶん後の方が正しいのでいったんMapに入れて後の方を使う
        String packageName;
        for (UsageStats usageStats : usageStatsList) {
            packageName = usageStats.getPackageName();
            long usedTime = usageStats.getTotalTimeInForeground();
            // 制限対象のアプリならカウント
            if (PreferenceHelper.isRestrictedApp(mContext, packageName)) {
                //Logger.d(this, packageName + " is restricted.");
                mMap.put(packageName, usedTime);
            }
            // 最後に起動したアプリは上記に含まれないので後で自分で計算する
            long lastTimeUsed = usageStats.getLastTimeUsed();
            if (lastUsedAppStartedTime < lastTimeUsed) {
                lastUsedAppStartedTime = lastTimeUsed;
                lastUsedPackageName = packageName;
            }
        }

        // 最後に起動したアプリ
        mLastUsedPackageName = lastUsedPackageName;

        // 最後に起動したアプリが制限対象ならカウント
        if (lastUsedAppStartedTime != 0 && PreferenceHelper.isRestrictedApp(mContext, lastUsedPackageName)) {
            long lastAppUsedTime = Calendar.getInstance().getTimeInMillis() - lastUsedAppStartedTime;
            if (mMap.containsKey(lastUsedPackageName)) {
                mMap.put(lastUsedPackageName, mMap.get(lastUsedPackageName) + lastAppUsedTime);
            } else {
                mMap.put(lastUsedPackageName, lastAppUsedTime);
            }
        }

        mUsageStatsManager = null;
        usageStatsList = null;
        lastUsedPackageName = null;
        packageName = null;
    }
     */

}
