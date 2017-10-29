package tamanegisoul.screentime;

import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * ユーティリティクラス
 */
public class ApplicationUtils {

    /**
     * @param context context
     * @return MainServiceが起動済の場合はtrue
     */
    public static boolean isMainServiceStarted(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MainService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param context context
     * @return アプリケーションの使用状況のアクセス許可設定がONの場合true
     */
    public static boolean isUsageStatsAccessible(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.getTimeInMillis(), calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
        return usageStatsList.size() != 0;
    }

    /**
     * @param context context
     * @return システム系アプリケーション（制限設定の対象から除外するアプリ）のリスト
     */
    public static List<ApplicationInfo> getSystemApplicationInfoList(Context context) {
        List<ApplicationInfo> applicationInfoList = context.getPackageManager().getInstalledApplications(0);
        List<ApplicationInfo> list = new ArrayList<>();
        for (ApplicationInfo info : applicationInfoList) {
            if (isSystemApplication(info)) {
                list.add(info);
            }
        }
        return list;
    }

    /**
     * @param context context
     * @return アプリケーションラベルでソートされたアプリケーションの一覧。システム系アプリは除外。
     */
    public static List<ApplicationInfo> getInstalledApplications(Context context) {
        List<ApplicationInfo> applicationInfoList = context.getPackageManager().getInstalledApplications(0);
        List<ApplicationInfo> list = new ArrayList<>();
        for (ApplicationInfo info : applicationInfoList) {
            if (!isSystemApplication(info)) {
                list.add(info);
            }
        }
        final PackageManager packageManager = context.getPackageManager();
        Collections.sort(list, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                return lhs.loadLabel(packageManager).toString().compareTo(rhs.loadLabel(packageManager).toString());
            }
        });
        return list;
    }

    /**
     * @param info ApplicationInfo
     * @return システム系アプリケーションの場合はtrue
     */
    public static boolean isSystemApplication(ApplicationInfo info) {
        return ((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM
                && !info.packageName.equals("com.android.chrome")
                && !info.packageName.equals("com.android.settings")
                && !info.packageName.equals("com.android.vending")
                && !info.packageName.equals("com.google.android.youtube")
                && !info.packageName.equals("com.google.android.gm")
                && !info.packageName.equals("com.google.android.music")
                && !info.packageName.equals("com.google.android.videos")
                && !info.packageName.startsWith("com.google.android.apps")
        ) || info.packageName.equals("tamanegisoul.screentime")
                || info.packageName.equals("com.google.android.apps.cloudprint")
                || info.packageName.equals("com.google.android.apps.enterprise.dmagent")
                || info.packageName.equals("com.google.android.apps.gcs")
                || info.packageName.startsWith("com.google.android.apps.inputmethod")
                || info.packageName.equals("com.google.android.apps.walletnfcrel")
                ;
    }

    /**
     * @param context     context
     * @param packageName パッケージ名
     * @return アプリケーションのラベル。取得できない場合はnull。
     */
    public static String getApplicationLabel(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        String label = null;
        try {
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            label = info.applicationInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return label;
    }

    /**
     * @param timeInMillisec ミリ秒
     * @return ミリ秒を時間として時:分:秒.ミリ秒に変換した文字列
     */
    public static String getTimeString(long timeInMillisec) {
        long hours = timeInMillisec / 1000 / 60 / 60;
        long minutes = (timeInMillisec - hours * 1000 * 60 * 60) / 1000 / 60;
        long seconds = (timeInMillisec - hours * 1000 * 60 * 60 - minutes * 1000 * 60) / 1000;
        long milliSeconds = (timeInMillisec - hours * 1000 * 60 * 60 - minutes * 1000 * 60 - seconds * 1000);
        return String.valueOf(hours) + ":" + String.valueOf(minutes) + ":" + String.valueOf(seconds) + "." + String.valueOf(milliSeconds);
    }

    /**
     * DebugActivityから呼び出している。
     * @param manager manager
     * @return パッケージ名と使用時間のマップ
     */
    public static Map<String, String> getUsageStats(UsageStatsManager manager) {
        Calendar mCalendar = Calendar.getInstance(Locale.JAPAN);
        mCalendar.set(Calendar.HOUR, 0);
        long from = mCalendar.getTimeInMillis();
        mCalendar.add(Calendar.DATE, 1);
        long to = mCalendar.getTimeInMillis();

        UsageEvents events = manager.queryEvents(from, to);
        String currentPackage = null;
        long currentTime = 0;
        Map<String, Long> summary = new HashMap<>();
        while(events.hasNextEvent()){
            UsageEvents.Event event = new UsageEvents.Event();
            events.getNextEvent(event);
            if(event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                currentPackage = event.getPackageName();
                currentTime = event.getTimeStamp();
            } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                if(!event.getPackageName().equals(currentPackage)){
                    throw new RuntimeException();
                }else{
                    long time = 0;
                    if(summary.containsKey(currentPackage)){
                        time = summary.get(currentPackage);
                    }
                    summary.put(currentPackage, time + event.getTimeStamp() - currentTime);
                }
            }
        }

        Map<String, String> result = new HashMap<>();
        for(Map.Entry<String, Long> entry : summary.entrySet()){
            String timeString = ApplicationUtils.getTimeString(entry.getValue());
            result.put(entry.getKey(), timeString);
        }

        return result;
    }

    public static List<String> getHolidayList(Context context){
        Set<String> s = PreferenceHelper.getHolidayList(context);
        return new ArrayList<>(s);
    }

}
