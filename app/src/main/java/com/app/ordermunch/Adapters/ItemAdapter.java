package com.app.ordermunch.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ordermunch.Models.Item;
import com.app.ordermunch.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
    private LayoutInflater inflater;

    public interface ItemClickListener {
        void onItemClickListener(String id);

    }

    private ItemClickListener itemClickListener;

    public ItemAdapter(Context context, ItemClickListener listener) {
        this.itemList = new ArrayList<>();
        this.itemClickListener = listener;
        inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Item> items) {
        this.itemList = items;
        notifyDataSetChanged();
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_menuitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        // Bind the data to the views in the grid item layout
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText("MYR. "+item.getPrice());
        holder.itemRating.setText(item.getRating()+"/5");
        holder.itemRatedBy.setText("("+item.getRatedBy()+")");
        holder.restaurantName.setText(item.getRestaurant().getName());


        // Load the Item image using an image loading library like Glide or Picasso
        try {
            Picasso.get()
                    .load(item.getImage())
                    .into(holder.itemImage);
        }
        catch (Exception ignored) {}

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClickListener(item.getId());
            }
        });



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemRating,  itemPrice,  itemRatedBy, restaurantName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemRating = itemView.findViewById(R.id.itemRating);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemRatedBy = itemView.findViewById(R.id.itemRatedBy);
            restaurantName = itemView.findViewById(R.id.restaurantName);

        }
    }
}