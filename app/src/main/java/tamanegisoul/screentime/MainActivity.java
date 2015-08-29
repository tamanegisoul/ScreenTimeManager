package tamanegisoul.screentime;

import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初期設定未済の場合は初期設定する
        if (!PreferenceHelper.isInitialized(this)) {
            PreferenceHelper.initializePreferences(this);
        }

        // MainServiceが起動していなかったら起動する
        if (!ApplicationUtils.isMainServiceStarted(this)) {
            startService(new Intent(this, MainService.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // アプリケーションの使用状況を取得できない場合はユーザに設定するよう促す
        if (!ApplicationUtils.isUsageStatsAccessible(this)) {
            showSecuritySettingDialog();
        }

        // パスコードが設定されていない場合はパスコードの設定を促す
        if (!PreferenceHelper.isPasscodeSet(this)) {
            showSettingDialog();
        }

        refreshView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, VerifyPasscodeActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * セキュリティ設定（アプリケーション使用状況のアクセス許可）をするよう通知し設定画面を表示する。
     */
    private void showSecuritySettingDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.dialog_security_settings);
        alertDialogBuilder.setMessage(R.string.turn_on_usage_access);
        alertDialogBuilder.setPositiveButton(R.string.action_OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent("android.settings.USAGE_ACCESS_SETTINGS"));
                    }
                });
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * パスコードを設定するよう通知し設定画面を表示する
     */
    private void showSettingDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.dialog_security_settings);
        alertDialogBuilder.setMessage(R.string.turn_on_usage_access);
        alertDialogBuilder.setPositiveButton(R.string.action_OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                    }
                });
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * 画面表示データを更新する
     */
    private void refreshView() {
        // 利用時間の集計
        long totalTime = 0;
        String lastUsedPackageName = null;
        long lastUsedAppStartedTime = 0;
        StringBuilder buffer = new StringBuilder();
        for (UsageStats usageStats : getUsageStatsListToday()) {
            String packageName = usageStats.getPackageName();
            long usedTime = usageStats.getTotalTimeInForeground();
            Logger.d(this, packageName + " is used for " + getTimeString(usedTime) + " msec.");
            // 制限対象のアプリならカウント
            if (PreferenceHelper.isRestrictedApp(this, packageName)) {
                Logger.d(this, packageName + " is restricted.");
                buffer.append(ApplicationUtils.getApplicationLabel(this, packageName)).append("/").append(getTimeString(usedTime)).append("\n");
                totalTime += usedTime;
            }
            // 最後に起動したアプリは上記に含まれないので自分で計算する
            long lastTimeUsed = usageStats.getLastTimeUsed();
            if (lastUsedAppStartedTime < lastTimeUsed) {
                lastUsedAppStartedTime = lastTimeUsed;
                lastUsedPackageName = packageName;
            }
        }
        // 制限対象のアプリならカウント
        if (lastUsedAppStartedTime != 0 && PreferenceHelper.isRestrictedApp(this, lastUsedPackageName)) {
            long lastAppUsedTime = Calendar.getInstance().getTimeInMillis() - lastUsedAppStartedTime;
            Logger.d(this, lastUsedPackageName + " is now in use for " + getTimeString(lastAppUsedTime) + " msec");
            buffer.append(ApplicationUtils.getApplicationLabel(this, lastUsedPackageName)).append("/").append(getTimeString(lastAppUsedTime)).append("\n");
            totalTime += lastAppUsedTime;
        }
        buffer.append("合計").append("/").append(getTimeString(totalTime)).append("\n");

        // 各アプリの利用時間
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(buffer.toString());

        // 合計利用時間
        textView = (TextView) findViewById(R.id.textView_current_usage_time);
        textView.setText(getString(R.string.current_usage_time, totalTime / 1000 / 60));

        // 制限時間
        textView = (TextView) findViewById(R.id.textView_restricted_time);
        textView.setText(getString(R.string.restricted_time, PreferenceHelper.getRestrictedTime(this)));

        // インストールした日
        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo("tamanegisoul.screentime", PackageManager.GET_META_DATA);
            textView = (TextView) findViewById(R.id.textView_installed_time);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(packageInfo.firstInstallTime);
            textView.setText(getString(R.string.installed_time, cal));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 今日の利用統計
     */
    private List<UsageStats> getUsageStatsListToday() {
        // 今日の使用時間情報を取得
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        return usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.getTimeInMillis(), calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
    }

    /**
     * @param timeInMillisec ミリ秒
     * @return ミリ秒を時間として時:分:秒.ミリ秒に変換した文字列
     */
    private String getTimeString(long timeInMillisec) {
        long hours = timeInMillisec / 1000 / 60 / 60;
        long minutes = (timeInMillisec - hours * 1000 * 60 * 60) / 1000 / 60;
        long seconds = (timeInMillisec - hours * 1000 * 60 * 60 - minutes * 1000 * 60) / 1000;
        long milliSeconds = (timeInMillisec - hours * 1000 * 60 * 60 - minutes * 1000 * 60 - seconds * 1000);
        return String.valueOf(hours) + ":" + String.valueOf(minutes) + ":" + String.valueOf(seconds) + "." + String.valueOf(milliSeconds);
    }

}
