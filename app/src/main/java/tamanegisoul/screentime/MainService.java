package tamanegisoul.screentime;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainService extends Service {

    private Timer timer;
    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        for(ApplicationInfo info : getPackageManager().getInstalledApplications(BIND_AUTO_CREATE)) {
            Log.d("MainService", info.processName);
        }
        // 非同期（別スレッド）で定期的に処理を実行させるためにTimerを利用する
        timer = new Timer();
        timer.schedule(new TimerTaskB(), 0, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.purge();
    }

    public class TimerTaskB extends TimerTask {
        @Override
        public void run() {
            Log.d("MainService", "run");
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
            UsageStatsManager m = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
            List<UsageStats> ss = m.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, c.getTimeInMillis(), c.getTimeInMillis() + 24 * 60 * 60 * 1000);
            long time = 0;
            for(UsageStats s : ss){
                try {
                    PackageInfo pi = getPackageManager().getPackageInfo(s.getPackageName(), 0);
                    String pn = pi.applicationInfo.loadLabel(getPackageManager()).toString();
                    if(!pn.equals("システムUI") && !pn.equals("Googleアプリ")){
                        time += s.getTotalTimeInForeground();
                    }
                    Log.d("MainService", pi.applicationInfo.loadLabel(getPackageManager()).toString());
                    Log.d("MainService", String.valueOf(s.getTotalTimeInForeground()));
                    Log.d("MainService", String.valueOf(s.toString()));
                } catch (PackageManager.NameNotFoundException e) {
                    Log.getStackTraceString(e);
                }
            }
            Log.d("MainService", String.valueOf(time));
            if(time > 1000 * 60 * 60) {
            //if(time > 1000) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getBaseContext().startActivity(i);
            }
        }
    }
    public class TimerTaskA extends TimerTask {
        @Override
        public void run() {
            Log.d("MainService", "run");
            ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo info : infos){
                if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    try {
                        PackageInfo pi = getPackageManager().getPackageInfo(info.processName, 0);
                        Log.d("MainService", pi.applicationInfo.loadLabel(getPackageManager()) + " is foreground 1.");
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.getStackTraceString(e);
                    }
                    break;
                }
            }
        }
    }
}
