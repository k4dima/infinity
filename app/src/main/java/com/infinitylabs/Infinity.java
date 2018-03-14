package com.infinitylabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

import com.stringcare.library.SC;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class Infinity {
    private static final SparseArray<String> cache = new SparseArray<>();
    private static final Map<String, String> cacheS = new HashMap<>();
    public static final String OPTIONS = "options";
    public static boolean DEBUG;
    public static String APPLICATION_ID;
    private static WeakReference<Context> contextWeakReference;

    public Infinity(boolean debug, Context context) {
        DEBUG = debug;
        contextWeakReference = new WeakReference<>(context);
        APPLICATION_ID = context.getPackageName();
    }

    public static SharedPreferences preferences(String name) {
        return context().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static Context context() {
        return contextWeakReference.get();
    }

    public static String string(String value) {
        String s = cacheS.get(value);
        if (s != null) {
            return s;
        } else {
            String decryptString = SC.decryptString(value);
            cacheS.put(value, decryptString);
            return decryptString;
        }
    }

    public static String string(int resId) {
        String cached = cache.get(resId);
        if (cached != null) {
            return cached;
        } else {
            String value = context().getString(resId);
            if (value.length() % 32 == 0) {
                value = string(value);
            }
            cache.put(resId, value);
            return value;
        }
    }
}