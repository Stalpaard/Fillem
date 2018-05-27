package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class HistoryScreenRecycler extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryMovieAdapter adapter;
    private TextView empty, helpHistory1, helpHistory2, helpHistory3;
    private Boolean history_enable;
    private Button clear, exitHistoryButton, helpHistoryButton, exitHelpHistoryButton;
    private CardView helpScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_screen_recycler);
        this.findViews();
        this.setOnClickListeners();
        history_enable = getIntent().getBooleanExtra(MainActivity.HISTORYENABLE, true);
        historyEnableInit(history_enable);

        checkIfEmpty();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


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

    private void historyEnableInit(boolean history_enable){
        if(history_enable){
            adapter = new HistoryMovieAdapter(this, MainActivity.historyMoviesList, history_enable);
        }
        else{
            MainActivity.loadWatchList(this);
            adapter = new HistoryMovieAdapter(this, MainActivity.watchList, history_enable);
            clear.setVisibility(View.GONE);
            helpHistory1.setTextSize(17);
            helpHistory1.setText("Movie seen: to remove an entry, swipe it to the right or the left");
            helpHistory2.setVisibility(View.GONE);
            helpHistory3.setTextSize(17);
            helpHistory3.setText("Add movie: scroll to the bottom in the movie screen to see the 'Add to watchlist' button");
        }
    }

    private void itemTouchHelperCallbackInit() {
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }


    private void findViews(){
        helpHistory1 = findViewById(R.id.helpHistory1);
        helpHistory2 = findViewById(R.id.helpHistory2);
        helpHistory3 = findViewById(R.id.helpHistory3);
        recyclerView = findViewById(R.id.historyRecycler);
        empty = findViewById(R.id.emptyHistory);
        clear = findViewById(R.id.clearHistoryRecycler);
        exitHistoryButton = findViewById(R.id.exitHistoryButton);
        helpScreen = findViewById(R.id.helpScreenHistory);
        helpHistoryButton = findViewById(R.id.helpHistoryButton);
        exitHelpHistoryButton = findViewById(R.id.closeHelpHistory);
    }

    private void setOnClickListeners(){


        helpHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpScreen.setVisibility(View.VISIBLE);
            }
        });

        exitHelpHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpScreen.setVisibility(View.GONE);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.historyMoviesList.clear();
                MainActivity.history.clear();
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
        if(history_enable){
            if(MainActivity.historyMoviesList.isEmpty()){
                empty.setVisibility(View.VISIBLE);
            }
            else{
                clear.setVisibility(View.VISIBLE);
            }
        }
        else{
            if(MainActivity.watchList.isEmpty()){
                empty.setVisibility(View.VISIBLE);
            }
            else{
                //clear.setVisibility(View.VISIBLE);
            }
        }

    }

    private void finishActivity(){
        this.finish();
    }
}
