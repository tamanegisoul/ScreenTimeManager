package tamanegisoul.screentime;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;

public class MainService extends Service {

    private Timer timer;

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        for (ApplicationInfo info : getPackageManager().getInstalledApplications(BIND_AUTO_CREATE)) {
            Log.d("MainService", info.processName);
        }
        // 非同期（別スレッド）で定期的に処理を実行させるためにTimerを利用する
        // TODO: make the interval time configurable
        timer = new Timer();
        timer.schedule(new ValidateUsageTimeTimer(this), 0, 5000);

        // TODO: refactor
        // TODO: オーバーレイの権限ない時の対処
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Gravity.RIGHT,
                Gravity.BOTTOM,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        View view = layoutInflater.inflate(R.layout.overlay, null);
        wm.addView(view, params);
        final TextView v = (TextView) view.findViewById(R.id.textView1);
        final TextView v2 = (TextView) view.findViewById(R.id.textView2);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdateOverlayIntent.ACTION_UPDATE_OVERLAY);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(UpdateOverlayIntent.ACTION_UPDATE_OVERLAY)) {
                    // TODO: intentは自前クラスでbroadcastしてもただのIntentでreceiveするので自前クラスはやめる
                    UpdateOverlayIntent uIntent = new UpdateOverlayIntent(intent);
                    v.setText(String.valueOf(uIntent.getUsageTime()));
                    if (uIntent.isRestrictedAppInUse() && uIntent.getUsageTime() > PreferenceHelper.getRestrictedTime(context)) {
                        v2.setVisibility(View.VISIBLE);
                    } else {
                        v2.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }, intentFilter);

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
