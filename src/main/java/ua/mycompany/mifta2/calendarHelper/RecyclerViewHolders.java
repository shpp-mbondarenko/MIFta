package ua.mycompany.mifta2.calendarHelper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ua.mycompany.mifta2.DayTask;
import ua.mycompany.mifta2.R;

/**
 * Created by Maxim on 05.06.2016.
 */
public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView tvDay;
    public ImageView ivImageOfDay;
    private Context context;

    public RecyclerViewHolders(View itemView, Context context) {
        super(itemView);
        this.context = context;
        itemView.setOnClickListener(this);
        tvDay = (TextView)itemView.findViewById(R.id.tvDay);
        ivImageOfDay = (ImageView)itemView.findViewById(R.id.ivImageOfDay);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Day Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        Intent dayTask = new Intent(context, DayTask.class);
        context.startActivity(dayTask);
    }
}
