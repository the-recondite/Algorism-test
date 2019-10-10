package com.example.algorismproperties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


//The purpose of this class is to show the details of a particular property card when it is clicked
public class detailActivity extends AppCompatActivity {

    //Define Variables
    TextView title;
    TextView address;
    ImageView photo;
    TextView price;
    TextView bed;
    TextView parking;
    TextView bath;
    TextView year;
    TextView description;

    //Firebase References
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    //Bind variables to views
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        title = findViewById(R.id.propertyTitle);
        address = findViewById(R.id.propertyAddress);
        photo = findViewById(R.id.imageView);
        price = findViewById(R.id.priceTextView);
        bed = findViewById(R.id.bedCount);
        parking = findViewById(R.id.parkingCount);
        bath = findViewById(R.id.bathCount);
        year = findViewById(R.id.yearTextView);
        description = findViewById(R.id.descriptionTextView);
        final String data = getIntent().getExtras().getString("ID");

        //get databasereference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //create valueeventlistener to put text into views
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                title.setText(dataSnapshot.child("name").getValue().toString());
                address.setText(dataSnapshot.child("address").getValue().toString());
                String image = dataSnapshot.child("download link").getValue().toString();
                Glide.with(getApplicationContext())
                        .load(image)
                        .into(photo);
                price.setText(dataSnapshot.child("price").getValue().toString());
                bed.setText(dataSnapshot.child("bed").getValue().toString());
                parking.setText(dataSnapshot.child("parking").getValue().toString());
                bath.setText(dataSnapshot.child("bath").getValue().toString());
                year.setText(dataSnapshot.child("year").getValue().toString());
                description.setText(dataSnapshot.child("description").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //add the created valueeventlistener to databasereference
        databaseReference.child("Mine").child(data).addValueEventListener(valueEventListener);
    }

    //create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    //Attach function to menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_property) {
            Intent detailedActivityIntent = new Intent(detailActivity.this, EditActivity.class);
            detailedActivityIntent.putExtra("ID", getIntent().getExtras().getString("ID"));
            startActivity(detailedActivityIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
