package tamanegisoul.screentime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import java.util.Date;

/**
 *
 */
public class AppListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
    }

    /**
     * 再表示の際に認証から時間が立っていれば終了する。
     */
    @Override
    protected void onRestart() {
        Logger.d(this, "onRestart()");
        super.onRestart();
        if(new Date().getTime() - PreferenceHelper.getAuthSessionTimeout(this) * 60 * 1000 > PreferenceHelper.getAuthTimestamp(this)) {
            finish();
        }
    }

    /**
     * 通常操作で戻る（戻り先は設定画面）場合は認証セッションを再開する。
     * @param keyCode key code
     * @param event event
     * @return true
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            PreferenceHelper.setAuthTimestamp(this);
        }
        return super.onKeyDown(keyCode, event);
    }

}
