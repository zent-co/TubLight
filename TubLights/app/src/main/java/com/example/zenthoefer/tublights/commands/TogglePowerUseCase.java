package com.example.zenthoefer.tublights.commands;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.zenthoefer.tublights.common.BaseObservable;
import com.example.zenthoefer.tublights.common.Constants;

public class TogglePowerUseCase extends BaseObservable<TogglePowerUseCase.Listener> {

    public interface Listener {
        void onTogglePowerSuccess(String response);
        void onTogglePowerFailure(String error);
    }

    private final RequestQueue queue;
    private final String uri = Constants.URL.concat("/togglePower");

    public TogglePowerUseCase(RequestQueue queue) {
        this.queue = queue;
        Log.i("queue", queue.toString());
    }

    public void sendTogglePowerAndNotify(){
        StringRequest putRequest = new StringRequest(Request.Method.PUT, uri,
                this::notifySuccess,
                error -> notifyFailure(error.toString())
        );

        queue.add(putRequest);
    }

    private void notifySuccess(String response){
        for(Listener listener : getListeners()){
            listener.onTogglePowerSuccess(response);
        }
    }

    private void notifyFailure(String error){
        for(Listener listener : getListeners()){
            listener.onTogglePowerFailure(error);
        }
    }
}
