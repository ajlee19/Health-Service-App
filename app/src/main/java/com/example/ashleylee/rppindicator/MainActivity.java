package  com.samsung.rpp_demo;

import android.content.Intent;
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
    private int aSbp;
    private int rppMeasured;
    private ArrayList<Integer> hrData, rppData;
    double[] features;

    private SensorManager sensorManager;
    private Sensor hrm;

    private GraphView graph;
    private LineGraphSeries<DataPoint> series;

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
        series = new LineGraphSeries<DataPoint>(new DataPoint[] {});
        graph.addSeries(series);

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
            hrData = new ArrayList<>();
            rppData = new ArrayList<>();
            bpAlgorithm.resetReq();
            turnOnSensor();
        }
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case 65584:
                    bpAlgorithm.pushData((int)event.values[2]);
//                    Log.d("test","value : "+ event.values[2]);
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

        hrData.add((int) hr);
        rppData.add(rppMeasured);
    }

    @Override
    public void onCalDone(double feat1, double feat2, double feat3, double feat4) {
        mFeat1 = feat1;
        mFeat2 = feat2;
        mFeat3 = feat3;
        mFeat4 = feat4;
        //Log.d("test", String.format("Feat: %.4f, %.4f, %.4f, %.4f", mFeat1, mFeat2, mFeat3, mFeat4));
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
        int tot = 0;
        for (Integer i: data){tot += i;}
        return Math.round(tot/data.size());
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
}
