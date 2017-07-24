package ashleylee.rpp_demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    /** Called when the user touches the button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, AboutBp.class);
        startActivity(intent);
    }

}
