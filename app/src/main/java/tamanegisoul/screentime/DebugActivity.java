package tamanegisoul.screentime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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

    public void onClick_startScreenLockActivity(View view){
        startActivity(new Intent(this, ScreenLockActivity.class));
    }

    public void onClick_startSettingsActivity(View view){
        startActivity(new Intent(this, SettingsActivity.class));
    }
    public void onClick_startVerifyPasscodeActivity(View view){
        startActivity(new Intent(this, VerifyPasscodeActivity.class));
    }

}
