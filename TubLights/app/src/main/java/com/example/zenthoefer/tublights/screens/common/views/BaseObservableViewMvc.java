package com.example.zenthoefer.tublights.screens.common.views;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BaseObservableViewMvc<ListenerType> extends BaseViewMvc
        implements ObservableViewMvc<ListenerType>{

    private Set<ListenerType> listeners = new HashSet<>();

    @Override
    public void registerListener(ListenerType listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterListener(ListenerType listener) {
        listeners.remove(listener);
    }

    protected final Set<ListenerType> getListeners() {
        return Collections.unmodifiableSet(listeners);
    }
}
