package tamanegisoul.screentime;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

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
        StringBuilder buffer = new StringBuilder();
        UsageStatsManager usageStatsManager = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);

        Map<String, String> usageStats = ApplicationUtils.getUsageStats(usageStatsManager);
        for (Map.Entry<String, String> entry : usageStats.entrySet()) {
            buffer.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
        }
        ((TextView) findViewById(R.id.textView)).setText(buffer.toString());
    }
}
