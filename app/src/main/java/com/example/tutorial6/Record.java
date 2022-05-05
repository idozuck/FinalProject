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
                ClickStartRecordButton();
            }
        });

        stopRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


    private void ClickStartRecordButton() {
        //check if already started
        if (collectingData) {
            Context context = getApplicationContext();
            CharSequence text = "you already started";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }

        //get file name
        EditText fileNameView = (EditText) findViewById(R.id.fileName);
        if (TextUtils.isEmpty(fileNameView.getText())) {
            fileNameView.setError("File name is required!");
            return;
        }
        this.fileName = fileNameView.getText().toString();

        //get number of steps
        EditText numberOfStepsView = (EditText) findViewById(R.id.numberOfSteps);
        if (TextUtils.isEmpty(numberOfStepsView.getText())) {
            numberOfStepsView.setError("Number of steps is required!");
            return;
        }
        String numberOfSteps = fileNameView.getText().toString();

        //get run
        RadioButton walkButton = (RadioButton) findViewById(R.id.radioButtonWalk);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        if (radioButtonID == -1) {
            // no radio buttons are checked
            walkButton.setError("Type of activity is required!");
            return;
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
            return;
        }

        //set data
        this.data.add(new String[]{"NAME:", this.fileName + ".csv"});
        this.data.add(new String[]{"EXPERIMENT TIME:", currentTime});
        this.data.add(new String[]{"ACTIVITY TYPE:", activity});
        this.data.add(new String[]{});
        this.data.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});

        collectingData = true;

        //show starting message
        Context context = getApplicationContext();
        CharSequence text = "Starting...";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

//        final Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int x = 1;
//                while (collectingData) {
//                    //TODO: Gil, Alon, add here the measurements from the IMU sensor in the following format:
//                    // this.data.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});
//                    // you may use a for loop containing the above line the add the measurments to the csv
//                    // you should use this line for each measure, I took care in all the rest, and it will
//                    // automatically do the csv in the wanted format.
//                    // add it here.
//                    this.data.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});
//                    for (int i = 0; i < 20; i++) {
//                        x = x * 2;
//                        System.out.println(x);
//                    }
//                    System.out.println(x);
//                }
//            }
//        });
//        t.start();
        collectData();
    }

    private void collectData() {
        int x = 1;
        Button stopRecordButton = (Button) findViewById(R.id.stopRecordButton);

        stopRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickStopRecordButton();
            }
        });

        while (collectingData) {
            //TODO: Gil, Alon, add here the measurements from the IMU sensor in the following format:
            // data.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});
            // you may use a for loop containing the above line the add the measurments to the csv
            // you should use this line for each measure, I took care in all the rest, and it will
            // automatically do the csv in the wanted format.
            // add it here.
            data.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});
            for (int i = 0; i < 10; i++) {
                x = x * 2;
                System.out.println(x);
            }
            System.out.println(x);

        }
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


        //show starting message
        Context context = getApplicationContext();
        CharSequence text = "Stopping...";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    private void ClickSaveRecordButton() {
        if (!collectingData) {
            Context context = getApplicationContext();
            CharSequence text = "you didn't started yet";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        if (!doneCollectingData) {
            Context context = getApplicationContext();
            CharSequence text = "you didn't stop yet";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }

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

        ClickResetRecordButton();
        //show saving message
        Context context = getApplicationContext();
        CharSequence text = "Saving...";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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