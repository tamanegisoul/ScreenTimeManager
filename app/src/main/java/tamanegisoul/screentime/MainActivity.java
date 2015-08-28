package tamanegisoul.screentime;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceHelper.initializePreferences(this);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUsageStatsDisplay();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUsageStatsDisplay();
    }

    private void updateUsageStatsDisplay() {
        // 今日の使用時間情報を取得
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.getTimeInMillis(), calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);

        StringBuilder builder = new StringBuilder();
        long totalTime = 0;
        String lastUsedPackageName = "";
        long lastUsedAppStartedTime = 0;
        for (UsageStats usageStats : usageStatsList) {
            String packageName = usageStats.getPackageName();
            long usedTime = usageStats.getTotalTimeInForeground();
            Logger.d(this, packageName + " is used for " + getTimeString(usedTime) + " msec.");
            // 制限対象のアプリならカウント
            if (PreferenceHelper.isRestrictedApp(this, packageName)) {
                Logger.d(this, packageName + " is restricted.");
                builder.append(getApplicationLabel(packageName)).append("/").append(getTimeString(usedTime)).append("\n");
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
            Logger.d(this, lastUsedPackageName + " is now in use for " + getTimeString(lastAppUsedTime) + " msec.");
            builder.append(getApplicationLabel(lastUsedPackageName)).append("/").append(getTimeString(lastAppUsedTime)).append("\n");
            totalTime += lastAppUsedTime;
        }

        builder.append("合計").append("/").append(getTimeString(totalTime)).append("\n");

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(builder.toString());
    }

    private String getApplicationLabel(String packageName) {
        PackageManager packageManager = getPackageManager();
        String label = null;
        try {
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            label = info.applicationInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return label;
    }

    private String getTimeString(long timeInMillisec) {
        StringBuilder builder = new StringBuilder();
        long hours = timeInMillisec / 1000 / 60 / 60;
        long minutes = (timeInMillisec - hours * 1000 * 60 * 60) / 1000 / 60;
        long seconds = (timeInMillisec - hours * 1000 * 60 * 60 - minutes * 1000 * 60) / 1000;
        long milliSeconds = (timeInMillisec - hours * 1000 * 60 * 60 - minutes * 1000 * 60 - seconds * 1000);
        builder.append(hours).append(":").append(minutes).append(":").append(seconds).append(".").append(milliSeconds);
        return builder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
