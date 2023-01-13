package io.github.j0b10.mad.myenergy.model.evcharger.values;

public interface ChannelId {
    interface Parameter {
        String CHARGE_MODE = "Parameter.Chrg.ActChaMod";
        String CHARGE_DURATION = "Parameter.Chrg.Plan.DurTmm";
        String CHARGE_ENERGY = "Parameter.Chrg.Plan.En";
    }

    interface Measurement {
        String GRID_W = "Measurement.Metering.PCCMs.PlntW";
        String GRID_W_CONSUMPTION = "Measurement.Metering.PCCMs.PlntCsmpW";
        String EV_W = "Measurement.Metering.GridMs.TotWIn.ChaSta";
    }
}
