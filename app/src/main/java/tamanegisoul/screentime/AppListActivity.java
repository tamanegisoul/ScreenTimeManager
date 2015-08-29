package tamanegisoul.screentime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ListView;

import java.util.Date;

/**
 * 制限するアプリケーションリスト。
 */
public class AppListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        ListView listView = (ListView) findViewById(R.id.listView_app_list);
        listView.setAdapter(new AppListAdapter(this, R.layout.list_item, ApplicationUtils.getInstalledApplications(this)));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 再表示の際に認証から時間が立っていれば終了する。
        if(new Date().getTime() - PreferenceHelper.getAuthSessionTimeout(this) * 60 * 1000 > PreferenceHelper.getAuthTimestamp(this)) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 通常操作で戻る（戻り先は設定画面）場合は認証セッションを再開する。
        if(keyCode==KeyEvent.KEYCODE_BACK){
            PreferenceHelper.setAuthTimestamp(this);
        }
        return super.onKeyDown(keyCode, event);
    }

}
