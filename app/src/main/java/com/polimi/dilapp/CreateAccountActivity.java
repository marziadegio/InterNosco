package com.polimi.dilapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class CreateAccountActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        // Get the Intent that started this activity
        Intent intent = getIntent();

        //dummy link to ActivityAlfa for debugging purpose
        Intent dummyIntent = new Intent(getApplicationContext(), ActivityAlfa.class);
        startActivity(dummyIntent);
    }
}