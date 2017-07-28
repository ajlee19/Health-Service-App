package ashleylee.rpp_demo;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import sds.mhs.android.BPAlgorithm;
import sds.mhs.android.BpEventListener;

public class MainActivity extends AppCompatActivity implements BpEventListener {

    private BPAlgorithm bp;
    private BPAlgorithm bpAlgorithm;
    private int sbpOffset;
    private double mFeat1, mFeat2, mFeat3, mFeat4;

    private TextView hrText, rppText, shortSum;
    private Button measureBtn;
    private int aSbp, aDbp;
    private int rppMeasured;
    private ArrayList<Integer> hrData, rppData;

    private SensorManager sensorManager;
    private Sensor hrm;

    private GraphView graph;
    private LineGraphSeries<DataPoint> series;
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

        Intent intent = getIntent();
        sbpOffset = intent.getIntExtra("sbpOffset", 0);
        double[] features = intent.getDoubleArrayExtra("features");
        onCalDone(features[0], features[1], features[2], features[3]);
        hrText = (TextView) findViewById(R.id.heart_rate);
        rppText = (TextView) findViewById(R.id.rpp);
        shortSum = (TextView) findViewById(R.id.shortSum);
        measureBtn = (Button) findViewById(R.id.btn_measure);
        measureBtn.setText(R.string.measure_button);

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        hrm = sensorManager.getDefaultSensor(65584); // WHAT IS THE SENSOR TYPE

        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>(new DataPoint[] {});
//        Viewport.viewport = graph.getViewport();
//        viewport.setYAxisBoundsManual(true);
//        viewport.setMinY(5);
//        viewport.setMaxY(25);
//        viewport.setScrollable(true);

        graph.addSeries(series);
        bpAlgorithm = new BPAlgorithm(this);
        bpAlgorithm.setBpChangedListener(this);
    }

    public void measureRpp(View view){
        measureBtn.setText("Measuring");
        hrData = new ArrayList<>();
        rppData = new ArrayList<>();
        bpAlgorithm.resetReq();
        turnOnSensor();
        // do nothing?
        turnOffSensor();
        measureBtn.setText(R.string.measure_button);
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {}
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    private void turnOnSensor(){
        sensorManager.registerListener(sensorEventListener, hrm, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void turnOffSensor(){
        if (sensorManager != null){
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    @Override
    public void onBpChanged(short sbp, short dbp, short hr) {
        aSbp =  sbp + sbpOffset;
        rppMeasured = Math.round((aSbp*hr)/1000);

        rppText.setText(String.valueOf(rppMeasured));
        hrText.setText(String.valueOf(hr));
        if (rppMeasured <= 12){
            shortSum.setText("Normal");
        }else if (rppMeasured <= 17){
            shortSum.setText("At Risk");
        }else if (rppMeasured <= 21){
            shortSum.setText("Danger");
        }else{
            shortSum.setText("Very Danger");
        }

        hrData.add((int) hr);
        rppData.add(rppMeasured);
    }

    @Override
    public void onCalDone(double feat1, double feat2, double feat3, double feat4) {
        mFeat1 = feat1;
        mFeat2 = feat2;
        mFeat3 = feat3;
        mFeat4 = feat4;
        Log.d("test", String.format("Feat: %.4f, %.4f, %.4f, %.4f", mFeat1, mFeat2, mFeat3, mFeat4));
    }

    public void viewRPPAnalysis(View view) {
        Intent intent = new Intent(this, ExerciseSuggestion.class);
        intent.putExtra("hrData", hrData);
        intent.putExtra("rppData", rppData);
        startActivity(intent);
    }
}
