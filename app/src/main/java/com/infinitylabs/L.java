package com.infinitylabs;

import android.util.Log;

import static com.infinitylabs.Infinity.DEBUG;

public class L {
    public static void v(String tag, String msg) {
        if (DEBUG)
            android.util.Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG)
            android.util.Log.d(tag, msg);
    }

    public static void og(Object message) {
        if (DEBUG)
            if (message == null)
                og("null");
            else if (message instanceof Exception)
                ((Exception) message).printStackTrace();
            else
                og(message.toString());
    }

    private static void og(String msg) {
        Log.d("infnt", msg);
    }
}