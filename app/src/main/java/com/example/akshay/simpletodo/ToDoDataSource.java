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
            item.setReminderDate(DBHelper.getDateFromString(cursor.getString(3)));
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
        values.put(DBHelper.REMINDER, DBHelper.getDateTime(item.getReminderDate()));
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
            item.setReminderDate(DBHelper.getDateFromString(cursor.getString(3)));
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
        values.put(DBHelper.REMINDER, DBHelper.getDateTime(item.getReminderDate()));
        values.put(DBHelper.REMINDERON, item.getReminderOn());
        database.update(DBHelper.TABLE_NAME, values, DBHelper.ID + "=" + item.getId(), null);
    }

    public void removeCompletedAlarms() {
        database.rawQuery("UPDATE " + DBHelper.TABLE_NAME + " SET " + DBHelper.REMINDERON + "=0 WHERE " + DBHelper.REMINDER + " <= Datetime('" + DBHelper.getDateTime(new Date())+ "');", null).close();

    }
}
