package com.example.tutorial6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendEmail2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email2);

        Button sendEmail = (Button) findViewById(R.id.sendButton);

        EditText nameView = (EditText) findViewById(R.id.editTextName);
        EditText ageView = (EditText) findViewById(R.id.editTextAge);
        EditText weightView = (EditText) findViewById(R.id.editTextWeight);
        EditText heightView = (EditText) findViewById(R.id.editTextHeight);
//        EditText emailView = (EditText) findViewById(R.id.editTextEmail);
//        EditText phoneView = (EditText) findViewById(R.id.editTextPhone);
//        EditText passwordView = (EditText) findViewById(R.id.editTextPassword);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameView.getText().toString();
                String age = ageView.getText().toString();
                String weight = weightView.getText().toString();
                String height = heightView.getText().toString();
//                String email = emailView.getText().toString();
//                String phone = phoneView.getText().toString();
//                String password = passwordView.getText().toString();
                System.out.println();
                openResults();
            }
        });
    }

    public void openResults() {
        Intent intent = new Intent(this, Results.class);
        startActivity(intent);
    }
}