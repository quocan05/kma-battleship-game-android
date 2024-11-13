package edu.utep.cs4330.battleship;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

import edu.utep.cs4330.battleship.dto.history.HistoryResult;
import edu.utep.cs4330.battleship.dto.response.HistoryResponse;

public class HistoryAdapter extends BaseAdapter {

    private Context context;
    private List<HistoryResponse> historyResults;

    public HistoryAdapter(Context context, List<HistoryResponse> historyResults) {
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
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.history_item, viewGroup, false);

        }
        HistoryResponse historyResult = historyResults.get(i);
        LinearLayout linearLayout = view.findViewById(R.id.layout);
        TextView textDate = view.findViewById(R.id.textDate);
        TextView textResult = view.findViewById(R.id.textResult);
        TextView textOpponent = view.findViewById(R.id.textOpponentName);
        if (historyResult.getWinner()) {
            linearLayout.setBackgroundColor(Color.parseColor("#0cf023")); // Màu xanh nếu thắng
        } else  {
            linearLayout.setBackgroundColor(Color.parseColor("#eb0c0c")); // Màu đỏ nếu thua
        }

        textDate.setText(historyResult.getDate());
        textOpponent.setText(historyResult.getOpponentName());
        textResult.setText(historyResult.getWinner() ? "Win" : "Lose");
        return view;
    }
}
