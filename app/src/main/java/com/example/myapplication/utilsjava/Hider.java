package com.example.myapplication.utilsjava;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import java.util.HashSet;


public final class Hider {

    private static final String TAG = "Hider";
    private static final HandlerThread hiderThread = new HandlerThread("HIDER_THREAD");
    private static final Handler threadHandler;

    public static boolean initialized = false;
    public static MutableLiveData<State> state;

    public enum State {
        HIDDEN,
        VISIBLE,
        PROCESSING
    }

    static {
        hiderThread.start();
        threadHandler = new Handler(hiderThread.getLooper());
    }

    public static void init() {
        assert PrefMgr.initialized;
        state = new MutableLiveData<>(PrefMgr.getIsHidden() ? State.HIDDEN : State.VISIBLE);
        state.observeForever(state -> {
            if (state != State.PROCESSING)
                PrefMgr.setIsHidden(state == State.HIDDEN);
        });
        initialized = true;
    }

    /**
     * NOTE: Calling this method on a background thread
     * does not guarantee that the latest value set will be received.
     */
    public static State getState() {
        return state.getValue();
    }

    public static void hide(Context context) {

        threadHandler.post(() -> {

            Log.i("MyAppAction " + TAG, "Process 'hide' start.");
            state.postValue(State.PROCESSING);

            try {
                PrefMgr.getAppHider(context).hide(PrefMgr.getHideApps());
            } catch (Exception e) {
                Log.w("MyAppAction " +TAG, "Process 'hide' interrupted.");
                return;
            }

            Log.i("MyAppAction " + TAG, "Process 'hide' finish.");
            state.postValue(State.HIDDEN);

            QuickHideService.stopService(context);

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setMessage("Hidden successful");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(context, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    });
            alertDialog.show();
        });
    }

    public static void unhide(Context context) {

        threadHandler.post(() -> {

            Log.i("MyAppAction " + TAG, "Process 'unhide' start.");
            state.postValue(State.PROCESSING);

            try {
                PrefMgr.getAppHider(context).unHide(PrefMgr.getHideApps());
            } catch (Exception e) {
                Log.w("MyAppAction " +TAG, "Process 'unhide' interrupted.");
                return;
            }

            Log.i("MyAppAction " + TAG, "Process 'unhide' finish.");
            state.postValue(State.VISIBLE);

            //WARNING
            PrefMgr.setHideApps(new HashSet<>());

            // Important: The startService() method of QuickHideService must be invoked on the main thread.
            // If it's called from a background thread, the service might not get the most recent value from Hider.getState() in time.
            // As a result, if the state changes into VISIBLE from HIDDEN just before, the service won't start.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                new Handler(Looper.getMainLooper()).post(
                        () -> QuickHideService.startService(context));
            }

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setMessage(context.getString(R.string.unhidden_toast));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(context, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    });
            alertDialog.show();
        });
    }

    public static void forceUnhide(Context context) {
        if (state.getValue() == State.PROCESSING)
            hiderThread.interrupt();
        PrefMgr.setIsHidden(true);
        unhide(context);
    }

}
