package com.infinitylabs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.infinitylabs.server.Run;
import com.smodule.Time;

import org.json.JSONException;

import java.io.IOException;

import static com.infinitylabs.Infinity.APPLICATION_ID;
import static com.infinitylabs.Infinity.string;

public class Apprate {
    public static final String URL_PLAY_STORE = "https://play.google.com/store/apps/details?id=" +
            APPLICATION_ID;
    public static final Intent INTENT_PLAY_STORE = new Intent(Intent.ACTION_VIEW,
            Uri.parse(URL_PLAY_STORE));
    private static final String APPRATER = "apprater";
    private static final String FIRST_LAUNCH = "first_launch";
    private static final String LAUNCH_COUNT = "launch_count";
    private static final int DAYS_UNTIL_PROMPT = 1;
    private static final String DONT_SHOW = "dont_show";
    private static final int LAUNCHES_UNTIL_PROMPT = 2;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private final Activity activity;

    public Apprate(Activity activity) {
        this.activity = activity;
        rate();
    }

    private void rate() {
        if (prefs == null) {
            new Run() {
                @SuppressLint("CommitPrefEdits")
                @Override
                protected void run() throws AppException, IOException, JSONException {
                    prefs = Infinity.preferences(APPRATER);
                    editor = prefs.edit();
                }

                @Override
                protected void completed() {
                    rate();
                }
            }.execute();
        } else {
            if (!prefs.getBoolean(DONT_SHOW, false)) {
                int launchCount = prefs.getInt(LAUNCH_COUNT, 0) + 1;
                editor.putInt(LAUNCH_COUNT, launchCount);
                Long firstLaunch = prefs.getLong(FIRST_LAUNCH, 0);
                if (firstLaunch == 0) {
                    firstLaunch = System.currentTimeMillis();
                    editor.putLong(FIRST_LAUNCH, firstLaunch);
                }
                editor.apply();
                if (launchCount >= LAUNCHES_UNTIL_PROMPT &&
                        System.currentTimeMillis() >= firstLaunch + (DAYS_UNTIL_PROMPT * Time.DAY)) {
                    // TODO non Google Play Stores
                    boolean review = Math.random() < 0.5;
                    new AlertDialog.Builder(activity)
                            .setMessage(string(review ? R.string.review : R.string.enjoying))
                            .setPositiveButton(string(R.string.yes), (dialog, id) -> {
                                if (review)
                                    success();
                                else
                                    new AlertDialog.Builder(activity)
                                            .setMessage(string(R.string.rate))
                                            .setPositiveButton(android.R.string.ok, (dialog1, which) -> success())
                                            .setOnCancelListener(dialog12 -> later())
                                            .show();
                            })
                            .setNegativeButton(string(R.string.no), (dialog, id) -> dontShow())
                            .setOnCancelListener(dialog -> later())
                            .show();
                }
            }
        }
    }

    private void success() {
        activity.startActivity(INTENT_PLAY_STORE);
        dontShow();
    }

    private void later() {
        editor.putLong(FIRST_LAUNCH, System.currentTimeMillis())
                .putInt(LAUNCH_COUNT, 0)
                .apply();
    }

    private void dontShow() {
        editor.putBoolean(DONT_SHOW, true)
                .apply();
    }
}