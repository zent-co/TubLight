package com.example.zenthoefer.tublights.commands;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.zenthoefer.tublights.common.BaseObservable;
import com.example.zenthoefer.tublights.common.Constants;

import java.util.HashMap;
import java.util.Map;

public class SetColorUseCase extends BaseObservable<SetColorUseCase.Listener> {

    public interface Listener{
        void onSetColorSuccess(String response);
        void onSetColorFail(String error);
    }

    private final RequestQueue queue;
    private final String uri = Constants.URL.concat("/color");
    private enum Rgb {RED, GREEN, BLUE}
    private Double r;
    private Double g;
    private Double b;

    public SetColorUseCase(RequestQueue queue) {
        this.queue = queue;
    }

    public void setColorAndNotify(int color){
        convertAndSetColorIntToRgbByteValue(color, Rgb.RED);
        convertAndSetColorIntToRgbByteValue(color, Rgb.GREEN);
        convertAndSetColorIntToRgbByteValue(color, Rgb.BLUE);

        sendColorRequest();
    }

    public void setColorAndNotify(String buttonName){
        setRgbByButtonColorName(buttonName);

        sendColorRequest();
    }

    private void sendColorRequest() {
        StringRequest putRequest = new StringRequest(Request.Method.PUT, uri+"?r="+Double.toString(r)+"&b="+Double.toString(b)+"&g="+Double.toString(g),
                this::notifySuccess,
                error -> notifyFailure(error.toString())
        ) {
/*            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                //params.put("command", "color");
                params.put("r", Double.toString(r));
                params.put("g", Double.toString(g));
                params.put("b", Double.toString(b));

                Log.i("params as string", params.toString());

                return params;
            }*/
        };

        Log.i("putRequest as string", putRequest.toString());

        queue.add(putRequest);
    }

    private void notifySuccess(String response){
        for(Listener listener : getListeners()){
            listener.onSetColorSuccess(response);
        }
    }

    private void notifyFailure(String error){
        for(Listener listener : getListeners()){
            listener.onSetColorFail(error);
        }
    }

    private void convertAndSetColorIntToRgbByteValue(int color, Rgb rgbSelection) {

        String hexStringColor = Integer.toHexString(color);

        if(rgbSelection== Rgb.RED){
            String rString = hexStringColor.substring(2,4);
            Log.i("red string = ",rString);
            r = (double) Integer.parseInt(rString,16);
            Log.i("red string = ",Double.toString(r));
        } else if (rgbSelection== Rgb.GREEN){
            String gString = hexStringColor.substring(4,6);
            Log.i("green string = ",gString);
            g = (double) Integer.parseInt(gString,16);
            Log.i("green string = ",Double.toString(g));
        } else if (rgbSelection== Rgb.BLUE){
            String bString = hexStringColor.substring(6,8);
            Log.i("blue string = ",bString);
            b = (double) Integer.parseInt(bString,16);
            Log.i("blue string = ",Double.toString(b));
        }
    }

    private void setRgbByButtonColorName(String buttonName) {
        switch (buttonName) {
            case "Watermelon":
                r = 239d;
                g = 0d;
                b = 10d;
                break;
            case "Something About Summer":
                r = 200d;
                g = 197d;
                b = 0d;
                break;
            case "Beck":
                r = 183d;
                g = 0d;
                b = 32d;
                break;
            case "Jolene's Pink":
                r = 213d;
                g = 0d;
                b = 117d;
                break;
            case "Night Pool":
                r = 0d;
                g = 15d;
                b = 15d;
                break;
        }
    }
}
