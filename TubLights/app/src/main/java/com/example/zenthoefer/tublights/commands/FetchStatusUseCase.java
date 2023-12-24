package com.example.zenthoefer.tublights.commands;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.zenthoefer.tublights.common.BaseObservable;
import com.example.zenthoefer.tublights.common.Constants;

public class FetchStatusUseCase extends BaseObservable<FetchStatusUseCase.Listener> {

    public interface Listener {
        void onFetchStatusSuccess(String response);
        void onFetchStatusFailed(String error);
    }

    private final RequestQueue queue;
    private final String uri = Constants.URL.concat("/getState");

    public FetchStatusUseCase(RequestQueue queue) {
        this.queue = queue;
    }

    public void fetchStatusAndNotify() {
        StringRequest putRequest = new StringRequest(Request.Method.GET, uri,
                this::notifySuccess,
                error -> notifyFailure(error.toString())
        );

        queue.add(putRequest);
    }

    private void notifySuccess(String response){
        for(Listener listener : getListeners()){
            listener.onFetchStatusSuccess(response);
        }
    }

    private void notifyFailure(String error){
        for(Listener listener : getListeners()){
            listener.onFetchStatusFailed(error);
        }
    }
}
