package com.example.tutorial6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Results extends AppCompatActivity {
    private final String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private final String dirPath = baseDir + File.separator + "IOT_out_files";
    private final String filePath = dirPath + File.separator + "record_file.csv";

    private String row;
    private String[] splitedRow;

    private final int OldRange = (250000 - 200000);
    private final int NewRange = 3;

    private List<Double> heart_rate = new ArrayList<Double>();
    private List<Double> valid_heart_rate = new ArrayList<Double>();
    private List<Double> spo2 = new ArrayList<Double>();
    private List<Double> valid_spo2 = new ArrayList<Double>();
    private List<Double> ppg = new ArrayList<Double>();
    private List<Double> accX = new ArrayList<Double>();
    private List<Double> accY = new ArrayList<Double>();
    private List<Double> accZ = new ArrayList<Double>();

    private Python py;
    private PyObject pyobj, obj;

    private String name;
    private float weight, age,height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        weight = Float.parseFloat(intent.getStringExtra("weight"));
        age = Float.parseFloat(intent.getStringExtra("age"));
        height = Float.parseFloat(intent.getStringExtra("height"));

        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        py = Python.getInstance();
        pyobj = py.getModule("test");
        try {
            Scanner sc = new Scanner(new File(filePath));
            sc.useDelimiter("\n");
            while (sc.hasNext()) {
                row = sc.next();
                row = row.replace('"', '\0');
                splitedRow = row.split(",");

                heart_rate.add(Double.parseDouble(splitedRow[1]));
                valid_heart_rate.add(Double.parseDouble(splitedRow[2]));
                spo2.add(Double.parseDouble(splitedRow[3]));
                valid_spo2.add(Double.parseDouble(splitedRow[4]));
                ppg.add(Double.parseDouble(splitedRow[5]));
                accX.add(Double.parseDouble(splitedRow[6]));
                accY.add(Double.parseDouble(splitedRow[7]));
                accZ.add(Double.parseDouble(splitedRow[8]));
//                System.out.print(sc.next());
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        TextView textViewHR = (TextView) findViewById(R.id.avgHeartRate);
        TextView textViewSPO2 = (TextView) findViewById(R.id.avgSPo2);
        TextView textViewBP = (TextView) findViewById(R.id.avgBloodPressure);
        TextView textViewSBP = (TextView) findViewById(R.id.SystolicPressure);
        TextView textViewDBP = (TextView) findViewById(R.id.DiastolicPressure);
        TextView textViewSummery = (TextView) findViewById(R.id.Summery);
        TextView textViewBMI = (TextView) findViewById(R.id.BMI);
        textViewHR.setText("Average Heart Rate: " + getTextAVG(heart_rate, false));
        textViewSPO2.setText("Average SPo2: " + getTextAVG(spo2, false));
        textViewBP.setText("Average Arterial Blood Pressure: " + getTextAVG(ppg, true));
        textViewSBP.setText("Systolic Pressure: " + getTextMax(ppg, true));
        textViewDBP.setText("Diastolic Pressure: " + getTextMin(ppg, true));
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        float BMI = calculateBMI();
        textViewBMI.setText("BMI: " + String.valueOf(Math.round(BMI)));
        textViewSummery.setText("Summery:\nYou are " + BMICategory(BMI) + "\n" + BloodPressureCategory(ppg));

        int stars = HealthScore(BMI, ppg);
        ratingBar.setRating(stars);
//        ratingBar.setClickable(false);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRecord();
                File f = new File(filePath);
                if (f.exists()) {
                    f.delete();
                }
            }
        });
    }


    private int HealthScore(float bmi, List<? extends Number> listBP) {
        int healthScore = 5;
        if (bmi < 18.5)
            healthScore--;
        else if (bmi < 25)
            healthScore += 0;
        else if (bmi < 30)
            healthScore--;
        else
            healthScore -= 2;

        double sbp = max(listBP, true);
        double dbp = min(listBP, true);
        boolean sbpFlag = false;
        boolean dbpFlag = false;
        if (age < 2) {
            if (sbp >= 80 && sbp <= 100)
                sbpFlag = true;
            if (dbp >= 40 && dbp <= 70)
                dbpFlag = true;
        } else if (age < 13) {
            if (sbp >= 80 && sbp <= 120)
                sbpFlag = true;
            if (dbp >= 40 && dbp <= 80)
                dbpFlag = true;
        } else if (age < 18) {
            if (sbp >= 90 && sbp <= 120)
                sbpFlag = true;
            if (dbp >= 50 && dbp <= 80)
                dbpFlag = true;
        } else if (age < 40) {
            if (sbp >= 95 && sbp <= 135)
                sbpFlag = true;
            if (dbp >= 60 && dbp <= 80)
                dbpFlag = true;
        } else if (age < 60) {
            if (sbp >= 110 && sbp <= 145)
                sbpFlag = true;
            if (dbp >= 70 && dbp <= 90)
                dbpFlag = true;
        } else {
            if (sbp >= 95 && sbp <= 145)
                sbpFlag = true;
            if (dbp >= 70 && dbp <= 90)
                dbpFlag = true;
        }
        if (!sbpFlag)
            healthScore--;
        if (!dbpFlag)
            healthScore--;
        return healthScore;
    }

    private float calculateBMI() {
        float bmi = (100 * 100 * weight) / (height * height);
        return bmi;

    }

    private String BMICategory(float bmi) {
        if (bmi < 18.5)
            return "underweight";
        if (bmi < 25)
            return "normal weight";
        if (bmi < 30)
            return "overweight";
        return "obese";
    }

    private String BloodPressureCategory(List<? extends Number> list) {
        double sbp = max(list, true);
        double dbp = min(list, true);
        boolean sbpFlag = false;
        boolean dbpFlag = false;
        if (!(dbp > 0 && sbp > 0))
            return "";
        if (age < 2) {
            if (sbp >= 80 && sbp <= 100)
                sbpFlag = true;
            if (dbp >= 40 && dbp <= 70)
                dbpFlag = true;
        } else if (age < 13) {
            if (sbp >= 80 && sbp <= 120)
                sbpFlag = true;
            if (dbp >= 40 && dbp <= 80)
                dbpFlag = true;
        } else if (age < 18) {
            if (sbp >= 90 && sbp <= 120)
                sbpFlag = true;
            if (dbp >= 50 && dbp <= 80)
                dbpFlag = true;
        } else if (age < 40) {
            if (sbp >= 95 && sbp <= 135)
                sbpFlag = true;
            if (dbp >= 60 && dbp <= 80)
                dbpFlag = true;
        } else if (age < 60) {
            if (sbp >= 110 && sbp <= 145)
                sbpFlag = true;
            if (dbp >= 70 && dbp <= 90)
                dbpFlag = true;
        } else {
            if (sbp >= 95 && sbp <= 145)
                sbpFlag = true;
            if (dbp >= 70 && dbp <= 90)
                dbpFlag = true;
        }

        String msg;
        if (sbpFlag)
            msg = "Your systolic pressure is good";
        else
            msg = "Your systolic pressure is not good";
        if (dbpFlag)
            msg += "\nYour diastolic pressure is good";
        else
            msg += "\nYour diastolic pressure is not good";
        return msg;
    }

    public String getTextMax(List<? extends Number> list, boolean bpFlag) {
        double max = max(list, bpFlag);
        if (max > 0)
            return String.valueOf(Math.round(max));
        else
            return "Not enough data";
    }

    public String getTextMin(List<? extends Number> list, boolean bpFlag) {
        double min = min(list, bpFlag);
        if (min > 0)
            return String.valueOf(Math.round(min));
        else
            return "Not enough data";
    }

    public String getTextAVG(List<? extends Number> list, boolean bpFlag) {
        double avg = avg(list, bpFlag);
        if (avg > 0)
            return String.valueOf(Math.round(avg));
        else
            return "Not enough data";
    }

    public double max(List<? extends Number> list, boolean bpFlag) {
        double max = 0.0;
        double val;
        boolean found = false;
        for (Number i : list) {
            if (bpFlag) {
                val = (((i.doubleValue() - 200000) * NewRange) / OldRange) + 0.5;
                obj = pyobj.callAttr("calc_BP", val);
                val = Double.parseDouble(obj.toString());
            } else
                val = i.doubleValue();
            if (val > 0.0) {
                if (val > max) {
                    max = val;
                    found = true;
                }
            }
        }
        if (found)
            return max;
        return 0.0;
    }

    public double min(List<? extends Number> list, boolean bpFlag) {
        double min = 300.0;
        double val;
        boolean found = false;
        for (Number i : list) {
            if (bpFlag) {
                val = (((i.doubleValue() - 200000) * NewRange) / OldRange) + 0.5;
                obj = pyobj.callAttr("calc_BP", val);
                val = Double.parseDouble(obj.toString());
            } else
                val = i.doubleValue();
            if (val > 0.0) {
                if (val < min) {
                    min = val;
                    found = true;
                }
            }
        }
        if (found)
            return min;
        return 0.0;
    }


    public double avg(List<? extends Number> list, boolean bpFlag) {
        double sum = 0.0;
        int counter = 0;
        double val;
        for (Number i : list) {
            if (i.doubleValue() > 0.0) {
                if (bpFlag) {
                    val = (((i.doubleValue() - 200000) * NewRange) / OldRange) + 0.5;
                    obj = pyobj.callAttr("calc_BP", val);
                    val = Double.parseDouble(obj.toString());
                } else
                    val = i.doubleValue();
                sum += val;
                counter += 1;
            }
        }
        if (counter > 0)
            return sum / counter;
        return 0.0;
    }


    public void openRecord() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}