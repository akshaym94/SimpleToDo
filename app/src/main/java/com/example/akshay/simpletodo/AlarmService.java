package com.example.akshay.simpletodo;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

        if (matcher.matchAction(action)) {
            execute();
        }
    }

    private void execute() {
        Log.d("Alarm", "Started alarm service");
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        ArrayList<Item> alarms = new ArrayList<Item>();
        DBHelper db = new DBHelper(this);
        db.getWritableDatabase().rawQuery("UPDATE " + DBHelper.TABLE_NAME + " SET " + DBHelper.REMINDERON + "=0 WHERE " + DBHelper.REMINDER + " <= Datetime(" + DBHelper.getDateTime(new Date())+ ");", null).close();
        Log.d("Alarm", "Disabling old alarms.");
        Cursor cursor = db.getReadableDatabase().rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME + " WHERE " + DBHelper.REMINDERON + "=1", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = new Item();
            item.setId(cursor.getInt(0));
            item.setText(cursor.getString(1));
            item.setStatus(cursor.getInt(2) == 1);
            item.setReminderDate(DBHelper.getDateFromString(cursor.getString(3)));
            item.setReminderOn(cursor.getInt(4));
            alarms.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
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

