package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HistoryScreen extends AppCompatActivity {

    private TextView testview1, testview2;
    private Button resetHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_screen);
        findViews();
        updateTextViews();
        setOnClickListeners();
    }

    private void findViews(){
        testview1 = findViewById(R.id.testview1);
        testview2 = findViewById(R.id.testview2);
        resetHistory = findViewById(R.id.resetHistory);
    }

    private void setOnClickListeners(){
        resetHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.history = new ArrayList<>();
                finishActivity();
            }
        });
    }

    private void updateTextViews(){
        if(!MainActivity.history.isEmpty()){
            testview1.setText(MainActivity.history.get(0));
        }
    }

    private void finishActivity(){
        Toast.makeText(MainActivity.mContext, "History cleared", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
