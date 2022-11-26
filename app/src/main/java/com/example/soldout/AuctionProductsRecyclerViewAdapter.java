package com.example.soldout;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class AuctionProductsRecyclerViewAdapter extends RecyclerView.Adapter<AuctionProductsRecyclerViewAdapter.MyViewHolder> {
    final String TAG = "AuctionProductsRecyclerViewAdapter";
    Context context;
    ArrayList<AuctionProduct> auctionProductArrayList;

    public AuctionProductsRecyclerViewAdapter(Context context, ArrayList<AuctionProduct> auctionProductArrayList) {
        this.context = context;
        this.auctionProductArrayList = auctionProductArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        AuctionProduct auctionProduct = auctionProductArrayList.get(position);
        if (auctionProduct.images.size() > 0) {
            Glide.with(context)
                    .load(auctionProduct.images.get(0))
                    .into(holder.imageView);
        }
        if (auctionProduct.name != null)
            holder.textView1.setText("" + (auctionProduct.name).toString());
        if (auctionProduct.price != null)
            holder.textView2.setText("" + (auctionProduct.price).toString());

    }

    @Override
    public int getItemCount() {
        return auctionProductArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView1;
        TextView textView2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View view) {
            AuctionProduct auctionProduct = auctionProductArrayList.get(getAdapterPosition());
            Intent intent = new Intent(context, AuctionProductDetailsActivity.class);
            intent.putExtra("EXTRA_PRODUCT_ID", auctionProduct.getProductId());

            //creates an intent and puts a pair inside the intent {key:value} -> { "EXTRA_PRODUCT_ID" : productId}
            context.startActivity(intent);
        }
    }

}
