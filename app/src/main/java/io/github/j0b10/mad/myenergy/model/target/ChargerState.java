package io.github.j0b10.mad.myenergy.model.target;

public enum ChargerState {

    FAST_CHARGING,
    FAST_CHARGING_STOPPED,
    SMART_CHARGING,
    SMART_CHARGING_STOPPED,
    EXCESS_CHARGING,
    EXCESS_CHARGING_STOPPED,
    UNCONNECTED;

    public boolean isCharging() {
        return switch (this) {
            case FAST_CHARGING, SMART_CHARGING, EXCESS_CHARGING -> true;
            default -> false;
        };
    }

    public boolean isQuickCharge() {
        return switch (this) {
            case FAST_CHARGING, FAST_CHARGING_STOPPED -> true;
            default -> false;
        };
    }

    public boolean isConnected() {
        return this != UNCONNECTED;
    }
}
