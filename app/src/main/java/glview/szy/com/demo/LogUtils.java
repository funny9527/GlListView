package glview.szy.com.demo;

import android.util.Log;

/**
 * Created by szy on 2016/4/27.
 */
public class LogUtils {
    public static final String TAG = "test";
    public static void log(String msg) {
        Log.v(TAG, msg);
    }

    public static void log(String tag, String msg) {
        Log.v(tag, msg);
    }
}
