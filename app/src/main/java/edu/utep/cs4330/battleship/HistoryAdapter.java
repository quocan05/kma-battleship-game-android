package edu.utep.cs4330.battleship;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.utep.cs4330.battleship.dto.history.HistoryResult;

public class HistoryAdapter extends BaseAdapter {

    private Context context;
    private List<HistoryResult> historyResults;

    public HistoryAdapter(Context context, List<HistoryResult> historyResults) {
        this.context = context;
        this.historyResults = historyResults;
    }

    @Override
    public int getCount() {
        return historyResults.size();
    }

    @Override
    public Object getItem(int i) {
        return historyResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.history_item, viewGroup, false);

        }
        HistoryResult historyResult = historyResults.get(i);

        TextView textDate = view.findViewById(R.id.textDate);
        TextView textResult = view.findViewById(R.id.textResult);

        textDate.setText(historyResult.getDate());
        textResult.setText(historyResult.getResult());
        return view;
    }
}
