package com.example.zenthoefer.tublights.screens.controlscreen;

import android.util.Log;

import com.example.zenthoefer.tublights.commands.FetchStatusUseCase;
import com.example.zenthoefer.tublights.commands.SetColorUseCase;
import com.example.zenthoefer.tublights.commands.SetColorUseCaseUdp;
import com.example.zenthoefer.tublights.commands.TogglePowerUseCase;
import com.example.zenthoefer.tublights.commands.TogglePowerUseCaseUdp;
import com.example.zenthoefer.tublights.common.Constants;
import com.example.zenthoefer.tublights.screens.common.toasthelper.ToastHelper;

public class ControlScreenController implements ControlScreenViewMvc.Listener,
        SetColorUseCaseUdp.Listener,
        TogglePowerUseCaseUdp.Listener,
        FetchStatusUseCase.Listener{

    private final SetColorUseCaseUdp setColorUseCaseUdp;
    private final TogglePowerUseCaseUdp togglePowerUseCaseUdp;
    private final FetchStatusUseCase fetchStatusUseCase;
    private final ToastHelper toastHelper;

    private int fetchStatusFailCount = 0;

    ControlScreenViewMvc controlScreenViewMvc;

    public ControlScreenController(SetColorUseCaseUdp setColorUseCaseUdp,
                                   TogglePowerUseCaseUdp togglePowerUseCaseUdp,
                                   FetchStatusUseCase fetchStatusUseCase,
                                   ToastHelper toastHelper) {
        this.setColorUseCaseUdp = setColorUseCaseUdp;
        this.togglePowerUseCaseUdp = togglePowerUseCaseUdp;
        this.fetchStatusUseCase = fetchStatusUseCase;
        this.toastHelper = toastHelper;
    }

    public void bindView(ControlScreenViewMvc controlScreenView){ this.controlScreenViewMvc = controlScreenView; }

    public void onStart() {
        fetchStatusUseCase.registerListener(this);
        togglePowerUseCaseUdp.registerListener(this);
        setColorUseCaseUdp.registerListener(this);
        controlScreenViewMvc.registerListener(this);
    }

    public void onStop() {
        fetchStatusUseCase.unregisterListener(this);
        togglePowerUseCaseUdp.unregisterListener(this);
        setColorUseCaseUdp.unregisterListener(this);
        controlScreenViewMvc.unregisterListener(this);
    }

    public void onResume() {
        fetchStatusUseCase.fetchStatusAndNotify();
    }

    @Override
    public void onColorSelectButtonPressed(String buttonName) {
        setColorUseCaseUdp.setColorAndNotify(buttonName);
    }

    @Override
    public void onPowerToggleButtonPressed() {
        togglePowerUseCaseUdp.sendTogglePowerAndNotify();
    }

    @Override
    public void onColorChanged(int selectedColor) {
        setColorUseCaseUdp.setColorAndNotify(selectedColor);
    }

    @Override
    public void onColorSelected(int selectedColor) {
        //currently no-op
    }

    @Override
    public void onSetColorSuccess() {
        controlScreenViewMvc.bindTogglePowerButtonText(Constants.turnPowerOff);
        Log.i("Color Change Success","Success");
    }

    @Override
    public void onSetColorFail(String error) {
        toastHelper.showVolleyError(error);
    }

    @Override
    public void onFetchStatusSuccess(String response) {

        fetchStatusFailCount = 0; //set back to the default of zero on successful fetch status

        controlScreenViewMvc.hideProgressBar();
        controlScreenViewMvc.showColorUI();

        Log.d("Response", response);
        int offset = response.indexOf(Constants.statePositionText);
        String stateValue = response.substring(offset+Constants.fetchStatusParseResponseOffsetStart,
                offset+Constants.fetchStatusParseResponseOffsetEnd);
        Log.d("State", stateValue);

        if(stateValue.equals(Constants.stateValueOn)){
            controlScreenViewMvc.bindTogglePowerButtonText(Constants.turnPowerOff);
        } else if (stateValue.equals(Constants.stateValueOff)){
            controlScreenViewMvc.bindTogglePowerButtonText(Constants.turnPowerOn);
        }
    }

    @Override
    public void onFetchStatusFailed(String error) {
        if (fetchStatusFailCount <= 2){
            fetchStatusUseCase.fetchStatusAndNotify();
            fetchStatusFailCount++;
            Log.i("fetch retry count: ", Integer.toString(fetchStatusFailCount));
        } else {
            toastHelper.showVolleyError(error);
        }
    }

    @Override
    public void onTogglePowerSuccess() {
        if (controlScreenViewMvc.getTogglePowerButtonText().equals(Constants.lightTurnedOnResponse)) {
            controlScreenViewMvc.bindTogglePowerButtonText(Constants.turnPowerOff);
        } else if (controlScreenViewMvc.getTogglePowerButtonText().equals(Constants.lightTurnedOffResponse)) {
            controlScreenViewMvc.bindTogglePowerButtonText(Constants.turnPowerOn);
        }
    }

    @Override
    public void onTogglePowerFailure(String error) {
        toastHelper.showVolleyError(error);
    }


}
