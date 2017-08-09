package  com.samsung.rpp_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import samsung.rpp_demo.R;

public class ExerciseSuggestion extends AppCompatActivity {

    private int hrAvg, ccAvg;
    private TextView hrText, ccText, exText, suggestText, suggestAeroHeader, suggestAnAeroHeader;
    private WebView suggestAero, suggestAnAero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_suggestion);

        Intent intent = getIntent();
        hrAvg = intent.getIntExtra("hrAvg",0);
        ccAvg = intent.getIntExtra("ccAvg",0);

        suggestAeroHeader = (TextView) findViewById(R.id.suggest_ah);
        suggestAeroHeader.setVisibility(View.GONE);
        suggestAero = (WebView) findViewById(R.id.suggest_at);
        suggestAero.loadUrl("file:///android_asset/basic.html");
        WebSettings webSettings1 = suggestAero.getSettings();
        webSettings1.setJavaScriptEnabled(true);
        suggestAero.setVisibility(View.GONE);

        suggestAnAeroHeader = (TextView) findViewById(R.id.suggest_anh);
        suggestAnAeroHeader.setVisibility(View.GONE);
        suggestAnAero = (WebView) findViewById(R.id.suggest_ant);
        suggestAnAero.loadUrl("file:///android_asset/basic.html");
        WebSettings webSettings2 = suggestAnAero.getSettings();
        webSettings2.setJavaScriptEnabled(true);
        suggestAnAero.setVisibility(View.GONE);

        if (hrAvg > 0 && ccAvg > 0){
            setText(hrAvg, ccAvg);
        }
    }

    public void toPrevious(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void toMore(View view) {
        Intent intent = new Intent(this, Additional.class);
        startActivity(intent);
    }

    private void setText(int hrAvg, int ccAvg){
        hrText = (TextView) findViewById(R.id.avg_heart_rate);
        ccText = (TextView) findViewById(R.id.avg_cc);
        exText = (TextView) findViewById(R.id.exercise_summary);
        suggestText = (TextView) findViewById(R.id.suggestion_sum);
        suggestAeroHeader.setVisibility(View.VISIBLE);
        suggestAnAeroHeader.setVisibility(View.VISIBLE);
        suggestAero.setVisibility(View.VISIBLE);
        suggestAnAero.setVisibility(View.VISIBLE);

        hrText.setText(String.valueOf(hrAvg));
        ccText.setText(String.valueOf(ccAvg));

        if (ccAvg <= 12){
            exText.setText("Normal");
            suggestText.setText(R.string.suggestion_normal);
            suggestAero.loadUrl("file:///android_asset/cardio_high_intensity.html");
            suggestAnAero.loadUrl("file:///android_asset/high_intensity.html");
        }else if (ccAvg <= 17){
            exText.setText("At Risk");
            suggestText.setText(R.string.suggestion_risk);
            suggestAero.loadUrl("file:///android_asset/cardio_medhigh_intensity.html");
            suggestAnAero.loadUrl("file:///android_asset/medhigh_intensity.html");
        }else if (ccAvg <= 21){
            exText.setText("Danger");
            suggestText.setText(R.string.suggestion_danger);
            suggestAero.loadUrl("file:///android_asset/cardio_lowmed_intensity.html");
            suggestAnAero.loadUrl("file:///android_asset/lowmed_intensity.html");
        }else{
            exText.setText("Very Danger");
            suggestText.setText(R.string.suggestion_vdanger);
            suggestAero.loadUrl("file:///android_asset/cardio_low_intensity.html");
            suggestAnAero.loadUrl("file:///android_asset/low_intensity.html");
        }
    }
}
