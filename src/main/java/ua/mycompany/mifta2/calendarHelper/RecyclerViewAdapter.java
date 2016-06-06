package ua.mycompany.mifta2.calendarHelper;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ua.mycompany.mifta2.R;


/**
 * Created by Maxim on 05.06.2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    private List<ItemObject> itemList;
    private Context context;

    public RecyclerViewAdapter(Context context, List<ItemObject> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_item, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        holder.tvDay.setText(itemList.get(position).getDate());
        holder.ivImageOfDay.setImageResource(itemList.get(position).getImageOfDay());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}