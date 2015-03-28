package com.example.akshay.simpletodo;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Akshay on 28-03-2015.
 */
public class AlarmService extends IntentService {

    public static final String CREATE = "CREATE";
    public static final String CREATEALL = "CREATEALL";
    public static final String CANCEL = "CANCEL";
    public static final String TAG = "SimpleToDoAlarmService";

    private IntentFilter matcher;

    public AlarmService() {
        super(TAG);
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CREATEALL);
        matcher.addAction(CANCEL);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        String notificationId = intent.getStringExtra("notificationId");

        if (matcher.matchAction(action)) {
            execute(action, notificationId);
        }
    }

    private void execute(String action, String notificationId) {
        Log.d("Alarm", "Started all alarms");
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        ArrayList<Item> alarms = new ToDoDataSource(getApplicationContext()).getAlarms();
        for(Item alarm : alarms) {
            Log.d("AlarmService", "Setting alarm for item id:" + alarm.getId());
            Intent i = new Intent(this, AlarmReceiver.class);
            i.putExtra("id", alarm.getId());
            i.putExtra("msg", alarm.getText());
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            am.set(AlarmManager.RTC, alarm.getReminderDate().getTime(), pi);
        }
    }
}

