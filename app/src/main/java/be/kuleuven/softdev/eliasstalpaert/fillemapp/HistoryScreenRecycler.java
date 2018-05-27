package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class HistoryScreenRecycler extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryMovieAdapter adapter;
    private TextView empty;
    private Button clear, exitHistoryButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_screen_recycler);

        this.findViews();
        this.setOnClickListeners();
        checkIfEmpty();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoryMovieAdapter(this, MainActivity.historyMoviesList);
        recyclerView.setAdapter(adapter);

        itemTouchHelperCallbackInit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(MainActivity.historyMoviesList.isEmpty()){
            MainActivity.history = new ArrayList<>();
        }
    }

    private void itemTouchHelperCallbackInit() {
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }


    private void findViews(){
        recyclerView = findViewById(R.id.historyRecycler);
        empty = findViewById(R.id.emptyHistory);
        clear = findViewById(R.id.clearHistoryRecycler);
        exitHistoryButton = findViewById(R.id.exitHistoryButton);
    }

    private void setOnClickListeners(){
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.historyMoviesList = new LinkedList<>();
                MainActivity.history = new ArrayList<>();
                Toast.makeText(MainActivity.mContext, "History cleared", Toast.LENGTH_SHORT).show();
                finishActivity();
            }
        });

        exitHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
    }

    private void checkIfEmpty(){
        if(MainActivity.historyMoviesList.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }
        else{
            clear.setVisibility(View.VISIBLE);
        }
    }

    private void finishActivity(){
        this.finish();
    }
}
