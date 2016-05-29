package ua.mycompany.mifta2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Maxim on 24.05.2016.
 */
public class CalendarActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);
        Log.d("myLog", "onCreate calendar");
    }
}
