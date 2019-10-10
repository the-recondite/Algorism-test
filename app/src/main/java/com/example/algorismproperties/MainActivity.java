package com.example.algorismproperties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.Model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//Main Activity to display properties
public class MainActivity extends AppCompatActivity {

    //Define variables
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind variables to views
        recyclerView = findViewById(R.id.rv_properties);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //call fetch method
        fetch();
    }

    //create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.properties_menu, menu);
        return true;
    }

    //attach fucntion to menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.create_property) {
            startActivity(new Intent(this, CreatePropertyActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Provide a reference to the views for each data item
    // you provide access to all the views for a data item in a view holder
    public class PropertiesViewHolder extends RecyclerView.ViewHolder {
        TextView propertyTitle;
        ImageView propertyPhoto;
        TextView bedCount;
        TextView parkingCount;
        TextView bathCount;
        TextView idTextView;
        CardView cardView;

        public PropertiesViewHolder(View itemView) {
            super(itemView);
            propertyTitle = itemView.findViewById(R.id.propertyTitle);
            propertyPhoto = itemView.findViewById(R.id.imageView);
            bedCount = itemView.findViewById(R.id.bedCount);
            parkingCount = itemView.findViewById(R.id.parkingCount);
            bathCount = itemView.findViewById(R.id.bathCount);
            idTextView = itemView.findViewById(R.id.idTextView);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    //The purpose of this method is to get values from the database and attach them to the recycler view
    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Mine");
        FirebaseRecyclerOptions<Property> options =
                new FirebaseRecyclerOptions.Builder<Property>()
                        .setQuery(query, new SnapshotParser<Property>() {
                            @NonNull
                            @Override
                            public Property parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Property(snapshot.child("name").getValue().toString(),
                                        snapshot.child("address").getValue().toString(),
                                        snapshot.child("price").getValue().toString(),
                                        snapshot.child("bed").getValue().toString(),
                                        snapshot.child("bath").getValue().toString(),
                                        snapshot.child("parking").getValue().toString(),
                                        snapshot.child("year").getValue().toString(),
                                        snapshot.child("description").getValue().toString(),
                                        snapshot.child("download link").getValue().toString(),
                                        snapshot.child("key").getValue().toString());
                            }
                        })
                        .build();
        adapter = new FirebaseRecyclerAdapter<Property, PropertiesViewHolder>(options) {
            @Override
            public PropertiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.containerlist, parent, false);
                return new PropertiesViewHolder(view);
            }

            //bind views to recycler view
            @Override
            protected void onBindViewHolder(@NonNull PropertiesViewHolder holder, int position, final @NonNull Property model) {
                holder.propertyTitle.setText(model.getName());
                Glide.with(getApplicationContext())
                        .load(model.getDownload())
                        .into(holder.propertyPhoto);
                holder.bedCount.setText(model.getBed());
                holder.parkingCount.setText(model.getParking());
                holder.bathCount.setText(model.getBath());
                holder.idTextView.setText(model.getId());
                final String s = model.getId();

                //click function for cards
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent detailedActivityIntent = new Intent(MainActivity.this, detailActivity.class);
                        detailedActivityIntent.putExtra("ID", s);
                        startActivity(detailedActivityIntent);
                    }
                });

                //Long click function for cards. You can edit or delete
                holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Confirmation")
                                .setMessage("Do you want to edit or delete this property")
                                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent detailedActivityIntent = new Intent(MainActivity.this, EditActivity.class);
                                        detailedActivityIntent.putExtra("ID", model.getId());
                                        startActivity(detailedActivityIntent);
                                    }
                                })
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle("Confirmation")
                                                .setMessage("Are you sure you want to delete?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Mine").child(model.getId());
                                                        databaseReference.removeValue();
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        return;
                                                    }
                                                })
                                                .show();
                                    }
                                })
                                .show();
                        return true;
                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    }



