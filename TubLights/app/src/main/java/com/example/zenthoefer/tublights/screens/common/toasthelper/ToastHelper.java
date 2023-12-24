package com.example.zenthoefer.tublights.screens.common.toasthelper;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {

    private final Context context;

    public ToastHelper(Context context){
        this.context = context;
    }

    public void showVolleyError(String errorMessage){
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
