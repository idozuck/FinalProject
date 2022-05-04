package com.example.tutorial6;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class Record extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Button mButton;
        EditText numberOfSteps, fileName;
        RadioButton walk, run;

        mButton = (Button) findViewById(R.id.button);
        numberOfSteps = (EditText) findViewById(R.id.numberOfSteps);
        fileName = (EditText) findViewById(R.id.fileName);


    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButtonWalk:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radioButtonRun:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }