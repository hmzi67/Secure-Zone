package io.github.hmzi67.securezone.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GesturesBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Intent serviceIntent = new Intent(context, GesturesService.class);
            context.startForegroundService(serviceIntent);

        }
    }
}
