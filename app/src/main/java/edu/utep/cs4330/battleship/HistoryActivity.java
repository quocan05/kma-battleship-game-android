package edu.utep.cs4330.battleship;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import edu.utep.cs4330.battleship.dto.history.HistoryResult;

public class HistoryActivity extends Activity {

    ListView listViewHistory;
    HistoryAdapter historyAdapter;
    List<HistoryResult> historyResultList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        listViewHistory = findViewById(R.id.listViewHistoryScore);

        historyResultList = new ArrayList<>();
        historyResultList.add(new HistoryResult("2024-10-01", "Win"));
        historyResultList.add(new HistoryResult("2024-09-30", "Lose"));
        historyResultList.add(new HistoryResult("2024-09-29", "Win"));

        historyAdapter = new HistoryAdapter(this, historyResultList);
        listViewHistory.setAdapter(historyAdapter);
    }
}
