package com.example.soldout;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.MyViewHolder> {
    final String TAG = "NotificationRecyclerViewAdapter";
    Context context;
    ArrayList<Notification> notificationArrayList;

    public NotificationRecyclerViewAdapter(Context context, ArrayList<Notification> notificationArrayList) {
        this.context = context;
        this.notificationArrayList = notificationArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notif, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notification notificaton = notificationArrayList.get(position);
        holder.title.setText(notificaton.getTitle());
        holder.text.setText(notificaton.getText());
    }

    @Override
    public int getItemCount() {
        if (notificationArrayList == null)
            return 0;
        return notificationArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView text;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View view) {
            Notification notification = notificationArrayList.get(getAdapterPosition());
            //notification main mentioned hoga
            if (notification.isForSale() == true) {
                Intent intent = new Intent(context, SellingProductDetailsActivity.class);
                intent.putExtra("EXTRA_PRODUCT_ID", notification.getProductId());
                //creates an intent and puts a pair inside the intent {key:value} -> { "EXTRA_PRODUCT_ID" : productId}
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, AuctionProductDetailsActivity.class);
                intent.putExtra("EXTRA_PRODUCT_ID", notification.getProductId());
                //creates an intent and puts a pair inside the intent {key:value} -> { "EXTRA_PRODUCT_ID" : productId}
                context.startActivity(intent);
            }
        }
    }
}
