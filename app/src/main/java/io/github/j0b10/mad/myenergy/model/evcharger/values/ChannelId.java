package io.github.j0b10.mad.myenergy.model.evcharger.values;

public interface ChannelId {
    interface Parameter {
        String CHARGE_MODE = "Parameter.Chrg.ActChaMod";
        String CHARGE_DURATION = "Parameter.Chrg.Plan.DurTmm";
        String CHARGE_ENERGY = "Parameter.Chrg.Plan.En";

        String EV_CHARGE_END_TM = "Parameter.Chrg.Plan.StopTm";
    }

    interface Measurement {
        String GRID_W = "Measurement.Metering.PCCMs.PlntW";
        String GRID_W_CONSUMPTION = "Measurement.Metering.PCCMs.PlntCsmpW";
        String EV_W = "Measurement.Metering.GridMs.TotWIn.ChaSta";

        String EV_CHARGE_WH = "Measurement.ChaSess.WhIn";

        String EV_MODE_HW_SWITCH = "Measurement.Chrg.ModSw";
    }
}
