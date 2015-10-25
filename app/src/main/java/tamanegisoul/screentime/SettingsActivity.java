package tamanegisoul.screentime;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.util.Date;
import java.util.List;


public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onRestart() {
        Logger.d(this, "onRestart()");
        super.onRestart();
        // 再表示の際に認証から時間が立っていれば終了する。
        if (new Date().getTime() - PreferenceHelper.getAuthSessionTimeout(this) * 60 * 1000 > PreferenceHelper.getAuthTimestamp(this)) {
            finish();
        }
    }

    // 以下は自動生成されたものをベースに作ったのでよくわからん。

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return fragmentName.equals(GeneralPreferenceFragment.class.getName());
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }

}
