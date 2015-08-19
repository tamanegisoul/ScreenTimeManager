package tamanegisoul.screentime;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
        timer.schedule(new TimerTask() {
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
        }, 0, 5000);
    }
}
