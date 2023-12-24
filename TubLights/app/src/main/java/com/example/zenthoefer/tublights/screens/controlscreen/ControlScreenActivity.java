package com.example.zenthoefer.tublights.screens.controlscreen;

import android.os.Bundle;

import com.example.zenthoefer.tublights.screens.common.controllers.BaseActivity;

public class ControlScreenActivity extends BaseActivity {

    private ControlScreenController controlScreenController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ControlScreenViewMvc controlScreenViewMvc = getControllerCompositionRoot().getViewMvcFactory().getControlScreenViewMvc(null);

        controlScreenController = getControllerCompositionRoot().getControlScreenController();
        controlScreenController.bindView(controlScreenViewMvc);

        setContentView(controlScreenViewMvc.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();

        controlScreenController.onStart();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        controlScreenController.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

        controlScreenController.onStop();
    }
}
