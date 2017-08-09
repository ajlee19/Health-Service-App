package  com.samsung.rpp_demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.samsung.sds.mhs.android.BPAlgorithm;
import com.samsung.sds.mhs.android.BpEventListener;
import samsung.rpp_demo.R;

public class Calibration extends AppCompatActivity implements BpEventListener {

    private SensorManager sensorManager;
    private Sensor hrm;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private BPAlgorithm bpAlgorithm;
    private double mFeat1, mFeat2, mFeat3, mFeat4;
    private int mSbpOffset, mDbpOffset;

    private Button calButton, skipBtm;
    private EditText sbpRef, dbpRef, nameRef;
    private ProgressBar spinner;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sbpRef = (EditText) findViewById(R.id.sbp_input);
        dbpRef = (EditText) findViewById(R.id.dbp_input);
        nameRef = (EditText) findViewById(R.id.name_input);
        calButton = (Button) findViewById(R.id.cal_button);
        skipBtm =(Button) findViewById(R.id.skpcalbtn);
        skipBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipToMain();
            }
        });

        prefs = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        prefEdit = prefs.edit();

        // Checking permission
        if (checkPermission()){
            Log.e("permission", "Permission already granted");
        }else{
            requestPermission();
        }

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        hrm = sensorManager.getDefaultSensor(65584);

        bpAlgorithm = new BPAlgorithm(this);
        bpAlgorithm.setBpChangedListener(this);

        spinner = (ProgressBar)findViewById(R.id.progressBarCal);
        spinner.setVisibility(View.GONE);
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case 65584:
                    bpAlgorithm.pushData((int)event.values[2]);
                    break;
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    public void calibrate(View view){
        if (calButton.getText().toString().equals("Done")){
            continueToMain();
        } else {
            spinner.setVisibility(View.VISIBLE);
            calButton.setText("Calibrating");
            bpAlgorithm.resetFeature();
            bpAlgorithm.resetReq();
            turnOnSensor();
        }
    }

    public void continueToMain(){
        prefEdit.putString("Name", nameRef.getText().toString());
        prefEdit.putInt("SBPOffset", mDbpOffset);
        prefEdit.putString("feat1", String.valueOf(mFeat1));
        prefEdit.putString("feat2", String.valueOf(mFeat2));
        prefEdit.putString("feat3", String.valueOf(mFeat3));
        prefEdit.putString("feat4", String.valueOf(mFeat4));
        prefEdit.commit();

        Toast.makeText(Calibration.this, "Offset: "+mSbpOffset+"/"+mDbpOffset, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void skipToMain(){
        turnOffSensor();
        prefEdit.putString("Name", "User");
        prefEdit.putInt("SBPOffset", 0);
        prefEdit.putString("feat1", "0");
        prefEdit.putString("feat2","0");
        prefEdit.putString("feat3", "0");
        prefEdit.putString("feat4", "0");
        prefEdit.commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBpChanged(short sbp, short dbp, short hr) {
        String sbpRefStr = sbpRef.getText().toString();
        String dbpRefStr = dbpRef.getText().toString();

        if (calButton.getText().toString().equals("Done")) {
            if (!sbpRefStr.isEmpty() && !dbpRefStr.isEmpty()) {
                mSbpOffset = Integer.valueOf(sbpRefStr) - sbp;
                mDbpOffset = Integer.valueOf(dbpRefStr) - dbp;
            } else {
                mSbpOffset = 0;
                mDbpOffset = 0;
            }
        }
    }

    @Override
    public void onCalDone(double feat1, double feat2, double feat3, double feat4) {
        mFeat1 = feat1;
        mFeat2 = feat2;
        mFeat3 = feat3;
        mFeat4 = feat4;
        turnOffSensor();
        spinner.setVisibility(View.GONE);
        calButton.setText("Done");
    }

    private void turnOnSensor(){
        sensorManager.registerListener(sensorEventListener, hrm, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void turnOffSensor(){
        if (sensorManager != null){
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(Calibration.this, Manifest.permission.BODY_SENSORS);
        if (result == PackageManager.PERMISSION_DENIED){
            return true;
        }else{
            return false;
        }
    }

    public void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Calibration.this,
                            "Permission accepted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Calibration.this,
                            "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }
}
