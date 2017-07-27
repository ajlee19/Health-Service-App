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
import android.widget.ProgressBar;
import android.widget.Toast;

public class Calibration extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor hrm;
    private Button calButton;
    private ProgressBar spinner;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        calButton = (Button) findViewById(R.id.cal_button);

        // Checking permission
        if (checkPermission()){
            Log.e("permission", "Permission already granted");
        }else{
            requestPermission();
        }

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        hrm = sensorManager.getDefaultSensor(65584);

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
            //start next activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        //spinner.setVisibility(View.VISIBLE);
        turnOnSensor();
        // BP BEASURE
        //spinner.setVisibility(View.GONE);
        turnOffSensor();
        calButton.setText(R.string.cal_button_done);
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
