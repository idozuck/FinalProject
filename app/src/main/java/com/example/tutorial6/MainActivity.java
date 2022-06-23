package com.example.tutorial6;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.lang.*;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    // Record Activity
    private volatile boolean running = false;
    private Runnable t;
    private Handler hand;
    private TextView HR_text;
    private TextView SPO2_text;
    private TextView BP_text;
    private int OldRange = (250000 - 200000);
    private int NewRange = 3;
    private double newval_ppg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DevicesFragment(), "devices").commit();
        } else {
            onBackStackChanged();
        }

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        Python py = Python.getInstance();
        PyObject pyobj = py.getModule("test");


        //Record Activity
        //        views
        Button startButton = (Button) findViewById(R.id.startButton);
        LinearLayout layout = (LinearLayout) findViewById(R.id.recordLayout);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                start();
                layout.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
            }
        });
        Button createReportButton = (Button) findViewById(R.id.createReportButton);
        Button startRecordButton = (Button) findViewById(R.id.startRecordButton);

        HR_text = (TextView) findViewById(R.id.HR);
        SPO2_text = (TextView) findViewById(R.id.SPO2);
        BP_text = (TextView) findViewById(R.id.BP);


        //buttons clicked
        createReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
                openSendEmailActivity();
            }
        });

        startRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = true;
                hand = new Handler();
                t = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String acc_data_path = "/storage/self/primary/IoT/data.csv";
                            BufferedReader buff_read = new BufferedReader(new FileReader(acc_data_path));
                            String text_row = buff_read.readLine();
                            String new_text_row = text_row.replace('"', '\0');
                            String[] statsArray = new_text_row.split(",");
                            String time_str = statsArray[0];
                            String heart_rate = statsArray[1];
                            String valid_heart_rate = statsArray[2];
                            String spo2 = statsArray[3];
                            String valid_spo2 = statsArray[4];
                            String ppg = statsArray[5];
                            String accX = statsArray[6];
                            String accY = statsArray[7];
                            String accZ = statsArray[8];
                            System.out.println("heart_rate" + heart_rate + " spo2" + spo2 + " ppg" + ppg + " accX" + accX + " accY" + accY + " accZ" + accZ);
                            float ppg_fl = Float.parseFloat(ppg);
                            if (!heart_rate.equals("\u0000-999\u0000")) {
                                HR_text.setText("HR: " + heart_rate);
                            } else {
                                HR_text.setText("HR: " + "ERROR");
                            }
                            if (!spo2.equals("\u0000-999\u0000")) {
                                SPO2_text.setText("SPO2: " + spo2);
                            } else {
                                SPO2_text.setText("SPO2: " + "ERROR");
                            }
                            if (ppg_fl > 200000) {

                                newval_ppg = (((ppg_fl - 200000) * NewRange) / OldRange) + 0.5;
                                PyObject obj = pyobj.callAttr("calc_BP", newval_ppg);
                                BP_text.setText("Arterial Blood Pressure: " + obj.toString());
                            } else {
                                BP_text.setText("Blood Pressure: " + "ERROR");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Running");
                        if (running) {
                            hand.postDelayed(this, 100);
                        }
                    }
                };
                hand.postDelayed(t, 100);
            }
        });

    }

    public void start() {
        Intent intent = new Intent(this, Record.class);
        startActivity(intent);

    }


    @Override
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //Record Activity
//    private Boolean ClickStartRecordButton() {
//
//        //check if already started
//        if (collectingData) {
//            Context context = getApplicationContext();
//            CharSequence text = "you already started";
//            int duration = Toast.LENGTH_SHORT;
//
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
//            return false;
//        }
//
//        //get file name
//        EditText fileNameView = (EditText) findViewById(R.id.fileName);
//        if (TextUtils.isEmpty(fileNameView.getText())) {
//            fileNameView.setError("File name is required!");
//            return false;
//        }
//        this.fileName = fileNameView.getText().toString();
//
//        //get number of steps
//        EditText numberOfStepsView = (EditText) findViewById(R.id.numberOfSteps);
//        if (TextUtils.isEmpty(numberOfStepsView.getText())) {
//            numberOfStepsView.setError("Number of steps is required!");
//            return false;
//        }
//        String numberOfSteps = numberOfStepsView.getText().toString();
//
//        //get run
//        RadioButton walkButton = (RadioButton) findViewById(R.id.radioButtonWalk);
//        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
//        int radioButtonID = radioGroup.getCheckedRadioButtonId();
//        if (radioButtonID == -1) {
//            // no radio buttons are checked
//            walkButton.setError("Type of activity is required!");
//            return false;
//        }
//        RadioButton radioButton = radioGroup.findViewById(radioButtonID);
//        String activity = (String) radioButton.getText();
//
//        //set path
//        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
//        String filePath = baseDir + File.separator + "IOT_out_files" + File.separator + this.fileName + ".csv";
//
//        //get time
//        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
//
//        //write to csv
//        File f = new File(filePath);
//        //check if directory exists
//        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IOT_out_files");
//        if (!dir.isDirectory()) {
//            dir.mkdir();
//        }
//
//        if (f.exists()) {
//            Context context = getApplicationContext();
//            CharSequence text = "File name already exists";
//            int duration = Toast.LENGTH_SHORT;
//
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
//            return false;
//        }
//
//        //set data
//        this.data_record.add(new String[]{"NAME:", this.fileName + ".csv"});
//        this.data_record.add(new String[]{"EXPERIMENT TIME:", currentTime});
//        this.data_record.add(new String[]{"ACTIVITY TYPE:", activity});
//        this.data_record.add(new String[]{"COUNT OF ACTUAL STEPS", numberOfSteps});
//        this.data_record.add(new String[]{"ESTIMATED NUMBER OF STEPS", String.valueOf(step_count)});
//
//
//        this.data_record.add(new String[]{});
//        this.data_record.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});
//
//        collectingData = true;
//
//        //show starting message
//        Context context = getApplicationContext();
//        CharSequence text = "Starting...";
//        int duration = Toast.LENGTH_SHORT;
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//        return true;
//    }
//
//    private void ClickStopRecordButton() {
//        //check if didn't started yet
//        if (!collectingData) {
//            Context context = getApplicationContext();
//            CharSequence text = "you didn't started yet";
//            int duration = Toast.LENGTH_SHORT;
//
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
//            return;
//        }
//        collectingData = false;
//        doneCollectingData = true;
//
//        //show starting message
//        Context context = getApplicationContext();
//        CharSequence text = "Stopping...";
//        int duration = Toast.LENGTH_SHORT;
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//    }
//
//    private void ClickSaveRecordButton() {
//        if (!doneCollectingData) {
//            Context context = getApplicationContext();
//            CharSequence text = "you didn't stop yet";
//            int duration = Toast.LENGTH_SHORT;
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
//            return;
//        }
//        doneCollectingData = false;
//
//        //write to csv
//        CSVWriter writer;
//        String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IOT_out_files" + File.separator + this.fileName + ".csv");
//        try {
//            writer = new CSVWriter(new FileWriter(csv));
//            this.data_record.set(4, new String[]{"ESTIMATED NUMBER OF STEPS", String.valueOf(step_count)});
//            writer.writeAll(this.data_record); // data is adding to csv
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //show saving message
//        Context context = getApplicationContext();
//        CharSequence text = "Saving...";
//        int duration = Toast.LENGTH_SHORT;
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//        ClickResetRecordButton();
//    }
//
//    private void ClickResetRecordButton() {
//        data_record = new ArrayList<String[]>();
//        N_list = new ArrayList<Double>();
//        collectingData = false;
//        doneCollectingData = false;
//        step_count = 0;
//
//        //show resetting message
//        Context context = getApplicationContext();
//        CharSequence text = "Resetting...";
//        int duration = Toast.LENGTH_SHORT;
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//    }

    public void openSendEmailActivity() {
        Intent intent = new Intent(this, SendEmail2.class);
        startActivity(intent);
    }
}
