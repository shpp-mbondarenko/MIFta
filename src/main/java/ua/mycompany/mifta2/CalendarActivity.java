package ua.mycompany.mifta2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import ua.mycompany.mifta2.calendarHelper.ItemObject;
import ua.mycompany.mifta2.calendarHelper.RecyclerViewAdapter;
import ua.mycompany.mifta2.calendarHelper.Task;

/**
 * Created by Maxim on 24.05.2016.
 */
public class CalendarActivity extends Activity {

    private static final int BASE_YEAR = 2014;
    final String DATE = "date";
    private GridLayoutManager gLayout;
    RecyclerView rView;
    Button btnNextYear;
    Button btnPrevYear;
    Button btnNextMonth;
    Button btnPrevMonth;
    TextView tvYear;
    TextView  tvMonth;
    ProgressDialog progressDialog;

    String date;
    String[] listOfMonth;
    int[] listOfYears;

    int monthIndicator = 5;
    int yearIndicator = 2;

    int todoDay = R.drawable.index;
    int freeDay = 1;

    Realm realm;

    List<ItemObject> rowListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);
        Log.d("myLog", "onCreate CalendarActivity");
        rowListItem = new ArrayList<ItemObject>();
        MakeList ml = new MakeList();

        findViewObjects();
        ml.execute();
        //how many rows (7)
        gLayout = new GridLayoutManager(CalendarActivity.this, 7);

        rView = (RecyclerView)findViewById(R.id.recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(gLayout);

    }
    //Finding all Views on screen
    private void findViewObjects() {
        listOfMonth = getResources().getStringArray(R.array.Month);
        listOfYears = getResources().getIntArray(R.array.Year);

        btnNextMonth = (Button) findViewById(R.id.btnNextMonth);
        btnNextYear = (Button) findViewById(R.id.btnNextYear);
        btnPrevYear = (Button) findViewById(R.id.btnPrevYear);
        btnPrevMonth = (Button) findViewById(R.id.btnPrevMonth);
        tvYear = (TextView) findViewById(R.id.tvYear);
        tvMonth = (TextView) findViewById(R.id.tvMonth);

        tvMonth.setText(listOfMonth[monthIndicator]);
        tvYear.setText(Integer.toString(listOfYears[yearIndicator]));

        btnNextMonth.setOnClickListener(monthListener);
        btnPrevMonth.setOnClickListener(monthListener);
        btnNextYear.setOnClickListener(monthListener);
        btnPrevYear.setOnClickListener(monthListener);

    }

    View.OnClickListener monthListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnNextMonth:
                    if (monthIndicator == listOfMonth.length - 1) {
                        monthIndicator = 0;
                        tvMonth.setText(listOfMonth[monthIndicator]);
                    } else if (monthIndicator < listOfMonth.length - 1) {
                        monthIndicator++;
                        tvMonth.setText(listOfMonth[monthIndicator]);
                    }
                    break;
                case R.id.btnPrevMonth:
                    if (monthIndicator == 0) {
                        monthIndicator = listOfMonth.length - 1;
                        tvMonth.setText(listOfMonth[monthIndicator]);
                    } else if (monthIndicator > 0) {
                        --monthIndicator;
                        tvMonth.setText(listOfMonth[monthIndicator]);
                    }
                    break;
                case R.id.btnNextYear:
                    if (yearIndicator == listOfYears.length - 1) {
                        yearIndicator = 0;
                        tvYear.setText(Integer.toString(listOfYears[yearIndicator]));
                    } else if (yearIndicator < listOfYears.length - 1) {
                        ++yearIndicator;
                        tvYear.setText(Integer.toString(listOfYears[yearIndicator]));
                    }
                    break;
                case R.id.btnPrevYear:
                    if (yearIndicator == 0) {
                        yearIndicator = listOfYears.length - 1;
                        tvYear.setText(Integer.toString(listOfYears[yearIndicator]));
                    } else if (yearIndicator > 0) {
                        --yearIndicator;
                        tvYear.setText(Integer.toString(listOfYears[yearIndicator]));
                    }
                    break;
            }
            refreshCalendar();
        }
    };

    private void refreshCalendar() {
        rowListItem.clear();
        MakeList ml = new MakeList();
        ml.execute();
        rView.removeAllViews();
        rView.setHasFixedSize(true);
        rView.setLayoutManager(gLayout);
    }

    private Date addDays(Date date, int days) {
        Log.d("myLog", "------ IN ADDdays ");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }



    class MakeList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CalendarActivity.this,  R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(("Please wait..."));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            int baseYear = BASE_YEAR;
            int baseMonth = 0;
            baseYear += yearIndicator;
            baseMonth += monthIndicator;
            Date curMonth;
            Date newMonth;
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
            realm = Realm.getInstance(realmConfig);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, baseYear);
            calendar.set(Calendar.MONTH, baseMonth);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            newMonth = calendar.getTime();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            curMonth = calendar.getTime();
            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yy");

            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            Log.d("myLog", "------ START MONTH " + fmt.format(curMonth));
            SimpleDateFormat sdf = new SimpleDateFormat("EE");
            Log.d("myLog", "------ FIRST WEEK " + sdf.format(curMonth));

            //Find beginning of week
            Date tmpDate = curMonth;
            Calendar tmpCal = Calendar.getInstance();
            tmpCal.setTime(tmpDate);

            while (tmpCal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
                tmpDate = addDays(tmpDate, -1);
                tmpCal.setTime(tmpDate);
                rowListItem.add(addItemObject(fmtOut.format(tmpDate), 1));
            }
            rowListItem.add(addItemObject(fmtOut.format(curMonth), 0));
            while (curMonth.before(newMonth)) {
                curMonth = addDays(curMonth, 1);
                rowListItem.add(addItemObject(fmtOut.format(curMonth), 0));
            }

            //To finish last week
            tmpCal.setTime(curMonth);
            while (tmpCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                curMonth = addDays(curMonth, 1);
                tmpCal.setTime(curMonth);
                rowListItem.add(addItemObject(fmtOut.format(curMonth), 1));
            }


            return null;
        }

        // Create new object of ItemObject.
        // d - date
        // flag - 0 is day in current month. 1 is day that not in current month (paint in GRAY color)
        private ItemObject addItemObject(String d, int flag) {
            RealmResults<Task> res = realm.where(Task.class).equalTo(DATE, d).findAll();
            if (res.isEmpty()) {
                return new ItemObject(d, freeDay, flag);
            } else {
                return new ItemObject(d, todoDay, flag);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(CalendarActivity.this, rowListItem);
            rView.setAdapter(rcAdapter);
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 0);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        rowListItem.clear();
        MakeList ml = new MakeList();
        ml.execute();
    }

}
