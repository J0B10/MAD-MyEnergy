package io.github.j0b10.mad.myenergy.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import io.github.j0b10.mad.myenergy.model.evcharger.EVChargerAPI;
import io.github.j0b10.mad.myenergy.model.evcharger.SessionManager;

public abstract class SessionAwareViewModel extends ViewModel {

    private boolean demoMode;

    public void loadProviders(Context context, boolean demoMode) {
        if (areProvidersLoaded() && demoMode == this.demoMode) {
            //provider already loaded, do nothing
            return;
        }
        if (demoMode) {
            loadDemoProviders();
        } else {
            SessionManager manager = SessionManager.getInstance(context);
            if (manager.isLoggedIn()) {
                loadProviders(manager.getAPI());
            } else {
                disposeProviders();
            }
        }
        this.demoMode = demoMode;
    }

    protected abstract boolean areProvidersLoaded();

    protected abstract void loadDemoProviders();

    protected abstract void loadProviders(EVChargerAPI api);

    protected abstract void disposeProviders();
}
