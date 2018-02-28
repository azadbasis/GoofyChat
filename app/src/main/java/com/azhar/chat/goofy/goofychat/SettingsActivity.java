package com.azhar.chat.goofy.goofychat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Random;

public class SettingsActivity extends AppCompatActivity {


    private DatabaseReference mDatabaseReference;
    private FirebaseUser current_user;
    private StorageReference mStorageRef;
    Context context;

    private ImageView settings_image;
    private TextView settings_display_name, settings_status;
    private Button settings_image_btn, settings_status_btn;
    private static final int GALLERY_PICK = 100;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;


        settings_status_btn = (Button) findViewById(R.id.settings_status_btn);
        settings_image_btn = (Button) findViewById(R.id.settings_image_btn);
        settings_display_name = (TextView) findViewById(R.id.settings_display_name);
        settings_status = (TextView) findViewById(R.id.settings_status);
        settings_image = (ImageView) findViewById(R.id.settings_image);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = current_user.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

              /*  status = Hi there I am using default chat app,
                        name = azharul islam,
                        image = default,
                thumb_image = default*/


                String displayName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();


                settings_display_name.setText(displayName);
                settings_status.setText(status);
//                Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);
                Picasso.with(context).load(image).into(settings_image);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        settings_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status_value = settings_status.getText().toString();
                Intent status_intent = new Intent(context, StatusActivity.class);
                status_intent.putExtra("status", status_value);
                startActivity(status_intent);
            }
        });

        settings_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
// start picker to get image for cropping and then use the image in cropping activity
               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setTitle("Image uploading");
                mProgressDialog.setMessage("Please,wait while we upload and process image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                Uri resultUri = result.getUri();
                String current_user_id = current_user.getUid();
                StorageReference filePath = mStorageRef.child("profile_images").child(current_user_id+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            String downLoadurl = task.getResult().getDownloadUrl().toString();
                            mDatabaseReference.child("image").setValue(downLoadurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mProgressDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            Toast.makeText(context, "working", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "File upload not working", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                mProgressDialog.dismiss();
                Exception error = result.getError();
            }

        }
    }


    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}