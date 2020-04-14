package com.example.omnicurisonlinemediaplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.VideoView;

import com.example.omnicurisonlinemediaplayer.Model.DisplayContent;
import com.example.omnicurisonlinemediaplayer.ViewHolder.Design;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    VideoView videoplayer;
    RelativeLayout relativeLayout;
    FrameLayout frameLayout;
    String Link = null, currentVID = null;
    String string1, string2;
    private RecyclerView recyclerView;
    DatabaseReference DisplayReference;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<DisplayContent, Design> adapter;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        Link = getIntent().getStringExtra("Link");
        currentVID = getIntent().getStringExtra("id");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        frameLayout = (FrameLayout) findViewById(R.id.video_frame);
        MediaController mediaController = new MediaController(this);
        videoplayer = (VideoView) findViewById(R.id.video_view);
        videoplayer.setMediaController(mediaController);
        mediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("Link", string1);
                intent.putExtra("id", string2);
                finish();
                startActivity(intent);


            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        videoplayer.setMediaController(mediaController);

        Uri uri = Uri.parse("" + Link);
        videoplayer.setVideoURI(uri);
        videoplayer.start();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(false);
        DisplayReference = FirebaseDatabase.getInstance().getReference().child("DisplayContent");
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            frameLayout.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            frameLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            relativeLayout.setVisibility(View.INVISIBLE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
            params.height = 700;
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<DisplayContent> options = new FirebaseRecyclerOptions.Builder<DisplayContent>()
                .setQuery(DisplayReference, DisplayContent.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<DisplayContent, Design>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Design holder, int position, @NonNull final DisplayContent model) {
                holder.imageView.setText("Text" + model.getVtitle());
                Picasso.get().load(model.getThum()).into(holder.imageView1);

                string1 = model.getVlink();
                string2 = model.getId();


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //     Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.putExtra("Link", model.getVlink());
                        intent.putExtra("id", model.getId());
                        finish();
                        startActivity(intent);

                    }
                });
            }


            @NonNull
            @Override
            public Design onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.designs, parent, false);
                Design holder = new Design(view);

                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoplayer.stopPlayback();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoplayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoplayer.pause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
