package io.github.j0b10.mad.myenergy.model.target;

import androidx.lifecycle.LiveData;

public interface PVStatusProvider extends Provider {

    LiveData<Double> pvProduction();
}
