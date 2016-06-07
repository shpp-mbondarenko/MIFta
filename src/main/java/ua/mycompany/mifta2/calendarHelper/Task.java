package ua.mycompany.mifta2.calendarHelper;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by Maxim on 07.06.2016.
 */
public class Task extends RealmObject {

    @Required
    private String date;

    private String eventType;
    private String eventDescription;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
}
