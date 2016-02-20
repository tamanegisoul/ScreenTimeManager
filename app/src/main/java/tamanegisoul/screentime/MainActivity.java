package tamanegisoul.screentime;

import android.app.AlertDialog;
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
        } else if (!PreferenceHelper.isPasscodeSet(this)) {
            // パスコードが設定されていない場合はパスコードの設定を促す
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
        } else if (item.getItemId() == R.id.action_debug) {
            startActivity(new Intent(this, DebugActivity.class));
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
        alertDialogBuilder.setMessage(R.string.set_passcode);
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
        // 統計情報を取得
        ApplicationUsageStats stats = new ApplicationUsageStats();
        stats.refreshUsageStatsMap(this);

        // 各アプリの利用時間
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(stats.getDisplayString(this));

        // 合計利用時間
        textView = (TextView) findViewById(R.id.textView_current_usage_time);
        textView.setText(getString(R.string.current_usage_time, stats.getTotalUsageTime() / 1000 / 60));

        // 制限時間
        if (PreferenceHelper.isEnabledRestriction(this)) {
            textView = (TextView) findViewById(R.id.textView_restricted_time);
            textView.setText(getString(R.string.restricted_time, PreferenceHelper.getRestrictedTime(this)));
        } else {
            textView = (TextView) findViewById(R.id.textView_restricted_time);
            textView.setText(getString(R.string.not_restricted));
        }

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

}
