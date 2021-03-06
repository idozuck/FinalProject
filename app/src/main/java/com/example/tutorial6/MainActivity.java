package com.example.tutorial6;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    // Record Activity
    private volatile boolean running = false;
    private volatile boolean firstRunning = false;
    private final int OldRange = (250000 - 200000);
    private final int NewRange = 3;
    private final int historyLength = 50;
    private final String acc_data_path = "/storage/self/primary/IoT/data.csv";
    private final String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private final String dirPath = baseDir + File.separator + "IOT_out_files";
    private final String filePath = dirPath + File.separator + "record_file.csv";

    private Runnable t;
    private Handler hand;
    private TextView HR_text, SPO2_text, BP_text;
    private double newval_ppg;
    private String heart_rate, spo2, ppg, accX, accY, accZ;

    private int history = 0;
    private Double[] heart_rate_list = new Double[historyLength];
    private Double[] spo2_list = new Double[historyLength];
    private Double[] ppg_list = new Double[historyLength];

    private List<String[]> data = new ArrayList<String[]>();

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
        //check if directory exists
        File dir = new File(dirPath);
        if (!dir.isDirectory())
            dir.mkdir();
        File f = new File(filePath);
        if (f.exists())
            f.delete();

        //initialzie arrays
        for (int i = 0; i < historyLength; i++) {
            heart_rate_list[i] = -999.0;
            spo2_list[i] = -999.0;
            ppg_list[i] = -999.0;
        }

        //Buttons
        Button startButton = (Button) findViewById(R.id.startButton);
        Button stopButton = (Button) findViewById(R.id.stopButton);
        Button startRecordButton = (Button) findViewById(R.id.startRecordButton);
        Button createReportButton = (Button) findViewById(R.id.createReportButton);

        LinearLayout layout = (LinearLayout) findViewById(R.id.recordLayout);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!running) {
                    toast("You have to start first");
                    return;
                }
                running = false;
                toast("Stopped");
            }
        });


        HR_text = (TextView) findViewById(R.id.HR);
        SPO2_text = (TextView) findViewById(R.id.SPO2);
        BP_text = (TextView) findViewById(R.id.BP);


        //buttons clicked
        createReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!firstRunning) {
                    toast("Didn't started yet");
                    return;
                }
                running = false;
                //save
                CSVWriter writer;
                try {
                    writer = new CSVWriter(new FileWriter(filePath));
                    writer.writeAll(data);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                openSendEmailActivity();
            }
        });

        startRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    toast("Already running");
                    return;
                }
                running = true;
                firstRunning = true;

                hand = new Handler();
                toast("Started");
                t = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            history++;
                            history = history % historyLength;
                            BufferedReader buff_read = new BufferedReader(new FileReader(acc_data_path));
                            String text_row = buff_read.readLine();
                            String new_text_row = text_row.replace('"', '\0');
                            String[] statsArray = new_text_row.split(",");
//                            time_str = statsArray[0];
                            heart_rate = statsArray[1];
//                            valid_heart_rate = statsArray[2];
                            spo2 = statsArray[3];
//                            valid_spo2 = statsArray[4];
                            ppg = statsArray[5];
                            accX = statsArray[6];
                            accY = statsArray[7];
                            accZ = statsArray[8];
                            //add to lists
                            heart_rate_list[history] = addNumber(heart_rate);
                            spo2_list[history] = addNumber(spo2);

                            System.out.println("heart_rate" + heart_rate + " spo2" + spo2 + " ppg" + ppg + " accX" + accX + " accY" + accY + " accZ" + accZ);
                            float ppg_fl = Float.parseFloat(ppg);
                            HR_text.setText("HR: " + setText(heart_rate_list));
                            SPO2_text.setText("SPO2: " + setText(spo2_list));
                            if (ppg_fl > 200000) {
                                newval_ppg = (((ppg_fl - 200000) * NewRange) / OldRange) + 0.5;
                                PyObject obj = pyobj.callAttr("calc_BP", newval_ppg);
                                ppg_list[history] = addNumber(obj.toString());
                                BP_text.setText("Arterial Blood Pressure: " + setText(ppg_list));
                            } else {
                                BP_text.setText("Arterial Blood Pressure: " + "ERROR");
                            }
                            //Record
                            data.add(statsArray);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (running) {
                            hand.postDelayed(this, 100);
                        }
                    }
                };
                hand.postDelayed(t, 100);
            }
        });

    }

    public String setText(Double[] list) {
        Double sum = 0.0;
        int count = 0;
        for (Double num : list) {
            if (num > 0) {
                sum += num;
                count += 1;
            }
        }
        if (count > 0)
            return String.valueOf(Math.round(sum / count));
        else
            return "ERROR";
    }

    public double addNumber(String number) {
        double num = Double.parseDouble(number);
        if (num < 0)
            return -999.0;
        else
            return num;
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

    public void toast(CharSequence text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void openSendEmailActivity() {
        Intent intent = new Intent(this, SendEmail2.class);
        startActivity(intent);
    }
}
