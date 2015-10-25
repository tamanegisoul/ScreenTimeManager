package tamanegisoul.screentime;

import android.util.Log;

public class Logger {

    public static void d(Object caller, String message) {
        Log.d(caller.getClass().getCanonicalName(), message);
    }
}
