package ua.mycompany.mifta2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import ua.mycompany.mifta2.calendarHelper.AlarmReceiver;
import ua.mycompany.mifta2.calendarHelper.Task;

/**
 * Created by Maxim on 05.06.2016.
 */
public class DayTask extends Activity {
    final String SAVED_NUM = "saved_num";
    final String LOG_TAG = "myLog";
    final String DATE = "date";
    final String EVENT_TYPE = "event_type";
    final String TASK = "task";
    String date;
    String task;
    String[] eventTypeArray;

    String eventType;
    LinearLayout llTaskList;
    Button btnAddTask;
    Button btnSetTime;
    EditText etTask;
    TextView tvDate;
    CheckBox cbNotification;

    Spinner spinnerEventType;
    Realm realm;
    //Time
    final int DIALOG_ID = 0;
    int alarmMinute;

    int alarmHour;
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_task);
        Intent intent = getIntent();
        date = intent.getStringExtra(DATE);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfig);

        findViewObjects();

        showTasks(findTasks(date));

    }

    private void findViewObjects() {
        tvDate = (TextView) findViewById(R.id.tvDate);
        spinnerEventType = (Spinner) findViewById(R.id.spinnerEventType);
        etTask = (EditText) findViewById(R.id.etTask);
        btnAddTask = (Button) findViewById(R.id.btnAddTask);
        btnSetTime = (Button) findViewById(R.id.btnSetTime);
        cbNotification = (CheckBox) findViewById(R.id.cbNotification);
        llTaskList = (LinearLayout) findViewById(R.id.llTaskList);

        btnAddTask.setOnClickListener(onClickListener);
        btnSetTime.setOnClickListener(onClickListener);

        tvDate.setText(date);
        //Setup adapter
        eventTypeArray = getResources().getStringArray(R.array.CalendarEventType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DayTask.this,
                android.R.layout.simple_spinner_item, eventTypeArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(adapter);
        spinnerEventType.setSelection(0);
        spinnerEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eventType = eventTypeArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnAddTask:
                    task = etTask.getText().toString();
                    addTask(date, eventType, task);
                    etTask.setText(null);
                    if (cbNotification.isChecked())
                        sendAlarm();


                    break;
                case R.id.btnSetTime:
                    showDialog(DIALOG_ID);
                    break;
            }

        }
    };

    private void sendAlarm() {
        int counter;
        sPref = getPreferences(MODE_PRIVATE);
        counter = sPref.getInt(SAVED_NUM, 0);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy");
        Date alarmDate = null;
        try {
            alarmDate = format.parse(date);
            Log.d(LOG_TAG, alarmDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(alarmDate);

        Log.d(LOG_TAG, "before " + cal.toString());

        cal.set(Calendar.HOUR_OF_DAY, alarmHour);
        cal.set(Calendar.MINUTE, alarmMinute);

        Log.d(LOG_TAG, "after " + cal.toString());

        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction(String.valueOf(counter));
        counter++;
        intent.putExtra(DATE, date);
        intent.putExtra(EVENT_TYPE, eventType);
        intent.putExtra(TASK, task);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, 0);
        // Setup alarm
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pIntent);

        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(SAVED_NUM, counter);
        ed.apply();

        Log.d(LOG_TAG, "Intent Action - " + intent.getAction());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID){
            Calendar cal = Calendar.getInstance();
            return new TimePickerDialog(DayTask.this,onTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        } else {
            return null;
        }
    }

    protected TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            alarmHour = hourOfDay;
            alarmMinute = minute;
            Toast.makeText(getApplicationContext(), alarmHour + ":" + alarmMinute, Toast.LENGTH_LONG).show();
        }
    };

    private void addTask(String date, String eventType, String eventDescription) {
        if (!eventDescription.matches("")) {
            realm.beginTransaction();
            Task task = realm.createObject(Task.class);
            task.setDate(date);
            task.setEventType(eventType);
            task.setEventDescription(eventDescription);
            realm.commitTransaction();
            Toast.makeText(getApplicationContext(), "Task is added!",Toast.LENGTH_LONG).show();
            showTasks(findTasks(date));
        } else {
            Toast.makeText(getApplicationContext(), "Task is NOT added!",Toast.LENGTH_LONG).show();
        }
    }

    private RealmResults<Task> findTasks(String date) {
        RealmResults<Task> res =
                realm.where(Task.class).equalTo(DATE, date).findAll();
        return res;
    }


    private void showTasks(RealmResults<Task> tasks) {
        llTaskList.removeAllViews();
        for (Task t : tasks) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(t.getEventType() + ": " + t.getEventDescription() );
            tv.setTextColor(getResources().getColor(R.color.colorTextPrimary));
            llTaskList.addView(tv);
        }
    }
}
