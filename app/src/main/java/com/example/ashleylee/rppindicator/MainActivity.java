package  com.samsung.rpp_demo;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.samsung.sds.mhs.android.BPAlgorithm;
import com.samsung.sds.mhs.android.BpEventListener;
import java.util.ArrayList;
import samsung.rpp_demo.R;

public class MainActivity extends AppCompatActivity implements BpEventListener {

    private BPAlgorithm bpAlgorithm;
    private int sbpOffset;
    private double mFeat1, mFeat2, mFeat3, mFeat4;
    private TextView hrText, rppText;
    private Button measureBtn;
    private int aSbp, rppMeasured;
    private ArrayList<Integer> hrData, rppData;
    double[] features;
    private SensorManager sensorManager;
    private Sensor hrm;
    private GraphView graph;
    private LineGraphSeries<DataPoint> series;
    private double xVal = 0;
    private int[] mColor = new int[8];
//    private Runnable timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        sbpOffset = intent.getIntExtra("sbpOffset", 0);
        features = intent.getDoubleArrayExtra("features");

        hrText = (TextView) findViewById(R.id.heart_rate);
        rppText = (TextView) findViewById(R.id.rpp);
        measureBtn = (Button) findViewById(R.id.measure_button);

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        hrm = sensorManager.getDefaultSensor(65584); // WHAT IS THE SENSOR TYPE
        hrData = new ArrayList<>();
        rppData = new ArrayList<>();

        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        graphSetting();

        bpAlgorithm = new BPAlgorithm(this);
        bpAlgorithm.setBpChangedListener(this);
        bpAlgorithm.setFeature(features[0], features[1], features[2], features[3]);
    }

    public void measureRpp(View view){
        if (measureBtn.getText().toString().equals("Measuring")){
            turnOffSensor();
            measureBtn.setText("Measure RPP");
            hrText.setText("");
            rppText.setText("");
        } else{
            measureBtn.setText("Measuring");
            hrText.setText("--");
            rppText.setText("--");

            DataPoint[] points = new DataPoint[0];
            series.resetData(points);
            hrData = new ArrayList<>();
            rppData = new ArrayList<>();
            bpAlgorithm.resetReq();
            turnOnSensor();
        }
    }

    public void viewRPPAnalysis(View view) {
        int hrAvg = calculateAverage(hrData);
        int rppAvg = calculateAverage(rppData);
        turnOffSensor();
        Intent intent = new Intent(this, ExerciseSuggestion.class);
        intent.putExtra("hrAvg", hrAvg);
        intent.putExtra("rppAvg", rppAvg);
        intent.putExtra("features", features);
        intent.putExtra("sbpOffset", sbpOffset);
        startActivity(intent);
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

    @Override
    public void onBpChanged(short sbp, short dbp, short hr) {
        aSbp =  sbp + sbpOffset;
        rppMeasured = Math.round((aSbp*hr)/1000);

        rppText.setText(String.valueOf(rppMeasured));
        hrText.setText(String.valueOf(hr));

        boolean scroll;
        if (xVal >100)
            scroll =true;
        else
            scroll =false;

        series.appendData(new DataPoint(xVal++, rppMeasured), scroll, 1000);
        updateGraphColor(rppMeasured);
        hrData.add((int) hr);
        rppData.add(rppMeasured);
    }

    @Override
    public void onCalDone(double feat1, double feat2, double feat3, double feat4) {
        mFeat1 = feat1;
        mFeat2 = feat2;
        mFeat3 = feat3;
        mFeat4 = feat4;
    }

    private void turnOnSensor(){
        sensorManager.registerListener(sensorEventListener, hrm, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void turnOffSensor(){
        if (sensorManager != null){
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    private int calculateAverage(ArrayList<Integer> data){
        if (data.isEmpty()){
            return 0;
        }
        int tot = 0;
        for (Integer i: data) {
            tot += i;
        }
        return Math.round(tot/data.size());
    }

    private void updateGraphColor(int rpp){
        if (rpp <= 12){
            series.setColor(mColor[0]);
            series.setBackgroundColor(mColor[1]);
        }else if (rpp <= 17){
            series.setColor(mColor[2]);
            series.setBackgroundColor(mColor[3]);
        }else if (rpp <= 21){
            series.setColor(mColor[4]);
            series.setBackgroundColor(mColor[5]);
        }else{
            series.setColor(mColor[6]);
            series.setBackgroundColor(mColor[7]);
        }
    }

    private void graphSetting(){
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL); //only horizontal grid
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        // area below the graph
        series.setThickness(5);
        series.setDrawBackground(true);

        Viewport viewport = graph.getViewport();
        // set y bound
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(5);
        viewport.setMaxY(25);
        // set x bound
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(100);

        viewport.setScrollable(true);
//        viewport.setScalable(true);
//        viewport.scrollToEnd();

        mColor[0] = Color.rgb(178,255,89); //green line
        mColor[1] = Color.rgb(204, 255, 144); //green background
        mColor[2] = Color.rgb(255,241,118); //yellow
        mColor[3] = Color.rgb(255, 249, 196); //yellow background
        mColor[4] = Color.rgb(255, 167, 38); //orange
        mColor[5] = Color.rgb(255, 224, 178); //orange background
        mColor[6] = Color.rgb(239, 83, 80); //red
        mColor[7] = Color.rgb(255, 205, 210); //red background
    }
}
