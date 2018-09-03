package com.infinitylabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.smodule.Time;

import org.jetbrains.annotations.NotNull;

import static com.infinitylabs.Infinity.OPTIONS;
import static com.infinitylabs.Infinity.string;

public class NewVersion {
    private static final String LAST_CHECK = "last_check";
    private static final SharedPreferences settings = Infinity.preferences(OPTIONS);

    public NewVersion(Activity activity, String url, boolean forced,
                      DialogInterface.OnClickListener listenerNegative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(string(R.string.new_version_available));
        builder.setPositiveButton(string(R.string.update), (dialog, id) -> openSite(activity, url));
        if (forced)
            settings.edit().putLong(LAST_CHECK, 0).apply();
        else
            builder.setNegativeButton(string(R.string.later), listenerNegative);
        builder.show();
    }

    public static boolean check() {
        boolean check = System.currentTimeMillis() - settings.getLong(LAST_CHECK, 0) > Time.DAY;
        if (check)
            settings.edit().putLong(LAST_CHECK, System.currentTimeMillis()).apply();
        return check;
    }

    public static void openSite(@NotNull Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}