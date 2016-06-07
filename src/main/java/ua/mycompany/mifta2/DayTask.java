package ua.mycompany.mifta2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import ua.mycompany.mifta2.calendarHelper.Task;

/**
 * Created by Maxim on 05.06.2016.
 */
public class DayTask extends Activity {

    final private String DATE = "date";
    String date;
    String[] eventTypeArray;
    String eventType;

    LinearLayout llTaskList;
    Button btnAddTask;
    EditText etTask;
    TextView tvDate;
    Spinner spinnerEventType;

    Realm realm;
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
        llTaskList = (LinearLayout) findViewById(R.id.llTaskList);

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnAddTask) {
                    addTask(date, eventType, etTask.getText().toString());
                    etTask.setText(null);
                }
            }
        });

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
            llTaskList.addView(tv);
        }
    }
}
