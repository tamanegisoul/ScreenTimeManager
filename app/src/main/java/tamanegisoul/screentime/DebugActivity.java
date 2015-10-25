package tamanegisoul.screentime;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        startService(new Intent(this, MainService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MainService.class));
    }

    public void onClick_startMainActivity(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onClick_startScreenLockActivity(View view) {
        startActivity(new Intent(this, ScreenLockActivity.class));
    }

    public void onClick_startSettingsActivity(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onClick_startVerifyPasscodeActivity(View view) {
        startActivity(new Intent(this, VerifyPasscodeActivity.class));
    }

    public void onClick_showStats(View view) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE), 0, 0, 0);
        StringBuilder buffer = new StringBuilder();
        UsageStatsManager usageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, mCalendar.getTimeInMillis(), mCalendar.getTimeInMillis() + 24 * 60 * 60 * 1000);

        for (UsageStats usageStats : usageStatsList) {
            String packageName = usageStats.getPackageName();
            long usedTime = usageStats.getTotalTimeInForeground();
            buffer.append(packageName).append("\t").append(ApplicationUtils.getTimeString(usedTime)).append("\n");
        }
        ((TextView) findViewById(R.id.textView)).setText(buffer.toString());
    }
}
