package ashleylee.rpp_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ExerciseSuggestion extends AppCompatActivity {

    private ArrayList<Integer> hrData, rppData;
    private int hrAvg, rppAvg;
    private TextView hrText, rppText, exText, suggestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_suggestion);

        Intent intent = getIntent();
        hrData = intent.getIntegerArrayListExtra("hrData");
        rppData = intent.getIntegerArrayListExtra("rppData");

        if (hrData.size() > 0 && rppData.size() > 0){
            setText(hrData, rppData);
        }
    }

    /** Called when the user touches the button */
    public void sendMessage(View view) {
        // return back to the main page
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setText(ArrayList<Integer> hrData, ArrayList<Integer> rppData){
        hrText = (TextView) findViewById(R.id.avg_heart_rate);
        rppText = (TextView) findViewById(R.id.avg_rpp);
        exText = (TextView) findViewById(R.id.exercise_summary);

        hrAvg = calculateAverage(hrData);
        rppAvg = calculateAverage(rppData);

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

    private int calculateAverage(ArrayList<Integer> data){
        int tot = 0;
        for (Integer i: data){tot += i;}
        return Math.round(tot/data.size());
    }
}
