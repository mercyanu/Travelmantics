package com.example.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class TravelListAdapter extends RecyclerView.Adapter<TravelListAdapter.DealViewHolder> {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ArrayList<TravelDeal> mDeals;
    private Context context;
    ImageView imageView;

    public TravelListAdapter(Context context){
        this.context = context;
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        mDeals = FirebaseUtil.mDeals;

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal deal = dataSnapshot.getValue(TravelDeal.class);
                Log.d("TRAVELLISTADAPTER", deal.getTitle());
                deal.setId(dataSnapshot.getKey());
                mDeals.add(deal);
                notifyItemInserted(mDeals.size() - 1); //notify when a new record is added at the last position
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.deal_list_item, parent, false);
        return new DealViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal currentDeal = mDeals.get(position);
        holder.bind(currentDeal);
    }

    @Override
    public int getItemCount() {
        return mDeals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dealTitle;
        TextView dealDescription;
        TextView dealPrice;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            dealTitle = itemView.findViewById(R.id.deal_title);
            dealDescription = itemView.findViewById(R.id.deal_description);
            dealPrice = itemView.findViewById(R.id.deal_price);
            imageView = itemView.findViewById(R.id.deal_img);

            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal deal){
            dealTitle.setText(deal.getTitle());
            dealDescription.setText(deal.getDescription());
            dealPrice.setText(deal.getPrice());
            showImage(deal.getImageUrl());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            TravelDeal dealSelected = mDeals.get(position);
            Intent i = new Intent(context, DealActivity.class);
            i.putExtra("mDeal", dealSelected);
            context.startActivity(i);
        }

        private void showImage(String url){
            if (url != null && !url.isEmpty()){
                Picasso.get()
                        .load(url)
                        .resize(80, 80)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }




}
