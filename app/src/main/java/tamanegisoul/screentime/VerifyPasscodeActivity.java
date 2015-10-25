package tamanegisoul.screentime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class VerifyPasscodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_verify);
    }

    public void onClick_OK(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        if (PreferenceHelper.isPasscodeValid(this, editText.getText().toString())) {
            PreferenceHelper.setAuthTimestamp(this);
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, R.string.passcode_is_invalid, Toast.LENGTH_LONG).show();
        }
    }

}
