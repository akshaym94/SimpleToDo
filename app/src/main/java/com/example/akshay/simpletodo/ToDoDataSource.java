package com.example.akshay.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Akshay on 21-03-2015.
 */
public class ToDoDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = { DBHelper.ID, DBHelper.TEXT, DBHelper.STATUS, DBHelper.REMINDER};

    public ToDoDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ArrayList<Item> getAllToDos() {
        ArrayList<Item> items = new ArrayList<Item>();
        Cursor cursor = database.query(DBHelper.TABLE_NAME, null, null, null, null, null, DBHelper.ID + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = new Item();
            item.setId(cursor.getInt(0));
            item.setText(cursor.getString(1));
            item.setStatus(cursor.getInt(2) == 1);
            item.setReminderDate(getDateFromString(cursor.getString(3)));
            item.setReminderOn(cursor.getInt(4));
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    public long addToDo(Item item) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.TEXT, item.getText());
        values.put(DBHelper.STATUS, item.getStatus());
        values.put(DBHelper.REMINDER, getDateTime(item.getReminderDate()));
        values.put(DBHelper.REMINDERON, item.getReminderOn());

        long insertId = database.insert(DBHelper.TABLE_NAME, null, values);
        return insertId;
    }

    public void clearDB() {
        database.execSQL("DELETE FROM " + DBHelper.TABLE_NAME);
    }

    public ArrayList<Item> search(String query) {
        ArrayList<Item> items = new ArrayList<Item>();
        Cursor cursor = database.query(DBHelper.TABLE_NAME, null, DBHelper.TEXT + " LIKE '%" + query + "%'", null, null, null, DBHelper.ID + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = new Item();
            item.setId(cursor.getInt(0));
            item.setText(cursor.getString(1));
            item.setStatus(cursor.getInt(2) == 1);
            item.setReminderDate(getDateFromString(cursor.getString(3)));
            item.setReminderOn(cursor.getInt(4));
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    public void updateItem(Item item) {
        Log.d("ToDoDataSource", "updating item:" + item);
        ContentValues values = new ContentValues();
        values.put(DBHelper.TEXT, item.getText());
        values.put(DBHelper.STATUS, item.getStatus());
        values.put(DBHelper.REMINDER, getDateTime(item.getReminderDate()));
        values.put(DBHelper.REMINDERON, item.getReminderOn());
        database.update(DBHelper.TABLE_NAME, values, DBHelper.ID + "=" + item.getId(), null);
    }

    public ArrayList<Item> getAlarms() {
        Log.d("Alarm", "Entered get all alarms");
        ArrayList<Item> items = new ArrayList<Item>();
        Cursor cursor = database.query(DBHelper.TABLE_NAME, null, DBHelper.REMINDERON + " = 1", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = new Item();
            item.setId(cursor.getInt(0));
            item.setText(cursor.getString(1));
            item.setStatus(cursor.getInt(2) == 1);
            item.setReminderDate(getDateFromString(cursor.getString(3)));
            item.setReminderOn(cursor.getInt(4));
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        Log.d("Alarm", "Exit get all alarms");
        return items;
    }

    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    private Date getDateFromString(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date d = null;
        try {
            d = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
