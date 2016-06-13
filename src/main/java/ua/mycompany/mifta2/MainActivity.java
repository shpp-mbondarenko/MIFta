package ua.mycompany.mifta2;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        // получаем TabHost
        TabHost tabHost = getTabHost();


        // инициализация была выполнена в getTabHost
        // метод setup вызывать не нужно

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("forecastTab");
        tabSpec.setIndicator(getString(R.string.forecast));
        tabSpec.setContent(new Intent(this, ForecastActivity.class));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("calendarTab");
        tabSpec.setIndicator(getString(R.string.calendar));
        tabSpec.setContent(new Intent(this, CalendarActivity.class));
        tabHost.addTab(tabSpec);

        //вкладка будет выбрана по умолчанию
        tabHost.setCurrentTabByTag("calendarTab");

    }


}
