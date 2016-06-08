package ua.mycompany.mifta2.calendarHelper;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ua.mycompany.mifta2.R;

/**
 * Created by Maxim on 08.06.2016.
 */
public class NotificationService extends IntentService {

    final private String DATE = "date";
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
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.three)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!" + intent.getStringExtra(DATE))
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(55, mBuilder.build());
    }
}
