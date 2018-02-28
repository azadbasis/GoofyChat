package com.azhar.chat.goofy.goofychat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegiserActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    Context context;


    private TextInputEditText mDisplayname, mEmail, mPassword;
    private Button mRegister;
    private Toolbar mRegToolbar;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiser);
        mAuth = FirebaseAuth.getInstance();
        context = this;

        mRegToolbar = (Toolbar) findViewById(R.id.reg_toolbar);
        setSupportActionBar(mRegToolbar);
        getSupportActionBar().setTitle("CREATE ACCOUNT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mProgressDialog = new ProgressDialog(context);

        mDisplayname = (TextInputEditText) findViewById(R.id.reg_display_name);
        mEmail = (TextInputEditText) findViewById(R.id.reg_email);
        mPassword = (TextInputEditText) findViewById(R.id.reg_password);
        mRegister = (Button) findViewById(R.id.reg_create_btn);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayname = mDisplayname.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (!TextUtils.isEmpty(displayname) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                    mProgressDialog.setTitle("Registering User");
                    mProgressDialog.setMessage("Please,wait while we registering user..");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    reg_user(displayname, email, password);
                }


            }
        });
    }

    private void reg_user(final String displayname, String email, String password) {


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", displayname);
                    userMap.put("status", "Hi there I am using custom chat app");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");
                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mProgressDialog.dismiss();
                                Intent mIntent = new Intent(context,MainActivity.class);
                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mIntent);
                                finish();
                            }
                        }
                    });


                } else {
                    mProgressDialog.hide();
                    Toast.makeText(context, "You can not sign in please,check the form and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
