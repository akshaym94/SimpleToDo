package com.example.akshay.simpletodo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Akshay on 28-03-2015.
 */
public class AlarmSetter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AlarmService.class);
        service.setAction(AlarmService.CREATE);
        context.startService(service);
    }
}
