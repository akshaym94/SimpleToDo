package com.example.akshay.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Akshay on 21-03-2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    // Database Information
    static final String DB_NAME = "TODO.DB";
    static final int DB_VERSION = 1;
    static final String TABLE_NAME = "todo";

    // columns
    public static final String ID = "id";
    public static final String TEXT = "text";
    public static final String STATUS = "status";
    public static final String REMINDER = "remind_on";
    public static final String REMINDERON = "reminder_on";

    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + ID
        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TEXT + " TEXT NOT NULL, " + STATUS + " INTEGER NOT NULL, " +
        REMINDER + " DATETIME, " + REMINDERON + " INTEGER);";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public static Date getDateFromString(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date d = null;
        try {
            d = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }
}
