package com.app.ordermunch.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ordermunch.Models.Restaurant;
import com.app.ordermunch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    // restaurant list
    private List<Restaurant> restaurantsList;
    private LayoutInflater inflater;

    public interface RestaurantClickListener {
        void onRestaurantClickListener(String id);

    }

    private RestaurantClickListener restaurantClickListener;

    Context context;

    private final boolean isGridView;

    public RestaurantAdapter(Context context, RestaurantClickListener restaurantClickListener, boolean isGridView) {
        restaurantsList = new ArrayList<>();
        this.isGridView = isGridView;
        this.restaurantClickListener = restaurantClickListener;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    // Method to update the data in the adapter
    public void updateData(List<Restaurant> restaurantsList) {
        this.restaurantsList = restaurantsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (isGridView) {
            holder.cardView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.restaurantImage.getLayoutParams().width =ViewGroup.LayoutParams.MATCH_PARENT;;
        }

        Restaurant restaurant = restaurantsList.get(position);
        // Bind the data to the views in the grid item layout
        holder.restaurantName.setText(restaurant.getName());

        // Load the Restaurant image using an image loading library like Glide or Picasso
        try {
            Picasso.get()
                    .load(restaurant.getImage())
                    .into(holder.restaurantImage);
        }
        catch (Exception e) {}

        holder.itemView.setOnClickListener(v -> {
            if (restaurantClickListener != null) {
                restaurantClickListener.onRestaurantClickListener(restaurant.getId());
            }
        });



    }

    @Override
    public int getItemCount() {
        return restaurantsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView restaurantImage;
        TextView restaurantName;

        CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurantImage);
            restaurantName = itemView.findViewById(R.id.restaurantName);
            cardView = itemView.findViewById(R.id.itemCardView);

        }
    }
}