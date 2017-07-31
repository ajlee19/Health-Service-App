package  com.samsung.rpp_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import samsung.rpp_demo.R;

public class ExerciseSuggestion extends AppCompatActivity {

    private int hrAvg, rppAvg, sbpOffset;
    private double[] features;
    private TextView hrText, rppText, exText, suggestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_suggestion);

        Intent intent = getIntent();
        hrAvg = intent.getIntExtra("hrAvg",0);
        rppAvg = intent.getIntExtra("rppAvg",0);
        sbpOffset = intent.getIntExtra("sbpOffset",0);
        features = intent.getDoubleArrayExtra("features");

        if (hrAvg > 0 && rppAvg > 0){
            setText(hrAvg, rppAvg);
        }
    }

    /** Back to Main */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("features", features);
        intent.putExtra("sbpOffset", sbpOffset);
        startActivity(intent);
    }

    private void setText(int hrAvg, int rppAvg){
        hrText = (TextView) findViewById(R.id.avg_heart_rate);
        rppText = (TextView) findViewById(R.id.avg_rpp);
        exText = (TextView) findViewById(R.id.exercise_summary);

        hrText.setText(String.valueOf(hrAvg));
        rppText.setText(String.valueOf(rppAvg));

        if (rppAvg <= 12){
            exText.setText("Normal");
        }else if (rppAvg <= 17){
            exText.setText("At Risk");
        }else if (rppAvg <= 21){
            exText.setText("Danger");
        }else{
            exText.setText("Very Danger");
        }
        setExerciseSuggestion(rppAvg);
    }

    private void setExerciseSuggestion(int rppAvg){
        suggestText = (TextView) findViewById(R.id.exercise_suggestion);

        if (rppAvg <= 12){
            suggestText.setText("Normal");
        }else if (rppAvg <= 17){
            suggestText.setText("At Risk");
        }else if (rppAvg <= 21){
            suggestText.setText("Danger");
        }else{
            suggestText.setText("Very Danger");
        }
    }
}
