package tamanegisoul.screentime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ScreenLockActivity extends AppCompatActivity {

    // Intentのパラメータキー
    public static String INTENT_EXTRA_SCREEN = "INTENT_EXTRA_SCREEN";
    // 使用時間超過の場合
    public static String INTENT_SCREEN_OVERUSE = "INTENT_SCREEN_OVERUSE";
    // 使用不可アプリの場合
    public static String INTENT_SCREEN_DISABLED_APP = "INTENT_SCREEN_DISABLED_APP";
    // 使用時間のパラメータキー
    public static String INTENT_EXTRA_TIME = "INTENT_EXTRA_TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_lock);
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView textView = (TextView) findViewById(R.id.textView);
        if (getIntent().getStringExtra(INTENT_EXTRA_SCREEN) != null) {
            if (getIntent().getStringExtra(INTENT_EXTRA_SCREEN).equals(INTENT_SCREEN_DISABLED_APP)) {
                textView.setText("このアプリは使えません。");
            } else if (getIntent().getStringExtra(INTENT_EXTRA_SCREEN).equals(INTENT_SCREEN_OVERUSE)) {
                long currentUsageTime = getIntent().getLongExtra(INTENT_EXTRA_TIME, 0);
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
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, VerifyPasscodeActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick_close(View view){
        finish();
    }
}
