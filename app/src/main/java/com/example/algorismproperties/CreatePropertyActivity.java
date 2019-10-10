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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//The purpose of this class is to add to the database
public class CreatePropertyActivity extends AppCompatActivity implements View.OnClickListener {

    //Create variables
    EditText propertyName;
    EditText propertyAddress;
    EditText propertyPrice;
    EditText bedCount;
    EditText bathCount;
    EditText parkingCount;
    EditText yearBuilt;
    EditText description;
    Button uploadButton;
    ProgressBar progressBar;
    Button createProperty;
    String downloadURL;

    //GET_FROM_GALLERY is used for picture uploading
    public static final int GET_FROM_GALLERY = 3;
    Bitmap bitmap = null;

    //Define Firebase Variables
    FirebaseStorage mFirebaseStorage;
    StorageReference algorismStorageReference;
    DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;

    //Bind variables to views, setOnClickListeners for views
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_property);
        mFirebaseStorage = FirebaseStorage.getInstance();
        algorismStorageReference = mFirebaseStorage.getReference().child("Algorism Photos");
        propertyName = findViewById(R.id.propertyName);
        propertyAddress = findViewById(R.id.propertyAddress);
        propertyPrice = findViewById(R.id.propertyPrice);
        bedCount = findViewById(R.id.bedCount);
        bathCount = findViewById(R.id.bathCount);
        parkingCount = findViewById(R.id.parkingCount);
        yearBuilt = findViewById(R.id.yearBuilt);
        description = findViewById(R.id.propertyDescription);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference();
        uploadButton = findViewById(R.id.uploadButton);
        progressBar = findViewById(R.id.progressBar);
        uploadButton.setOnClickListener(this);
        createProperty = findViewById(R.id.createProperty);
        createProperty.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.uploadButton) {
            uploadButton();
        }
        if (id == R.id.createProperty) {
            createProperty();
        }
    }


    //The purpose of this method is to get a result image and store in firebaseStorage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects Request Codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
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
                            progressBar.setVisibility(View.GONE);
                            createProperty.setClickable(true);
                        }
                    }
                });
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    //This method calls the validateInput method and writeData method.
   public void createProperty() {
        if (!validateInput()){
            return;
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            String pName = propertyName.getText().toString();
            String pAddress = propertyAddress.getText().toString();
            String pPrice = propertyPrice.getText().toString();
            String pBed = bedCount.getText().toString();
            String pBath = bathCount.getText().toString();
            String pParking = parkingCount.getText().toString();
            String pYear = yearBuilt.getText().toString();
            String pDescription = description.getText().toString();
            writeData(pName, pAddress, pPrice, pBed, pBath, pParking, pYear, pDescription, downloadURL);
            progressBar.setVisibility(View.GONE);
            clearData();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    //The purpose of this method is to check whether all textfields are filled and an image has been selected
    public boolean validateInput() {
        boolean result = true;
        if (TextUtils.isEmpty(propertyName.getText().toString())){
            propertyName.setError("Required");
            result = false;
        }
        else {
            propertyName.setError(null);
        }
        if (TextUtils.isEmpty(propertyAddress.getText().toString())){
            propertyAddress.setError("Required");
            result = false;
        }
        else {
            propertyAddress.setError(null);
        }
        if (TextUtils.isEmpty(propertyPrice.getText().toString())){
            propertyPrice.setError("Required");
            result = false;
        }
        else {
            propertyPrice.setError(null);
        }
        if (TextUtils.isEmpty(bedCount.getText().toString())){
            bedCount.setError("Required");
            result = false;
        }
        else {
            bedCount.setError(null);
        }
        if (TextUtils.isEmpty(bathCount.getText().toString())){
            bathCount.setError("Required");
            result = false;
        }
        else {
            bathCount.setError(null);
        }
        if (TextUtils.isEmpty(parkingCount.getText().toString())){
            parkingCount.setError("Required");
            result = false;
        }
        else {
            parkingCount.setError(null);
        }
        if (TextUtils.isEmpty(yearBuilt.getText().toString())){
            yearBuilt.setError("Required");
            result = false;
        }
        else {
            yearBuilt.setError(null);
        }
        if (TextUtils.isEmpty(description.getText().toString())){
            description.setError("Required");
            result = false;
        }
        else {
            description.setError(null);
        }
        if (bitmap == null){
            result = false;
            Toast.makeText(this, "Please upload a picture", Toast.LENGTH_LONG).show();
        }
        return result;
    }

    //The purpose of this method is to write data into the database
    public void writeData(String name, String address, String price, String bed, String bath, String parking, String year, String propDescription, String download){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Mine").push();
        Log.i("reference", databaseReference.getParent().toString());
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("address", address);
        map.put("price", price);
        map.put("bed", bed);
        map.put("bath", bath);
        map.put("parking", parking);
        map.put("year", year);
        map.put("description", propDescription);
        map.put("download link", download);
        map.put("key", databaseReference.getKey());
        databaseReference.setValue(map);
    }

    //This method is called when the "choose picture" button is pressed
    public void uploadButton() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), GET_FROM_GALLERY);
        progressBar.setVisibility(View.VISIBLE);
        createProperty.setClickable(false);
    }

    //The purpose of this method is to clear all textfields after submission
    public void clearData() {
        propertyName.setText("");
        propertyAddress.setText("");
        propertyPrice.setText("");
        bedCount.setText("");
        bathCount.setText("");
        parkingCount.setText("");
        yearBuilt.setText("");
        description.setText("");
    }
}
