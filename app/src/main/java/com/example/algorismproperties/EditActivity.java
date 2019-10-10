package com.example.algorismproperties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

//The purpose of thisclass is to edit the database
public class EditActivity extends AppCompatActivity {

    //Define variales
    TextView title;
    TextView address;
    ImageView photo;
    TextView price;
    TextView bed;
    TextView parking;
    TextView bath;
    TextView year;
    TextView description;
    ProgressBar progressBar;
    Bitmap bitmap;

    //Firebase Variables
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    ValueEventListener changeValueEventListener;
    StorageReference algorismStorageReference;
    ProgressBar updateProgressBar;
    FirebaseStorage mFirebaseStorage;

    //create a variable to store the link for the url to the property image on firebase database
    String downloadURL = "";

    //GET_FROM_GALLERY is used when a picture is to be selected
    private static int GET_FROM_GALLERY = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //Bind variables to views
        title = findViewById(R.id.propertyTitle);
        address = findViewById(R.id.propertyAddress);
        photo = findViewById(R.id.imageView);
        price = findViewById(R.id.priceTextView);
        bed = findViewById(R.id.bedCount);
        parking = findViewById(R.id.parkingCount);
        bath = findViewById(R.id.bathCount);
        year = findViewById(R.id.yearTextView);
        description = findViewById(R.id.descriptionTextView);
        progressBar = findViewById(R.id.progressBar);
        updateProgressBar = findViewById(R.id.updateProgressBar);

        //get intent containing the id key which will to used to access specific values
        final String data = getIntent().getExtras().getString("ID");

        //firebase initialization
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseStorage = FirebaseStorage.getInstance();
        algorismStorageReference = mFirebaseStorage.getReference().child("Algorism Photos");

        //create valueeventlistener
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //add valueeventlistener to databasereference
        databaseReference.child("Mine").child(data).addValueEventListener(valueEventListener);
    }

    //This method is called when the choose picture button is pressed. The purpose is to start the activity to pick a picture
    public void choose(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        progressBar.setVisibility(View.VISIBLE);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), GET_FROM_GALLERY);
    }

    //This method is called when the update button is pressed. The purpose is to update the database with data
    public void update(View view) {
        updateProgressBar.setVisibility(View.VISIBLE);
        final String name = title.getText().toString();
        final String add = address.getText().toString();
        final String cash = price.getText().toString();
        final String mattrass = bed.getText().toString();
        final String park = parking.getText().toString();
        final String baff = bath.getText().toString();
        final String date = year.getText().toString();
        final String explanation = description.getText().toString();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("address", add);
        map.put("price", cash);
        map.put("bed", mattrass);
        map.put("bath", baff);
        map.put("parking", park);
        map.put("year", date);
        map.put("description", explanation);
        if (downloadURL.equals("")){
        }
        else{
            map.put("download link", downloadURL);
        }
        databaseReference.child("Mine").child(getIntent().getExtras().getString("ID")).updateChildren(map);
        updateProgressBar.setVisibility(View.GONE);
        Intent detailedActivityIntent = new Intent(this, detailActivity.class);
        detailedActivityIntent.putExtra("ID", getIntent().getExtras().getString("ID"));
        startActivity(detailedActivityIntent);
        finish();
    }

    //The method is called from the choose method to pick a picture and store it in firebase
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects Request Codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                photo.setImageBitmap(bitmap);
                final StorageReference photoRef = algorismStorageReference.child(selectedImage.getLastPathSegment());
                UploadTask uploadTask = photoRef.putFile(selectedImage);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return photoRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            downloadURL = downloadUri.toString();
                            Log.i("url", downloadURL);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
