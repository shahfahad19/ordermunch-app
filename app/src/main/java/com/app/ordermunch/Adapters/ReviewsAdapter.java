package com.app.ordermunch.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ordermunch.Models.ItemReview;
import com.app.ordermunch.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<ItemReview> reviewList;
    private LayoutInflater inflater;



    public ReviewsAdapter(Context context) {
        this.reviewList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public void updateData(List<ItemReview> reviews) {
        this.reviewList = reviews;
        notifyDataSetChanged();
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_itemreview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemReview review = reviewList.get(position);
        // Bind the data to the views in the grid review layout


        holder.userName.setText(review.getPosted_by().getName());
        holder.review.setText(review.getReview());
        holder.stars.setText(review.getStars()+"/5");

        // Defining input and output date formats
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a dd MMM, yyyy", Locale.ENGLISH);

        // Setting the input date's time zone to UTC
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String formattedDate = "";
        try {
            Date date = isoFormat.parse(review.getDate());
            formattedDate = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.date.setText("posted "+formattedDate);


    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, review, stars, date;


        public ViewHolder(@NonNull View reviewView) {
            super(reviewView);
            userName = reviewView.findViewById(R.id.userName);
            review = reviewView.findViewById(R.id.review);
            stars = reviewView.findViewById(R.id.stars);
            date = reviewView.findViewById(R.id.reviewDate);

        }
    }
}