package com.example.akshay.simpletodo;

import java.util.Date;

/**
 * Created by Akshay on 21-03-2015.
 */
public class Item {
    int id;
    String text;
    Date reminderDate = new Date();
    Boolean status = false;
    int reminderOn = 0;

    public Item() {}
    public Item(String text) { this.text = text;}
    public Item(String text, Boolean status) { this.text = text; this.status = status; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(Date reminder_date) {
        this.reminderDate = reminder_date;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String toString() {
        return id + ":" + text + ":" + reminderDate + ":" + status;
    }

    public int getReminderOn() {
        return reminderOn;
    }

    public void setReminderOn(int reminderOn) {
        this.reminderOn = reminderOn;
    }
}
