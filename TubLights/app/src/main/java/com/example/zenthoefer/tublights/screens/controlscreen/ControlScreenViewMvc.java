package com.example.zenthoefer.tublights.screens.controlscreen;

import com.example.zenthoefer.tublights.screens.common.views.ObservableViewMvc;

public interface ControlScreenViewMvc extends ObservableViewMvc<ControlScreenViewMvc.Listener> {

    interface Listener{
        void onColorSelectButtonPressed(String buttonName);
        void onPowerToggleButtonPressed();
        void onColorChanged(int selectedColor);
        void onColorSelected(int selectedColor);
    }

    void bindTogglePowerButtonText(String text);
    String getTogglePowerButtonText();

    void showColorUI();
    void hideProgressBar();
}
