package com.example.zenthoefer.tublights.screens.controlscreen;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.example.zenthoefer.tublights.R;
import com.example.zenthoefer.tublights.screens.common.ViewMvcFactory;
import com.example.zenthoefer.tublights.screens.common.views.BaseObservableViewMvc;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.slider.LightnessSlider;

public class ControlScreenViewMvcImpl extends BaseObservableViewMvc<ControlScreenViewMvc.Listener>
        implements ControlScreenViewMvc {

    private final Button powerToggle;
    private final Button watermelonButton;
    private final Button somethingAboutSummerButton;
    private final Button beckButton;
    private final Button nightPoolButton;
    private final Button jolenesPinkButton;
    private final ColorPickerView colorPickerView;
    private final ProgressBar progressBar;
    private final LightnessSlider lightnessSlider;

    public ControlScreenViewMvcImpl(LayoutInflater layoutInflater,
                                    @Nullable ViewGroup parent,
                                    ViewMvcFactory viewMvcFactory) {

        setRootView(layoutInflater.inflate(R.layout.activity_main_picker, parent, false));

        watermelonButton = (Button) findViewById(R.id.watermelonButton);
        watermelonButton.setOnClickListener(onClickListener);
        somethingAboutSummerButton = (Button) findViewById(R.id.somethingAboutSummerButton);
        somethingAboutSummerButton.setOnClickListener(onClickListener);
        beckButton = (Button) findViewById(R.id.beckButton);
        beckButton.setOnClickListener(onClickListener);
        nightPoolButton = (Button) findViewById(R.id.nightPoolButton);
        nightPoolButton.setOnClickListener(onClickListener);
        jolenesPinkButton = (Button) findViewById(R.id.joleneButton);
        jolenesPinkButton.setOnClickListener(onClickListener);
        lightnessSlider = findViewById(R.id.v_lightness_slider);

        powerToggle = findViewById(R.id.powerToggle);

        progressBar = findViewById(R.id.progressBar);

        colorPickerView = findViewById(R.id.color_picker_view);

        colorPickerView.addOnColorChangedListener(selectedColor -> {
            for(Listener listener : getListeners()){
                listener.onColorChanged(selectedColor);
            }
        });

        colorPickerView.addOnColorSelectedListener(selectedColor -> {
            for(Listener listener : getListeners()){
                listener.onColorSelected(selectedColor);
            }
        });

        powerToggle.setOnClickListener(v -> {
            for(Listener listener : getListeners()){
                Log.i("listener", listener.toString());
                listener.onPowerToggleButtonPressed();
            }
        });
    }

    @Override
    public void bindTogglePowerButtonText(String text) {
        powerToggle.setText(text);
    }

    View.OnClickListener onClickListener = v -> {
        Button b = (Button) v;
        String buttonText = b.getText().toString();

        Log.i("button clicked",buttonText);

        for(Listener listener : getListeners()){
            listener.onColorSelectButtonPressed(buttonText);
        }
    };

    @Override
    public String getTogglePowerButtonText() {
        return powerToggle.getText().toString();
    }

    @Override
    public void showColorUI() {
        watermelonButton.setVisibility(View.VISIBLE);
        somethingAboutSummerButton.setVisibility(View.VISIBLE);
        beckButton.setVisibility(View.VISIBLE);
        nightPoolButton.setVisibility(View.VISIBLE);
        jolenesPinkButton.setVisibility(View.VISIBLE);
        powerToggle.setVisibility(View.VISIBLE);
        colorPickerView.setVisibility(View.VISIBLE);
        lightnessSlider.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
