package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public static final int PICTURE_REQUEST = 42;

    EditText txtTitle;
    EditText txtDescription;
    EditText txtPrice;
    TravelDeal mDeal;
    Button uploadButton;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtPrice);
        uploadButton = findViewById(R.id.uploadButton);
        imageView = findViewById(R.id.dealImage);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(getIntent().createChooser(i, "Insert Picture"), PICTURE_REQUEST);
            }
        });

        Intent i = getIntent();
        TravelDeal sentDeal = (TravelDeal) i.getSerializableExtra("mDeal");
        if (sentDeal == null) {
            sentDeal = new TravelDeal();
        }
        this.mDeal = sentDeal;
        txtTitle.setText(sentDeal.getTitle());
        txtDescription.setText(sentDeal.getDescription());
        txtPrice.setText(sentDeal.getPrice());
        showImage(mDeal.getImageUrl());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.deal_menu, menu);

        MenuItem deleteMenu = menu.findItem(R.id.delete_deal);
        MenuItem saveMenu = menu.findItem(R.id.save_menu);

        if(FirebaseUtil.isAdmin == true){
            deleteMenu.setVisible(true);
            saveMenu.setVisible(true);
            enableFormFields(true);
        }
        else{
            deleteMenu.setVisible(false);
            saveMenu.setVisible(false);
            enableFormFields(false);
            uploadButton.setVisibility(View.GONE);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                return true;
            case R.id.delete_deal:
                deleteDeal();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void saveDeal() {
        mDeal.setTitle(txtTitle.getText().toString());
        mDeal.setDescription(txtDescription.getText().toString());
        mDeal.setPrice(txtPrice.getText().toString());

        if (mDeal.getId() == null) {
            mDatabaseReference.push().setValue(mDeal);
            clean();
        } else {
            mDatabaseReference.child(mDeal.getId()).setValue(mDeal);
            finish();
        }
    }

    private void deleteDeal() {
        if (mDeal.getId() == null) {
            Toast.makeText(this, "Nothing to delete", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
                mDatabaseReference.child(mDeal.getId()).removeValue();
                if(mDeal.getImageName() != null && !mDeal.getImageName().isEmpty()){
                    StorageReference picRef = FirebaseUtil.mFirebaseStorage.getReference().child(mDeal.getImageName());
                    picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(DealActivity.this, "Deal deleted", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DealActivity.this, "Unable to delete deal", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
            finish();
        }
    }

    private void clean() {
        txtPrice.setText("");
        txtDescription.setText("");
        txtTitle.setText("");
    }

    private void enableFormFields(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICTURE_REQUEST && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            final StorageReference ref = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    //com.google.gms is returned by getDownloadUrl, hence, added OnSuccessListener to get uri
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mDeal.setImageUrl(uri.toString());
                            mDeal.setImageName(taskSnapshot.getStorage().getPath());
                            showImage(mDeal.getImageUrl());
                            Log.d("Image Upload Url", uri.toString());
                            Log.d("Model image url", mDeal.getImageUrl());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            });
        }
    }

    private void showImage(String url){
        if(url != null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }
}
