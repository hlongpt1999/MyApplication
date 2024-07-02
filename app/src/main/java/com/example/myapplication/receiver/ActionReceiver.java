package com.example.myapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.utilsjava.Hider;

import java.util.Objects;

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("MyAppAction " + "ActionReceiver", "New action received.");

        if (Hider.state.getValue() == Hider.State.PROCESSING) {
            Log.w("MyAppAction " +"ActionReceiver", "Already processing. Ignore the new action.");
            return;
        }

        if (Objects.equals(intent.getAction(), "deltazero.amarok.HIDE")) {
            Hider.hide(context);
        } else if (Objects.equals(intent.getAction(), "deltazero.amarok.UNHIDE")) {
            //todo:
//            if (SecurityUtil.isUnlockRequired())
//                context.startActivity(new Intent(context, SecurityAuthForQSActivity.class)
//                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//            else
                Hider.unhide(context);
        } else {
            Log.w("MyAppAction " +"ActionReceiver", "Invalid action: " + intent.getAction());
            Toast.makeText(context, context.getString(R.string.invalid_action, intent.getAction()),
                    Toast.LENGTH_LONG).show();
        }
    }
}
