package ua.mycompany.mifta2.calendarHelper;

/**
 * Created by Maxim on 05.06.2016.
 */
public class ItemObject {
    private String date;
    private int imageOfDay;


    public ItemObject(String date, int imageOfDay) {
        this.date = date;
        this.imageOfDay = imageOfDay;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getImageOfDay() {
        return imageOfDay;
    }

    public void setImageOfDay(int imageOfDay) {
        this.imageOfDay = imageOfDay;
    }
}
