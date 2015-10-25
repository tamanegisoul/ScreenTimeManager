package tamanegisoul.screentime;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;

public class MainService extends Service {

    private Timer timer;

    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        for (ApplicationInfo info : getPackageManager().getInstalledApplications(BIND_AUTO_CREATE)) {
            Log.d("MainService", info.processName);
        }
        // 非同期（別スレッド）で定期的に処理を実行させるためにTimerを利用する
        timer = new Timer();
        timer.schedule(new ValidateUsageTimeTimer(this), 0, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.purge();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
