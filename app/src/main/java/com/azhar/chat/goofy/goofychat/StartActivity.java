package com.azhar.chat.goofy.goofychat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {


    Context context;
    private Button start_reg_btn,start_log_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        context = this;
        start_reg_btn = (Button) findViewById(R.id.start_reg_btn);
        start_log_btn = (Button) findViewById(R.id.start_log_btn);

        start_reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, RegiserActivity.class));
            }
        });

        start_log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,LoginActivity.class));
            }
        });
    }
}
