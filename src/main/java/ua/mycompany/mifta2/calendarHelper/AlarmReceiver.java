package ua.mycompany.mifta2.calendarHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Maxim on 08.06.2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    final private String DATE = "date";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, NotificationService.class);
        i.putExtra(DATE,intent.getStringExtra(DATE));
        context.startService(i);
    }
}
