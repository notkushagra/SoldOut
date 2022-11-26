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


public class SellingProductsRecyclerViewAdapter extends RecyclerView.Adapter<SellingProductsRecyclerViewAdapter.MyViewHolder> {
    final String TAG = "SellingProductsAdapter";
    Context context;
    ArrayList<SellingProduct> sellingProductArrayList;

    public SellingProductsRecyclerViewAdapter(Context context, ArrayList<SellingProduct> sellingProductArrayList) {
        this.context = context;
        this.sellingProductArrayList = sellingProductArrayList;
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

        SellingProduct sellingProduct = sellingProductArrayList.get(position);
        if (sellingProduct.images.size()>0) {
            Glide.with(context)
                    .load(sellingProduct.images.get(0))
                    .into(holder.imageView);
        }
        holder.textView1.setText("" + (sellingProduct.name).toString());
        holder.textView2.setText("" + (sellingProduct.price).toString());

    }

    @Override
    public int getItemCount() {
        return sellingProductArrayList.size();
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
            SellingProduct sellingProduct = sellingProductArrayList.get(getAdapterPosition());
            Log.d(TAG, "Image clicked");
            Intent intent = new Intent(context, SellingProductDetailsActivity.class);
            intent.putExtra("EXTRA_PRODUCT_ID", sellingProduct.getProductId());

            //creates an intent and puts a pair inside the intent {key:value} -> { "EXTRA_PRODUCT_ID" : productId}
            context.startActivity(intent);
        }
    }
}
