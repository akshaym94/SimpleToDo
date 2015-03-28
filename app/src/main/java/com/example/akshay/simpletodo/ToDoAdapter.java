package com.example.akshay.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Akshay on 21-03-2015.
 */

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private ArrayList<Item> mDataset;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public CheckBox mCheckBox;
        public TextView reminderView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.todo_text);
            mCheckBox = (CheckBox)v.findViewById(R.id.todo_check_box);
            reminderView = (TextView)v.findViewById(R.id.reminder_time);
        }
    }

    public ToDoAdapter(ArrayList<Item> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views
    @Override
    public ToDoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //setup the UI of card
        Item item = mDataset.get(position);
        holder.mTextView.setText(item.getText());
        holder.mCheckBox.setChecked(item.getStatus());
        if (item.reminderOn == 1) {
            holder.reminderView.setText(getDateTime(item.getReminderDate()));
        } else {
            holder.reminderView.setText("Reminder not set");
        }
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()) {
                    MainActivity.mDataSet.get(position).setStatus(isChecked);
                    MainActivity.DB.updateItem(MainActivity.mDataSet.get(position));
                }
            }
        });

        holder.reminderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.sendBroadcast(new Intent("show.date_time.dialog").putExtra("position", position));
            }
        });
    }

    // Return the size of dataset
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }
}