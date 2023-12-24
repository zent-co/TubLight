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

public class TogglePowerUseCaseUdp extends BaseObservable<TogglePowerUseCaseUdp.Listener> {

    public interface Listener {
        void onTogglePowerSuccess();
        void onTogglePowerFailure(String error);
    }

    //this will need to be changed to be delivered via dependency injection with the address specified then. don't want to do this three times here.
    //NOTE: need to make use of this use case in the main part of the program. needs to be delivered through DI
    private DatagramSocket ds=null;
    private InetAddress addr;
    private int port;
    byte[] bytes;

    public TogglePowerUseCaseUdp() {

    }

    public void sendTogglePowerAndNotify(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("cmd", "togglePower");
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
            listener.onTogglePowerSuccess();
        }
    }

    private void notifyFailure(String error){
        for(Listener listener : getListeners()){
            listener.onTogglePowerFailure(error);
        }
    }
}
