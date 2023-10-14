package com.app.ordermunch.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ordermunch.Models.Order;
import com.app.ordermunch.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Order> orderList;
    private LayoutInflater inflater;

    private Map<String, Integer> stateColors = new HashMap<>();
    Context context;


    public interface OrderClickListener {
        void onOrderClickListener(String id);

    }

    private OrderClickListener orderClickListener;

    public OrderAdapter(Context context, OrderClickListener listener) {
        this.context = context;
        this.orderList = new ArrayList<>();
        this.orderClickListener = listener;
        inflater = LayoutInflater.from(context);
        stateColors.put("Pending", R.color.colorPending);
        stateColors.put("Confirmed", R.color.colorConfirmed);
        stateColors.put("Preparing", R.color.colorPreparing);
        stateColors.put("Dispatched", R.color.colorDispatched);
        stateColors.put("Completed", R.color.colorCompleted);
        stateColors.put("Cancelled", R.color.colorCancelled);
    }

    public void updateData(List<Order> items) {
        this.orderList = items;
        notifyDataSetChanged();
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        // Bind the data to the views in the grid item layout
        holder.orderId.setText(order.getId());
        holder.orderAmount.setText("MYR. "+order.getAmount());
        holder.orderStatus.setText(order.getStatus());
        holder.orderItems.setText("Items: "+order.getItemsCount());

        int colorResId = stateColors.get(order.getStatus());
        holder.orderStatus.setTextColor(ContextCompat.getColor(context, colorResId));



        // Defining input and output date formats
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a dd MMM, yyyy", Locale.ENGLISH);

        // Setting the input date's time zone to UTC
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = isoFormat.parse(order.getDate());
            String formattedDate = outputFormat.format(date);
            holder.orderDate.setText(formattedDate);
        } catch (ParseException e) {
            holder.orderDate.setText("");
            e.printStackTrace();
        }


        holder.itemView.setOnClickListener(v -> {
            if (orderClickListener != null) {
                orderClickListener.onOrderClickListener(order.getId());
            }
        });



    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId,  orderDate,  orderAmount, orderItems, orderStatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderItems = itemView.findViewById(R.id.orderItems);
            orderAmount = itemView.findViewById(R.id.orderAmount);
            orderStatus = itemView.findViewById(R.id.orderStatus);

        }
    }
}