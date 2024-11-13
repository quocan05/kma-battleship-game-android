//package edu.utep.cs4330.battleship;
//
//import android.app.Application;
//import android.util.Log;
//
//import java.io.IOException;
//
//import edu.utep.cs4330.battleship.common.Common;
//import edu.utep.cs4330.battleship.common.Constants;
//import edu.utep.cs4330.battleship.dto.UserSingleton;
//import edu.utep.cs4330.battleship.dto.request.RoomRequest;
//import edu.utep.cs4330.battleship.service.BEService;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class MyApp extends Application {
//    private BEService beService;
//    private UserSingleton userSingleton;
//    private String TAG = "TERMINATE";
//    @Override
//    public void onCreate() {
//        beService = BEService.getInstance();
//        userSingleton = UserSingleton.getInstance();
//        Request roRequest = Common.getRequest(null, Constants.LOGOUT + "/" + 1, Constants.POST);
//        Log.d(TAG, "TERMINATE");
//
//        beService.getClient().newCall(roRequest).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("LoginError", "Request failed", e);
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "SUCCESS");
//                }
//            }
//        });
//        super.onCreate();
//
//        // Khởi tạo ứng dụng
//    }
//    @Override
//    public void onTrimMemory(int level) {
//        super.onTrimMemory(level);
//        if (level == TRIM_MEMORY_UI_HIDDEN) {
//            // Ứng dụng đang ở background (người dùng thoát ứng dụng)
//            // Xử lý hành động khi ứng dụng bị thoát
//        }
//    }
//
//    @Override
//    public void onTerminate() {
//        super.onTerminate();
//        Request roRequest = Common.getRequest(null, Constants.LOGOUT + "/" + userSingleton.getId() , Constants.POST);
//        Log.d(TAG, "TERMINATE");
//
//        beService.getClient().newCall(roRequest).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("LoginError", "Request failed", e);
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "SUCCESS");
//                }
//            }
//        });
//        // Xử lý khi ứng dụng bị hệ thống hoặc người dùng tắt hoàn toàn
//    }
//}