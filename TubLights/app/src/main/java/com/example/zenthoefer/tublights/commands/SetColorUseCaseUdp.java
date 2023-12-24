package com.example.zenthoefer.tublights.commands;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.zenthoefer.tublights.common.BaseObservable;
import com.example.zenthoefer.tublights.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class SetColorUseCaseUdp extends BaseObservable<SetColorUseCaseUdp.Listener> {

    public interface Listener{
        void onSetColorSuccess();
        void onSetColorFail(String error);
    }

    //this will need to be changed to be delivered via dependency injection with the address specified then. don't want to do this three times here.
    //NOTE: need to make use of this use case in the main part of the program. needs to be delivered through DI
    private DatagramSocket ds=null;
    private InetAddress addr;
    private int port;
    byte[] bytes;

    private enum Rgb {RED, GREEN, BLUE}
    private Double r;
    private Double g;
    private Double b;

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
        try {
            JSONObject obj = new JSONObject();
            obj.put("cmd", "color");
            obj.put("r", r);
            obj.put("g", g);
            obj.put("b", b);
            String s = obj.toString();
            bytes = s.getBytes("UTF-8");
        }
        catch (JSONException e){System.out.println(e.toString());}
        catch(IOException e) {System.out.println(e.toString());}

        try {
            addr = InetAddress.getByName("192.168.1.25");
            port = 1234;
            ds = new DatagramSocket();
        }
        catch(IOException e) {System.out.println(e.toString());}

        AsyncTask.execute(new Runnable(){

            @Override public void run(){
                try {
                    DatagramPacket packet=new DatagramPacket(bytes,bytes.length,addr,port);
                    ds.send(packet);
                    notifySuccess();
                }
                catch(IOException e) {
                    System.out.println(e.toString());
                    notifyFailure(e.toString());
                }
            }
        });
    }

    private void notifySuccess(){
        for(Listener listener : getListeners()){
            listener.onSetColorSuccess();
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
