package ashleylee.rpp_demo;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import sds.mhs.android.BPAlgorithm;

public class MainActivity extends AppCompatActivity {

    private BPAlgorithm bp;
    private SensorManager sensorManager;
    private Sensor hrm;
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private double graph2LastXValue = 5d;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        hrm = sensorManager.getDefaultSensor(65584); // WHAT IS THE SENSOR TYPE

        //spinner = (ProgressBar)findViewById(R.id.progressBar);
        //spinner.setVisibility(View.GONE);

        // for now
        GraphView graph = (GraphView) findViewById(R.id.graph);
        //LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                //new DataPoint(0, 1)
        //});
        //graph.addSeries(series);
    }

    public void measureRpp(View view){
        //spinner.setVisibility(View.VISIBLE);
        turnOnSensor();

        turnOffSensor();
        //spinner.setVisibility(View.GONE);
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {}
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    public void viewRPPAnalysis(View view) {
        Intent intent = new Intent(this, ExerciseSuggestion.class);
        startActivity(intent);
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
