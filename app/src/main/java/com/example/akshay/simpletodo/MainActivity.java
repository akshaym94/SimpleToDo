package com.example.akshay.simpletodo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static ToDoDataSource DB;
    private FloatingActionButton addButton;
    static ArrayList<Item> mDataSet;
    private View addLayout;
    private EditText newItem;

    @Override
    protected void onResume() {
        DB.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        DB.close();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Make database connection
        DB = new ToDoDataSource(this);
        DB.open();

        new AsyncTask<Void, Void, ArrayList<Item>>() {

            @Override
            protected ArrayList<Item> doInBackground(Void... params) {
                ArrayList<Item> temp = DB.getAllToDos();
                return temp;
            }

            @Override
            protected void onPostExecute(ArrayList<Item> temp) {
                mDataSet.addAll(temp);
                mAdapter.notifyDataSetChanged();
            }
        }.execute();

        //Setup toolbar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setup RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //set adapter
        mDataSet = new ArrayList<Item>();
        mAdapter = new ToDoAdapter(mDataSet, this);
        mRecyclerView.setAdapter(mAdapter);

        //add item layout
        addLayout = findViewById(R.id.new_item);
        newItem = (EditText) addLayout.findViewById(R.id.new_item_edittext);

        //Setup floating add button
        addButton = (FloatingActionButton) findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addLayout.getVisibility() == View.GONE) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    newItem.requestFocus();
                    expand(addLayout);
                    addButton.setImageResource(R.drawable.ic_action_done);
                } else {
                    String text = newItem.getText().toString();
                    if(!text.trim().isEmpty()) {
                        Item item = new Item(text);
                        mDataSet.add(0, item);
                        mAdapter.notifyItemInserted(0);
                        mRecyclerView.smoothScrollToPosition(0);
                        DB.addToDo(item);
                        collapse(addLayout);
                        newItem.setText("");
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        addButton.setImageResource(R.drawable.ic_content_add);
                        hideKeyboard();
                    }
                }
            }
        });

        //setup broadcast receiver to show datetime dialog and the datetime listener

        BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //This piece of code will be executed when you click on your item
                // Call your fragment...
                final int position = intent.getExtras().getInt("position");
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener() {
                            @Override
                            public void onDateTimeSet(Date date) {
                                mDataSet.get(position).setReminderDate(date);
                                mDataSet.get(position).setReminderOn(1);
                                DB.updateItem(mDataSet.get(position));
                                Item item = mDataSet.get(position);
                                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                //set alarm
                                Intent i = new Intent(MainActivity.this, AlarmReceiver.class);
                                i.putExtra("id", item.getId());
                                i.putExtra("msg", item.getText());
                                PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                                try {
                                    am.cancel(pi);
                                    Log.d("Alarm", "Alarm cancelled for updation");
                                } catch(Exception e) {
                                    Log.d("Alarm", "Alarm was not cancelled");
                                }
                                am.set(AlarmManager.RTC, item.getReminderDate().getTime(), pi);
                                mAdapter.notifyItemChanged(position);
                            }
                        })
                        .setInitialDate(new Date())
                        .build()
                        .show();
            }
        };
        registerReceiver(mBroadcastReceiver, new IntentFilter("show.date_time.dialog"));
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.onActionViewCollapsed();
                mRecyclerView.swapAdapter(mAdapter, false);
                return true;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.search) {
            onSearchRequested();
            return true;
        } else if(id == android.R.id.home) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            newItem.setText("");
            collapse(addLayout);
            addButton.setImageResource(R.drawable.ic_content_add);
            hideKeyboard();
        } else if(id == R.id.clearDB) {
            DB.clearDB();
            Toast.makeText(this, "Database cleared.", Toast.LENGTH_SHORT).show();
            mDataSet.clear();
            mAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // Do work using string
            DB.open();
            mRecyclerView.swapAdapter(new ToDoAdapter(DB.search(query), this), false);
        }
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
