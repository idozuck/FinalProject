package com.example.tutorial6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

//import com.example.tutorial6.EmailPasswordActivity;

public class SendEmail2 extends AppCompatActivity {
//    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email2);

        Button sendEmail = (Button) findViewById(R.id.sendButton);

        EditText nameView = (EditText) findViewById(R.id.editTextName);
        EditText ageView = (EditText) findViewById(R.id.editTextAge);
        EditText weightView = (EditText) findViewById(R.id.editTextWeight);
        EditText heightView = (EditText) findViewById(R.id.editTextHeight);
        EditText emailView = (EditText) findViewById(R.id.editTextEmail);
        EditText phoneView = (EditText) findViewById(R.id.editTextPhone);
        EditText passwordView = (EditText) findViewById(R.id.editTextPassword);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameView.getText().toString();
                String age = ageView.getText().toString();
                String weight = weightView.getText().toString();
                String height = heightView.getText().toString();
                String email = emailView.getText().toString();
                String phone = phoneView.getText().toString();
                String password = passwordView.getText().toString();
//                EmailPasswordActivity emailpassword= new EmailPasswordActivity();
//                emailpassword.createAccount(email, password);
//                createAccount(email, password);
                System.out.println();

            }

//            public void updateUI(FirebaseUser user) {
//
//            }
        });


    }

}