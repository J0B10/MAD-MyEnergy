package io.github.j0b10.mad.myenergy.model.target;

import androidx.lifecycle.LiveData;

public interface StatusProvider extends Provider {
    LiveData<Double> getGridFeedIn();

    LiveData<Double> getHomeConsumption();

    LiveData<Double> getEvConsumption();

    LiveData<Double> getPvProduction();
}
