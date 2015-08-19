package tamanegisoul.screentime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Tomoya on 2015/08/17.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // サービスの起動
            Intent service = new Intent(context, MainService.class);
            context.startService(service);
        }
    }
}
