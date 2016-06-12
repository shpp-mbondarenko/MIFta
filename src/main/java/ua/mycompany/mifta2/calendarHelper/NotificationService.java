package ua.mycompany.mifta2.calendarHelper;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ua.mycompany.mifta2.DayTask;
import ua.mycompany.mifta2.R;

/**
 * Created by Maxim on 08.06.2016.
 */
public class NotificationService extends IntentService {

    final String LOG_TAG = "myLog";
    final String DATE = "date";
    final String EVENT_TYPE = "event_type";
    final String TASK = "task";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationService(String name) {
        super(name);
    }

    public NotificationService() {
        super("NotificationService");

    }
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("myLog", "----" + intent.getStringExtra(DATE));
        Log.d("myLog", "InHandleIntent");

        Intent goToTaskActivity = new Intent(this, DayTask.class);
        goToTaskActivity.putExtra(DATE, intent.getStringExtra(DATE));
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, goToTaskActivity, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.index)
                        .setContentTitle(intent.getStringExtra(EVENT_TYPE))
                        .setContentText(intent.getStringExtra(DATE) + " - " + intent.getStringExtra(TASK))
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(pIntent);

        // Hide the notification after its selected
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }
}
