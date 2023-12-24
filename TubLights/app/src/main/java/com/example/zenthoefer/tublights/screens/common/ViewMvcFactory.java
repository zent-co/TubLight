package com.example.zenthoefer.tublights.screens.common;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.zenthoefer.tublights.screens.controlscreen.ControlScreenViewMvc;
import com.example.zenthoefer.tublights.screens.controlscreen.ControlScreenViewMvcImpl;

public class ViewMvcFactory {

    private final LayoutInflater layoutInflater;

    public ViewMvcFactory(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public ControlScreenViewMvc getControlScreenViewMvc(@Nullable ViewGroup parent){
        return new ControlScreenViewMvcImpl(layoutInflater, parent, this);
    }
}
