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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //views
        Button startRecordButton = (Button) findViewById(R.id.startRecordButton);
        Button stopRecordButton = (Button) findViewById(R.id.stopRecordButton);

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
    }


    private void ClickStartRecordButton() {
        //TODO: add a toast
        //get file name
        EditText fileNameView = (EditText) findViewById(R.id.fileName);
        if (TextUtils.isEmpty(fileNameView.getText())) {
            fileNameView.setError("File name is required!");
            return;
        }
        String fileName = fileNameView.getText().toString();

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
        String filePath = baseDir + File.separator + fileName;

        //get time
        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        //write to csv
        File f = new File(filePath);
        CSVWriter writer;
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
        String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IOT_out_files" + File.separator + fileName + ".csv");
        try {
            writer = new CSVWriter(new FileWriter(csv));

            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[]{"NAME:", fileName + ".csv"});
            data.add(new String[]{"EXPERIMENT TIME:", currentTime});
            data.add(new String[]{"ACTIVITY TYPE:", activity});
            data.add(new String[]{});
            data.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});
            //TODO: add everything
//                data.add(new String[]{"Germany", "Berlin"});

            writer.writeAll(data); // data is adding to csv

            writer.close();
//            callRead();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button stopRecordButton = (Button) findViewById(R.id.stopRecordButton);
        stopRecordButton.setClickable(true);
        Button startRecordButton = (Button) findViewById(R.id.startRecordButton);
        startRecordButton.setClickable(false);
    }

    private void ClickStopRecordButton(String filePath, List<String[]> data) {
        //TODO: add a toast
//        mFileWriter = new FileWriter(filePath, true);
//        writer = new CSVWriter(mFileWriter);

        //write to csv
        CSVWriter writer;
        //check if directory exists
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IOT_out_files");
        if (!dir.isDirectory()) {
            dir.mkdir();
        } else {
            String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "IOT_out_files" + File.separator + fileName + ".csv");
            try {
                writer = new CSVWriter(new FileWriter(csv));

                data.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});

                writer.writeAll(data); // data is adding to csv

                writer.close();
//            callRead();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //set buttons mode
        Button stopRecordButton = (Button) findViewById(R.id.stopRecordButton);
        stopRecordButton.setClickable(false);
        Button startRecordButton = (Button) findViewById(R.id.startRecordButton);
        startRecordButton.setClickable(true);
    }
}