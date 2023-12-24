package com.example.zenthoefer.tublights.screens.common.controllers;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zenthoefer.tublights.common.CustomApplication;
import com.example.zenthoefer.tublights.common.di.ControllerCompositionRoot;

public class BaseActivity extends AppCompatActivity {

    private ControllerCompositionRoot controllerCompositionRoot;

    protected ControllerCompositionRoot getControllerCompositionRoot(){
        if (controllerCompositionRoot == null) {
            controllerCompositionRoot = new ControllerCompositionRoot(
                    ((CustomApplication) getApplication()).getCompositionRoot(),
                    this
            );
        }
        return controllerCompositionRoot;
    }

}
