package edu.utep.cs4330.battleship;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import edu.utep.cs4330.battleship.common.Common;
import edu.utep.cs4330.battleship.common.Constants;
import edu.utep.cs4330.battleship.dto.UserSingleton;
import edu.utep.cs4330.battleship.dto.history.HistoryResult;
import edu.utep.cs4330.battleship.dto.request.RoomRequest;
import edu.utep.cs4330.battleship.dto.response.BEResponse;
import edu.utep.cs4330.battleship.dto.response.HistoryResponse;
import edu.utep.cs4330.battleship.service.BEService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryActivity extends Activity {

    ListView listViewHistory;
    HistoryAdapter historyAdapter;
    List<HistoryResponse> historyResultList;
    private BEService beService;
    private UserSingleton userSingleton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beService = BEService.getInstance();
        userSingleton = UserSingleton.getInstance();
        setContentView(R.layout.activity_history);
        listViewHistory = findViewById(R.id.listViewHistoryScore);

        historyResultList = new ArrayList<>();
        getRoom();
    }

    public void getRoom() {
        Request roRequest = Common.getRequest(null, Constants.GET_ROOM + "/" + this.userSingleton.getId(), Constants.GET);

        beService.getClient().newCall(roRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LoginError", "Request failed", e);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String responseData = response.body().string();
                    BEResponse beResponse = gson.fromJson(responseData,BEResponse.class);
                    ArrayList<LinkedTreeMap> list = (ArrayList) beResponse.getData();
                    for(LinkedTreeMap<String,String> linkedTreeMap : list ){
                        HistoryResponse historyResponse = new HistoryResponse();
                        historyResponse.convertLinkedTree(linkedTreeMap);
                        historyResultList.add(historyResponse);
                    }
                    // Đảm bảo cập nhật giao diện người dùng trên luồng chính
                    runOnUiThread(() -> {
                        historyAdapter = new HistoryAdapter(HistoryActivity.this, historyResultList);
                        listViewHistory.setAdapter(historyAdapter);
                    });
                }

            }
        });
    }


}
