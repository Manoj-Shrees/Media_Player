package com.dreamhunterztech.media_player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class Onelinevideolist extends AppCompatActivity {
RecyclerView itemlist;
    DatabaseReference ref;
    Button close_btn;
    FirebaseRecyclerAdapter  itemadapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onlinevideolistlayout);
        itemlist = findViewById(R.id.onlinevideolist);
        close_btn = findViewById(R.id.back_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ref = FirebaseDatabase.getInstance().getReference();
        Query query=ref;
        Log.e(">>firedatebase"," check => "+query);
        FirebaseRecyclerOptions<firegetdata> options =
                new FirebaseRecyclerOptions.Builder<firegetdata>()
                        .setQuery(query, firegetdata.class)
                        .build();


        itemadapter = new FirebaseRecyclerAdapter<firegetdata, itemdataviewholder>(options)  {

            @NonNull
            @Override
            public itemdataviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.onlinevideoitem, parent, false);

                return new itemdataviewholder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull itemdataviewholder holder, int position, @NonNull final firegetdata model) {

                holder.setImageurl(model.getVideothumb() ,getApplicationContext());
                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Onelinevideolist.this, testplayer.class).putExtra("video", "test,>" +model.getVideourl().toString()));
                    }
                });
            }
        };

        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        itemlist.setLayoutManager(gaggeredGridLayoutManager);
        itemlist.setAdapter(itemadapter);
    }



    public  static class itemdataviewholder extends RecyclerView.ViewHolder
    {
        View view;
        ImageView videoimg;
        CardView card;
        public itemdataviewholder(View itemView) {
            super(itemView);
            view = itemView;
            videoimg = view.findViewById(R.id.onlinevideoitemimg);
            card = view.findViewById(R.id.videoitemcard);
        }

        public void setImageurl(String imgurl , Context context)
        {

            Log.e(">>imgurl",". "+imgurl);
            Picasso.get().load(imgurl).into(videoimg);
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        itemadapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        itemadapter.stopListening();
    }


}
