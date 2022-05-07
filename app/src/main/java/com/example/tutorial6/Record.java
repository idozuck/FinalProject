package com.example.tutorial6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Record extends AppCompatActivity {
    private List<String[]> data = new ArrayList<String[]>();
    private String fileName;
    private Boolean collectingData = false;
    private Boolean doneCollectingData = false;
    private volatile boolean running = true;
    private Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);


        //views
        Button startRecordButton = (Button) findViewById(R.id.startRecordButton);
        Button stopRecordButton = (Button) findViewById(R.id.stopRecordButton);
        Button resetRecordButton = (Button) findViewById(R.id.resetRecordButton);
        Button saveRecordButton = (Button) findViewById(R.id.saveRecordButton);

        EditText numberOfStepsView = (EditText) findViewById(R.id.numberOfSteps);
        EditText fileNameView = (EditText) findViewById(R.id.fileName);

        RadioButton radioButtonRun = (RadioButton) findViewById(R.id.radioButtonRun);
        RadioButton radioButtonWalk = (RadioButton) findViewById(R.id.radioButtonWalk);


        //buttons clicked
        startRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean succeeded = ClickStartRecordButton();
                if (succeeded) {
                    running = true;
                    t = new Thread(new Runnable() {
                        public void run() {
                            while (running) {
                                //TODO: Gil, Alon, add here the measurements from the IMU sensor in the following format:
                                // data.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});
                                // you may use a for loop containing the above line the add the measurments to the csv
                                // you should use this line for each measure, I took care in all the rest, and it will
                                // automatically do the csv in the wanted format.
                                // add it here.

//                                data.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});

                                System.out.println("Running");
                                try {
                                    Thread.sleep(30);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    t.start();
                }

            }
        });

        stopRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;

                ClickStopRecordButton();

            }
        });

        resetRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickResetRecordButton();
            }
        });

        saveRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickSaveRecordButton();
            }
        });
    }


    private Boolean ClickStartRecordButton() {
        //check if already started
        if (collectingData) {
            Context context = getApplicationContext();
            CharSequence text = "you already started";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }

        //get file name
        EditText fileNameView = (EditText) findViewById(R.id.fileName);
        if (TextUtils.isEmpty(fileNameView.getText())) {
            fileNameView.setError("File name is required!");
            return false;
        }
        this.fileName = fileNameView.getText().toString();

        //get number of steps
        EditText numberOfStepsView = (EditText) findViewById(R.id.numberOfSteps);
        if (TextUtils.isEmpty(numberOfStepsView.getText())) {
            numberOfStepsView.setError("Number of steps is required!");
            return false;
        }
        String numberOfSteps = fileNameView.getText().toString();

        //get run
        RadioButton walkButton = (RadioButton) findViewById(R.id.radioButtonWalk);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        if (radioButtonID == -1) {
            // no radio buttons are checked
            walkButton.setError("Type of activity is required!");
            return false;
        }
        RadioButton radioButton = radioGroup.findViewById(radioButtonID);
        String activity = (String) radioButton.getText();

        //set path
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = baseDir + File.separator + "IOT_out_files" + File.separator + this.fileName + ".csv";

        //get time
        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        //write to csv
        File f = new File(filePath);
        //check if directory exists
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IOT_out_files");
        if (!dir.isDirectory()) {
            dir.mkdir();
        }

        if (f.exists()) {
            Context context = getApplicationContext();
            CharSequence text = "File name already exists";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }

        //set data
        this.data.add(new String[]{"NAME:", this.fileName + ".csv"});
        this.data.add(new String[]{"EXPERIMENT TIME:", currentTime});
        this.data.add(new String[]{"ACTIVITY TYPE:", activity});
        this.data.add(new String[]{"COUNT OF ACTUAL STEPS", numberOfSteps});

        this.data.add(new String[]{});
        this.data.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});

        collectingData = true;

        //show starting message
        Context context = getApplicationContext();
        CharSequence text = "Starting...";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return true;
    }

    private void ClickStopRecordButton() {
        //check if didn't started yet
        if (!collectingData) {
            Context context = getApplicationContext();
            CharSequence text = "you didn't started yet";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        collectingData = false;
        doneCollectingData = true;

        //show starting message
        Context context = getApplicationContext();
        CharSequence text = "Stopping...";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void ClickSaveRecordButton() {
//        if (!collectingData) {
//            Context context = getApplicationContext();
//            CharSequence text = "you didn't started yet";
//            int duration = Toast.LENGTH_SHORT;
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
//            return;
//        }
        if (!doneCollectingData) {
            Context context = getApplicationContext();
            CharSequence text = "you didn't stop yet";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        doneCollectingData = false;

        //write to csv
        CSVWriter writer;
        String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IOT_out_files" + File.separator + this.fileName + ".csv");
        try {
            writer = new CSVWriter(new FileWriter(csv));
            writer.writeAll(this.data); // data is adding to csv
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //show saving message
        Context context = getApplicationContext();
        CharSequence text = "Saving...";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        ClickResetRecordButton();
    }

    private void ClickResetRecordButton() {
        data = new ArrayList<String[]>();
        collectingData = false;
        doneCollectingData = false;

        //show resetting message
        Context context = getApplicationContext();
        CharSequence text = "Resetting...";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}