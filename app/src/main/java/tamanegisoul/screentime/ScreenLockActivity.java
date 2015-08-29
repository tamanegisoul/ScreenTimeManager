package tamanegisoul.screentime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ScreenLockActivity extends AppCompatActivity {

    public static String INTENT_SCREEN_EXTRA = "INTENT_SCREEN_EXTRA";
    public static String INTENT_SCREEN_OVERUSE = "INTENT_SCREEN_OVERUSE";
    public static String INTENT_SCREEN_DISABLED_APP = "INTENT_SCREEN_DISABLED_APP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_lock);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Display the current usage time.
        TextView textView = (TextView) findViewById(R.id.textView);
        if (getIntent().getStringExtra(INTENT_SCREEN_EXTRA) != null) {
            if (getIntent().getStringExtra(INTENT_SCREEN_EXTRA).equals(INTENT_SCREEN_DISABLED_APP)) {
                textView.setText("このアプリは使えません。");
            } else if (getIntent().getStringExtra(INTENT_SCREEN_EXTRA).equals(INTENT_SCREEN_OVERUSE)) {
                int currentUsageTime = PreferenceHelper.getCurrentUsageTime(this);
                textView.setText(getString(R.string.current_usage_time, currentUsageTime));
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, VerifyPasscodeActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick_close(View view){
        finish();
    }
}
