package com.app.ordermunch.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ordermunch.Models.CartItem;
import com.app.ordermunch.Models.ItemReview;
import com.app.ordermunch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {

    //list for order items and reviews
    private List<CartItem> itemList;
    private List<ItemReview> reviewList;
    private String orderStatus;

    private final LayoutInflater inflater;

    public interface ItemClickListener {
        void onItemClickListener(String itemId);
        void addReviewClickListener(String itemId);
        void updateReviewClickListener(ItemReview review);

    }

    private ItemClickListener itemClickListener;

    public OrderItemsAdapter(Context context, ItemClickListener listener) {
        this.itemList = new ArrayList<>();
        this.reviewList = new ArrayList<>();
        this.itemClickListener = listener;
        inflater = LayoutInflater.from(context);
    }

    public void updateData(List<CartItem> items, List<ItemReview> reviewList, String orderStatus) {
        this.itemList = items;
        this.reviewList = reviewList;
        this.orderStatus = orderStatus;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_orderitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // hide review and buttons first
        holder.reviewLayout.setVisibility(View.GONE);
        holder.reviewBtnLayout.setVisibility(View.GONE);
        holder.addReviewBtn.setVisibility(View.GONE);
        holder.updateReviewBtn.setVisibility(View.GONE);


        CartItem cartItem = itemList.get(position);
        // Bind the data to the views in the grid item layout
        holder.itemName.setText(cartItem.getItem().getName());
        holder.itemPrice.setText("MYR. "+cartItem.getItem().getPrice());
        holder.itemCount.setText("x "+cartItem.getCount());
        holder.restaurantName.setText(cartItem.getItem().getRestaurant().getName());


        // Load the Item image 
        try {
            Picasso.get()
                    .load(cartItem.getItem().getImage())
                    .into(holder.itemImage);
        }
        catch (Exception ignored) {}


        // if order is completed, only then show add review button
        if (orderStatus.equals("Completed")) {
            holder.reviewBtnLayout.setVisibility(View.VISIBLE);
        }

        ItemReview foundReview = reviewList.stream().filter(review -> review.getItem().equals(cartItem.getItem().getId())).findFirst().orElse(null);

        if (foundReview != null) {

            // If review found, show review and update review button
            holder.updateReviewBtn.setVisibility(View.VISIBLE);
            holder.reviewLayout.setVisibility(View.VISIBLE);
            holder.review.setText(foundReview.getReview());
            holder.stars.setText(foundReview.getStars()+"/5");
        }
        else {

            // if no review found show add review button
            holder.addReviewBtn.setVisibility(View.VISIBLE);

        }


        // click listeners for different parts of item
        holder.itemLayout.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClickListener(cartItem.getItem().getId());
            }
        });

        holder.addReviewBtn.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.addReviewClickListener(cartItem.getItem().getId());
            }
        });

        holder.updateReviewBtn.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.updateReviewClickListener(foundReview);
            }
        });



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName,  itemPrice,  itemCount, restaurantName, review, stars;

        LinearLayout itemLayout, reviewLayout, reviewBtnLayout;

        Button addReviewBtn, updateReviewBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemCount = itemView.findViewById(R.id.itemCount);
            restaurantName = itemView.findViewById(R.id.restaurantName);
            review = itemView.findViewById(R.id.review);
            stars = itemView.findViewById(R.id.stars);
            itemLayout = itemView.findViewById(R.id.itemLayout);
            reviewLayout = itemView.findViewById(R.id.itemReviewLayout);
            reviewBtnLayout = itemView.findViewById(R.id.reviewBtnsLayout);
            addReviewBtn = itemView.findViewById(R.id.addReviewBtn);
            updateReviewBtn = itemView.findViewById(R.id.updateReviewBtn);
        }
    }
}