package com.infinitylabs.server;

import android.os.AsyncTask;

import com.infinitylabs.L;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class Call<T> {
    private static final Executor PARALLEL = new ThreadPoolExecutor(4, 8, 8, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());
    protected Executor executor = PARALLEL;
    protected boolean log = true;

    protected T call() throws AppException, IOException, JSONException {
        run();
        return null;
    }

    protected abstract void run() throws AppException, IOException, JSONException;

    protected CallExecutor<T> executor() {
        return new CallExecutor<>(this);
    }

    /**
     * Executes on own, parallel thread pool
     */
    public void execute() {
        executor().executeOnExecutor(executor);
    }

    protected void postExecute(T result, Exception exception) {
        if (exception == null) {
            completed(result);
        }
    }

    protected void completed(T result) {
        completed();
    }

    protected void completed() {
    }

    protected void deleteFail(File file) {
        L.og("unable_delete " + file.getName());
    }

    public static class CallExecutor<T> extends AsyncTask<Void, T, T> {
        protected final Call<T> call;
        protected Exception exception;
        String name;
        private long start;

        protected CallExecutor(Call<T> call) {
            this.call = call;
        }

        @Override
        protected T doInBackground(Void... params) {
            start = System.currentTimeMillis();
            try {
                Class aClass = call.getClass();
                name = aClass.getSimpleName();
                if (name.isEmpty()) {
                    String[] split = aClass.getName().split("\\.");
                    name = split[split.length - 1];
                }
                if (call.log)
                    L.og("task: " + name);
                return call.call();
            } catch (AppException | IOException | JSONException e) {
                return logsavereturn(e);
            }
        }

        protected T logsavereturn(Exception e) {
            this.exception = e;
            L.og(e);
            return null;
        }

        @Override
        protected void onPostExecute(T result) {
            if (call.log)
                L.og("time: " + name + " : " + (System.currentTimeMillis() - start));
            call.postExecute(result, exception);
        }
    }

    public static class AppException extends Exception {
        public AppException() {
        }

        public AppException(String message) {
            super(message);
        }

        public AppException(JSONException e) {
            super(e);
        }
    }
}