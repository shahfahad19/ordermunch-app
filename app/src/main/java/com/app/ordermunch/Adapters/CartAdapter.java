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

import com.app.ordermunch.Models.CartItem;
import com.app.ordermunch.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    // list for cart items
    private List<CartItem> itemList;
    private LayoutInflater inflater;

    // click listeners
    public interface CartItemClickListener {
        void onRemoveItemClicked(String id);
        void onAddItemClicked(String id);
        void onDeleteItemClicked(String id);

    }

    private CartItemClickListener cartItemClickListener;


    public CartAdapter(Context context, CartItemClickListener listener) {
        this.itemList = new ArrayList<>();
        this.cartItemClickListener = listener;
        inflater = LayoutInflater.from(context);
    }

    public void updateData(List<CartItem> items) {
        this.itemList = items;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_cartitem, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Setting data
        CartItem item = itemList.get(position);
        // Bind the data to the views in the grid item layout
        holder.itemName.setText(item.getItem().getName());
        holder.itemPrice.setText("MYR. "+item.getItem().getPrice());
        holder.itemCount.setText(String.valueOf(item.getCount()));
        holder.restaurantName.setText("Sold by: "+item.getItem().getRestaurant().getName());
        if (item.getCount() == 1) {
            holder.removeItemBtnImg.setImageResource(R.drawable.ic_remove_inactive);
        }
        else {
            holder.removeItemBtnImg.setImageResource(R.drawable.ic_remove);

        }

        // Load the Item image
        try {
            Picasso.get()
                    .load(item.getItem().getImage())
                    .into(holder.itemImage);
        }
        catch (Exception e) {}

        holder.addItemBtnImg.setOnClickListener(v -> {
            if (cartItemClickListener != null) {

                cartItemClickListener.onAddItemClicked(item.getItem().getId());
            }
        });

        holder.removeItemBtnImg.setOnClickListener(v -> {
            if (cartItemClickListener != null && item.getCount() > 1) {
                cartItemClickListener.onRemoveItemClicked(item.getItem().getId());
            }
        });

        holder.deleteItemBtnImg.setOnClickListener(v -> {
            if (cartItemClickListener != null) {
                cartItemClickListener.onDeleteItemClicked(item.getItem().getId());
            }
        });



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage, addItemBtnImg, removeItemBtnImg, deleteItemBtnImg;
        TextView itemName,  itemPrice, itemCount, restaurantName;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemCount = itemView.findViewById(R.id.itemCount);
            addItemBtnImg = itemView.findViewById(R.id.addBtn);
            removeItemBtnImg = itemView.findViewById(R.id.subtractBtn);
            deleteItemBtnImg = itemView.findViewById(R.id.itemDeleteBtn);
            restaurantName = itemView.findViewById(R.id.restaurantName);



        }
    }
}