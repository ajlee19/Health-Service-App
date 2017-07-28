package ashleylee.rpp_demo;

import android.Manifest;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import sds.mhs.android.BPAlgorithm;
import sds.mhs.android.BpEventListener;

public class Calibration extends AppCompatActivity implements BpEventListener {

    private SensorManager sensorManager;
    private Sensor hrm;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private Button calButton;
    private EditText sbpRef;
    private EditText dbpRef;
    private EditText hrRef;
    private ProgressBar spinner;

    private BPAlgorithm bp;
    private BPAlgorithm bpAlgorithm;
    private double mFeat1, mFeat2, mFeat3, mFeat4;
    private int mSbpOffset, mDbpOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        calButton = (Button) findViewById(R.id.cal_button);
        sbpRef = (EditText) findViewById(R.id.sbp_input);
        dbpRef = (EditText) findViewById(R.id.dbp_input);
        hrRef = (EditText) findViewById(R.id.hr_input);

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
        public void onSensorChanged(SensorEvent event) {}
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

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

    public void calibrate(View view){
        if (calButton.getText().toString().equals("Done")){
            continueToMain();
        }
        spinner.setVisibility(View.VISIBLE);
        bpAlgorithm.resetFeature();
        bpAlgorithm.resetReq();
        turnOnSensor();
        turnOffSensor();
        spinner.setVisibility(View.GONE);
        calButton.setText(R.string.cal_button_done);
    }

    public void continueToMain(){
        double[] features = new double[4];
        features[0] = mFeat1;
        features[1] = mFeat2;
        features[2] = mFeat3;
        features[3] = mFeat4;
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("sbpOffset", mSbpOffset);
        intent.putExtra("dbpOffset", mDbpOffset);
        intent.putExtra("features", features);
        startActivity(intent);
    }

    @Override
    public void onBpChanged(short sbp, short dbp, short hr) {
        String sbpRefStr = sbpRef.getText().toString();
        String dbpRefStr = dbpRef.getText().toString();
        String hrRefStr = hrRef.getText().toString();

        if (calButton.getText().equals("DONE")) {
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
        Log.d("test", String.format("Feat: %.4f, %.4f, %.4f, %.4f", mFeat1, mFeat2, mFeat3, mFeat4));
    }

    private void turnOnSensor(){
        sensorManager.registerListener(sensorEventListener, hrm, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void turnOffSensor(){
        if (sensorManager != null){
            sensorManager.unregisterListener(sensorEventListener);
        }
    }
}
