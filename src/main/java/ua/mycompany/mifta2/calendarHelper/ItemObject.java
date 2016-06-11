package ua.mycompany.mifta2.calendarHelper;

/**
 * Created by Maxim on 05.06.2016.
 */
public class ItemObject {
    private String date;
    private int imageOfDay;
    private int offDay;


    public ItemObject(String date, int imageOfDay, int offDay) {
        this.date = date;
        this.imageOfDay = imageOfDay;
        this.offDay = offDay;
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

    public int getOffDay() {
        return offDay;
    }

    public void setOffDay(int offDay) {
        this.offDay = offDay;
    }
}
