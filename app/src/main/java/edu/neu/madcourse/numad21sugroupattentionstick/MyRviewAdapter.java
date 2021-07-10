package edu.neu.madcourse.numad21sugroupattentionstick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRviewAdapter extends RecyclerView.Adapter<MyRviewHolder> {

    private final ArrayList<MyItemCard> itemList;
    private ItemClickListener listener;

    //Constructor
    public MyRviewAdapter(ArrayList<MyItemCard> itemList) {
        this.itemList = itemList;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public MyRviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_item_card, parent, false);
        return new MyRviewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(MyRviewHolder holder, int position) {
        MyItemCard currentItem = itemList.get(position);

        //holder.itemIcon.setImageResource(currentItem.getImageSource());
        holder.itemName.setText(currentItem.getItemName());
        holder.itemDesc.setText(currentItem.getItemDesc());
        //holder.checkBox.setChecked(currentItem.getStatus());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
