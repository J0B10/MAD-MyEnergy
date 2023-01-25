package io.github.j0b10.mad.myenergy.model.pv.tripower;

import androidx.annotation.NonNull;

import io.github.j0b10.mad.myenergy.model.pv.PVInverter;
import io.github.j0b10.mad.myenergy.model.pv.PVInverterFactory;

public class TripowerPVInverter implements PVInverter {

    private static final String MODEL = "tripower";

    private static final int PORT = 502;

    private static final int UNIT_ID = 3;

    private final String ip;
    private final String name;

    public TripowerPVInverter(String ip, String name) {
        this.ip = ip;
        this.name = name;
    }

    @Override
    public String getModel() {
        return MODEL;
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPort() {
        return PORT;
    }

    public int getUnitId() {
        return UNIT_ID;
    }

    @NonNull
    @Override
    public String toString() {
        return '{' + MODEL + '\\' + name + '\\' + ip + '}';
    }

    public static class Factory implements PVInverterFactory<TripowerPVInverter> {

        @Override
        public TripowerPVInverter deserialize(String string) {
            String[] split = string.split("\0");
            if (split.length != 3) throw new IllegalArgumentException(string);
            if (!MODEL.equalsIgnoreCase(split[0])) throw new IllegalArgumentException(string);
            return new TripowerPVInverter(split[1], split[2]);
        }

        @Override
        public String serialize(TripowerPVInverter pvInverter) {
            return pvInverter.getModel() + '\0' + pvInverter.getName() + '\0' + pvInverter.getIP();
        }
    }
}
