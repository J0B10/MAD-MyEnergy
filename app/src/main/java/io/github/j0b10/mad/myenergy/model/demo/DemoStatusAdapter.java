package io.github.j0b10.mad.myenergy.model.demo;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import androidx.lifecycle.MutableLiveData;

import java.util.Random;

import io.github.j0b10.mad.myenergy.model.target.BaseProvider;
import io.github.j0b10.mad.myenergy.model.target.StatusProvider;

public class DemoStatusAdapter extends BaseProvider implements StatusProvider {


    private static DemoStatusAdapter instance;
    private final Random random = new Random();
    private final MutableLiveData<Double>
            gridFeedIn = new MutableLiveData<>(0.0),
            homeConsumption = new MutableLiveData<>(0.0),
            evConsumption = new MutableLiveData<>(0.0),
            pvProduction = new MutableLiveData<>(0.0);

    private DemoStatusAdapter() {
    }

    public static DemoStatusAdapter getInstance() {
        if (instance == null) instance = new DemoStatusAdapter();
        return instance;
    }

    @Override
    protected void update() {
        double home = nextDouble(0, 2.5);
        double ev = nextDouble(1.3, 11);
        double pv = nextDouble(0, 9.6);
        double grid = pv - home - ev;
        gridFeedIn.postValue(grid);
        homeConsumption.postValue(home);
        evConsumption.postValue(ev);
        pvProduction.postValue(pv);
    }

    private double nextDouble(double min, double max) {
        return min(min, max) + random.nextDouble() * abs(max - min);
    }

    @Override
    public MutableLiveData<Double> gridFeedIn() {
        return gridFeedIn;
    }

    @Override
    public MutableLiveData<Double> homeConsumption() {
        return homeConsumption;
    }

    @Override
    public MutableLiveData<Double> evConsumption() {
        return evConsumption;
    }

    @Override
    public MutableLiveData<Double> pvProduction() {
        return pvProduction;
    }
}
