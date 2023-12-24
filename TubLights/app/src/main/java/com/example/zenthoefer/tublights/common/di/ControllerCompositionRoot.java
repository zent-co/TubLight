package com.example.zenthoefer.tublights.common.di;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.zenthoefer.tublights.commands.FetchStatusUseCase;
import com.example.zenthoefer.tublights.commands.SetColorUseCase;
import com.example.zenthoefer.tublights.commands.SetColorUseCaseUdp;
import com.example.zenthoefer.tublights.commands.TogglePowerUseCase;
import com.example.zenthoefer.tublights.commands.TogglePowerUseCaseUdp;
import com.example.zenthoefer.tublights.screens.common.ViewMvcFactory;
import com.example.zenthoefer.tublights.screens.common.toasthelper.ToastHelper;
import com.example.zenthoefer.tublights.screens.controlscreen.ControlScreenController;

public class ControllerCompositionRoot {

    private final CompositionRoot compositionRoot;
    private final Activity activity;

    public ControllerCompositionRoot(CompositionRoot compositionRoot, Activity activity) {
        this.compositionRoot = compositionRoot;
        this.activity = activity;
    }

    private Context getContext(){ return this.activity; }

    private LayoutInflater getLayoutInflater() { return LayoutInflater.from(getContext()); }

    private RequestQueue getVolleyRequestQueue(){
        return Volley.newRequestQueue(getContext());
    }

    public ViewMvcFactory getViewMvcFactory(){
        return new ViewMvcFactory(getLayoutInflater());
    }

    public ControlScreenController getControlScreenController(){
        return new ControlScreenController(getSetColorUseCaseUdp(),
                getTogglePowerUseCaseUdp(),
                getFetchStatusUseCase(),
                getToastHelper());
    }

    public SetColorUseCase getSetColorUseCase(){
        return new SetColorUseCase(getVolleyRequestQueue());
    }

    public SetColorUseCaseUdp getSetColorUseCaseUdp(){
        return new SetColorUseCaseUdp();
    }

    public TogglePowerUseCase getTogglePowerUseCase(){
        return new TogglePowerUseCase(getVolleyRequestQueue());
    }

    public TogglePowerUseCaseUdp getTogglePowerUseCaseUdp(){
        return new TogglePowerUseCaseUdp();
    }

    public FetchStatusUseCase getFetchStatusUseCase(){
        return new FetchStatusUseCase(getVolleyRequestQueue());
    }

    public ToastHelper getToastHelper(){
        return new ToastHelper(getContext());
    }
}
