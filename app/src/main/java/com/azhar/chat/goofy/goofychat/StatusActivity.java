package com.azhar.chat.goofy.goofychat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {


    Context context;
    private Toolbar status_bar_layout;
    private TextInputEditText status_input;
    private Button status_btn;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        context = this;


        status_bar_layout = (Toolbar) findViewById(R.id.status_bar_layout);
        setSupportActionBar(status_bar_layout);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mProgressDialog = new ProgressDialog(context);
        String status_value = getIntent().getStringExtra("status");

        status_input = (TextInputEditText) findViewById(R.id.stattus_input);
        status_input.setText(status_value);
        status_btn = (Button) findViewById(R.id.status_btn);
        status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.setTitle("Account Status");
                mProgressDialog.setMessage("Please wait while we save the changes");
                mProgressDialog.show();

                String status = status_input.getText().toString();
                current_user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = current_user.getUid();
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                mDatabaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                        } else {
                            Toast.makeText(context, "There were some error in saving change", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


    }
}
