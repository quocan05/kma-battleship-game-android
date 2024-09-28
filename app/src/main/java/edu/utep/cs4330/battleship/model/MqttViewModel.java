package edu.utep.cs4330.battleship.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MqttViewModel extends ViewModel {
    private MutableLiveData<String> messageLiveData = new MutableLiveData<>();

    public LiveData<String> getMessage() {
        return messageLiveData;
    }

    public void setMessage(String message) {
        messageLiveData.setValue(message);
    }
}
