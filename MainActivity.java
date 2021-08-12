package com.codewithharry.firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //Views
    Button mRegisterButton, mLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init views
        mRegisterButton = findViewById(R.id.register_btn);
        mLoginButton = findViewById(R.id.login_btn);
        //Handle register btn
        mRegisterButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //start RegisterActivity
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });

        //Handle Login button click
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start LoginActivity
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
    }
}

/*ERRORS
* 1)Last seen
* 2)Message seen
* 3)Color comb at cover photo*/