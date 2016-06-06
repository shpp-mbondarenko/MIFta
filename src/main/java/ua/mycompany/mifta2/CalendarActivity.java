package ua.mycompany.mifta2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ua.mycompany.mifta2.calendarHelper.ItemObject;
import ua.mycompany.mifta2.calendarHelper.RecyclerViewAdapter;

/**
 * Created by Maxim on 24.05.2016.
 */
public class CalendarActivity extends Activity {

    private GridLayoutManager gLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);
        Log.d("myLog", "onCreate CalendarActivity");

        List<ItemObject> rowListItem = getAllItemList();
        //how many rows (4)
        gLayout = new GridLayoutManager(CalendarActivity.this, 7);

        RecyclerView rView = (RecyclerView)findViewById(R.id.recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(gLayout);

        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(CalendarActivity.this, rowListItem);
        rView.setAdapter(rcAdapter);
    }



    private List<ItemObject> getAllItemList(){

        List<ItemObject> allItems = new ArrayList<ItemObject>();
        Date date = new Date();
        GregorianCalendar gCalendar = new GregorianCalendar(2016, 6, 1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        date = calendar.getTime();
        SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM");
//        fmtOut.format(date);

        for (int i = 0; i < 31; i++) {
            date = addDays(date,1);
//            allItems.add(new ItemObject(Integer.toString(date.getMonth()) + " " + Integer.toString(date.getYear()), R.drawable.three));
            allItems.add(new ItemObject(fmtOut.format(date), R.drawable.three));
        }
        Log.d("myLog", "------ "+fmtOut.format(date));

        return allItems;
    }

    private Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

}
