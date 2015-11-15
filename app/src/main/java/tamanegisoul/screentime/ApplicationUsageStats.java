package tamanegisoul.screentime;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
     *
     * @param mContext  Context
     * @param mCalendar Calendar
     */
    public void refreshUsageStatsMap(Context mContext, Calendar mCalendar) {
        // TODO:remove mCalendar from param
        mMap.clear();
        mLastUsedPackageName = "";
        mTotalUsageTime = 0;

        // 今日の使用時間情報を取得
        // 使用時間が０になってしまう場合は、設定ーセキュリティー使用履歴にアクセスできるアプリを確認する。
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
        long endTime = System.currentTimeMillis();
        List<UsageStats> usageStatsList = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, endTime - 30 * 1000, endTime);

        String lastUsedPackageName = "";
        long lastUsedAppStartedTime = 0;

        // UsageStatsListが何故か二重に出る。たぶん後の方が正しいのでいったんMapに入れて後の方を使う
        for (UsageStats usageStats : usageStatsList) {
            String packageName = usageStats.getPackageName();
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
        // Marshmarrowだと自動でカウントしてくれるようになった模様
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (lastUsedAppStartedTime != 0 && PreferenceHelper.isRestrictedApp(mContext, lastUsedPackageName)) {
                long lastAppUsedTime = Calendar.getInstance().getTimeInMillis() - lastUsedAppStartedTime;
                if (mMap.containsKey(lastUsedPackageName)) {
                    mMap.put(lastUsedPackageName, mMap.get(lastUsedPackageName) + lastAppUsedTime);
                } else {
                    mMap.put(lastUsedPackageName, lastAppUsedTime);
                }
            }
        }
    }

}
