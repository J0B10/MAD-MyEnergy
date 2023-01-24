package io.github.j0b10.mad.myenergy.ui.charging;

import io.github.j0b10.mad.myenergy.model.demo.DemoChargingAdapter;
import io.github.j0b10.mad.myenergy.model.evcharger.EVChargerAPI;
import io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVChargeControlAdapter;
import io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVChargeInfoAdapter;
import io.github.j0b10.mad.myenergy.model.target.ChargeControls;
import io.github.j0b10.mad.myenergy.model.target.ChargeInfoProvider;
import io.github.j0b10.mad.myenergy.ui.viewmodel.SessionAwareViewModel;

public class ChargingViewModel extends SessionAwareViewModel {
    private ChargeInfoProvider chargeInfo;
    private ChargeControls chargeControls;

    boolean showChargeLimPrompt = true;

    public ChargeInfoProvider info() {
        return chargeInfo;
    }

    public ChargeControls controls() {
        return chargeControls;
    }

    @Override
    protected boolean areProvidersLoaded() {
        return chargeInfo != null && chargeControls != null;
    }

    @Override
    protected void loadDemoProviders() {
        DemoChargingAdapter adapter = DemoChargingAdapter.getInstance();
        chargeInfo = adapter;
        chargeControls = adapter;
    }

    @Override
    protected void loadProviders(EVChargerAPI api) {
        chargeInfo = new EVChargeInfoAdapter(api);
        chargeControls = new EVChargeControlAdapter(api);
    }

    @Override
    protected void disposeProviders() {
        chargeInfo = null;
        chargeControls = null;
    }
}
