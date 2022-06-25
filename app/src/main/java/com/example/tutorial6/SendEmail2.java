package com.example.tutorial6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
//                String email = emailView.getTextAVG().toString();
//                String phone = phoneView.getTextAVG().toString();
//                String password = passwordView.getTextAVG().toString();
                System.out.println();
                openResults();
            }
        });
    }

    public void openResults() {
        Intent intent = new Intent(this, Results.class);
        TextView editTextName = (TextView) findViewById(R.id.editTextName);
        TextView editTextWeight = (TextView) findViewById(R.id.editTextWeight);
        TextView editTextAge = (TextView) findViewById(R.id.editTextAge);
        TextView editTextHeight = (TextView) findViewById(R.id.editTextHeight);

        if (editTextName.getText().toString().trim().equals("")) {
            toast("Please enter name");
            editTextName.setError("Please enter name");
            return;
        }
        if (editTextAge.getText().toString().trim().equals("")) {
            toast("Please enter age");
            editTextAge.setError("Please enter age");
            return;
        }
        if (editTextWeight.getText().toString().trim().equals("")) {
            toast("Please enter weight");
            editTextWeight.setError("Please enter weight");
            return;
        }

        if (editTextHeight.getText().toString().trim().equals("")) {
            toast("Please enter height");
            editTextHeight.setError("Please enter height");
            return;
        }

        String name = editTextName.getText().toString();
        String weight = editTextWeight.getText().toString();
        String age = editTextAge.getText().toString();
        String height = editTextHeight.getText().toString();

        intent.putExtra("name", name);
        intent.putExtra("weight", weight);
        intent.putExtra("age", age);
        intent.putExtra("height", height);
        startActivity(intent);
    }

    public void toast(CharSequence text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}