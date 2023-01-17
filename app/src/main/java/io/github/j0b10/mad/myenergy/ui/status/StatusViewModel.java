package io.github.j0b10.mad.myenergy.ui.status;

import io.github.j0b10.mad.myenergy.model.demo.DemoStatusAdapter;
import io.github.j0b10.mad.myenergy.model.evcharger.EVChargerAPI;
import io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVStatusAdapter;
import io.github.j0b10.mad.myenergy.model.target.StatusProvider;
import io.github.j0b10.mad.myenergy.ui.viewmodel.SessionAwareViewModel;

public class StatusViewModel extends SessionAwareViewModel {
    private StatusProvider status;

    public StatusProvider status() {
        return status;
    }

    @Override
    protected boolean areProvidersLoaded() {
        return status != null;
    }

    @Override
    protected void loadDemoProviders() {
        status = DemoStatusAdapter.getInstance();
    }

    @Override
    protected void loadProviders(EVChargerAPI api) {
        status = new EVStatusAdapter(api);
    }

    @Override
    protected void disposeProviders() {
        status = null;
    }
}
