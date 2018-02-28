package com.azhar.chat.goofy.goofychat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Context context;
    private ProgressDialog mProgressDialog;
    TextInputEditText login_email,login_password;
    Button login_btn;
private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        mToolbar = (Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("LOGIN");



        mProgressDialog = new ProgressDialog(context);
        mAuth = FirebaseAuth.getInstance();

        login_email = (TextInputEditText)findViewById(R.id.login_email);
        login_password = (TextInputEditText)findViewById(R.id.login_password);
        login_btn = (Button)findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = login_email.getText().toString();
                String password = login_password.getText().toString();
                if(!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)){
                    mProgressDialog.setTitle("loggin In");
                    mProgressDialog.setMessage("please, wait while we check your credential");
                    mProgressDialog.show();
                    login(email,password);
                }
            }
        });


    }

    private void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    mProgressDialog.dismiss();
                    Intent mIntent = new Intent(context,MainActivity.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                    finish();
                }else{
                    mProgressDialog.hide();
                    Toast.makeText(context, "You can not sign in please,check the form and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
