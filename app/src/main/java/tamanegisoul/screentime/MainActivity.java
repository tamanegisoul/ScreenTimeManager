package tamanegisoul.screentime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter f = new IntentFilter();
        f.addAction(ValidateUsageTimeTimer.ACTION_UPDATE_USAGE_TIME);
        startService(new Intent(this, MainService.class));
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String time = String.valueOf(intent.getLongExtra(ValidateUsageTimeTimer.TIME, 0));
                Log.d(this.getClass().getName(), time);
                TextView t = (TextView) findViewById(R.id.textView_time);
                t.setText(time);
            }
        }, f);
        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);
        checkBox.setChecked(PreferenceHelper.isEnabledRestriction(this));
        EditText editText = (EditText)findViewById(R.id.editText_time);
        // setText(int)だとエラーになるので
        editText.setText(String.valueOf(PreferenceHelper.getRestrictedTime(this)));
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
