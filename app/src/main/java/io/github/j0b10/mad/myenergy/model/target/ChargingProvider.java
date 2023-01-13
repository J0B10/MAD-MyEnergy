package io.github.j0b10.mad.myenergy.model.target;

public interface ChargingProvider extends Provider {

    void setOnUpdate(StatusConsumer consumer);

    interface StatusConsumer {
        void consumeStatus(boolean fastCharging, boolean charging, int whIn);
    }
}
