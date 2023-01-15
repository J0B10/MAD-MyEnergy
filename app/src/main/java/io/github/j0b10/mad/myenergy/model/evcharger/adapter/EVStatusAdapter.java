package io.github.j0b10.mad.myenergy.model.evcharger.adapter;

import static java.lang.Math.max;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.j0b10.mad.myenergy.model.evcharger.EVChargerAPI;
import io.github.j0b10.mad.myenergy.model.evcharger.dataQueries.SearchQueryItem;
import io.github.j0b10.mad.myenergy.model.evcharger.measurements.Measurement;
import io.github.j0b10.mad.myenergy.model.evcharger.values.ChannelId;
import io.github.j0b10.mad.myenergy.model.evcharger.values.ComponentId;
import io.github.j0b10.mad.myenergy.model.target.BaseProvider;
import io.github.j0b10.mad.myenergy.model.target.StatusProvider;
import retrofit2.Response;

public class EVStatusAdapter extends BaseProvider implements StatusProvider {

    private final MutableLiveData<Double>
            gridFeedIn = new MutableLiveData<>(0.0),
            homeConsumption = new MutableLiveData<>(0.0),
            evConsumption = new MutableLiveData<>(0.0),
            pvProduction = new MutableLiveData<>(0.0);

    private final EVChargerAPI api;

    public EVStatusAdapter(EVChargerAPI api) {
        this.api = api;
    }

    static <T> Map<String, T> toMap(List<T> list, Function<T, String> mapper) {
        return list.stream().collect(Collectors.toMap(mapper, Function.identity()));
    }

    @Override
    protected void update() throws IOException {
        Response<List<Measurement>> response = api
                .getLiveData(List.of(new SearchQueryItem(ComponentId.PLANT, null)))
                .execute();
        Map<String, Measurement> measurements =
                toMap(Objects.requireNonNull(response.body()), m -> m.channelId);
        Measurement gridFeedIn = measurements.get(ChannelId.Measurement.GRID_W);
        Measurement gridFeedOut = measurements.get(ChannelId.Measurement.GRID_W_CONSUMPTION);
        Measurement evConsumption = measurements.get(ChannelId.Measurement.EV_W);
        double gridIn = Optional.ofNullable(gridFeedIn).map(m -> m.values.get(0).value).orElse(0.0);
        double gridOut = Optional.ofNullable(gridFeedOut).map(m -> m.values.get(0).value).orElse(0.0);
        double ev = Optional.ofNullable(evConsumption).map(m -> m.values.get(0).value).orElse(0.0);
        if (gridIn > gridOut) this.gridFeedIn.postValue(gridIn / 1000);
        else this.gridFeedIn.postValue(-gridOut / 1000);
        this.evConsumption.postValue(ev / 1000);
        this.homeConsumption.postValue(max(0, gridOut - ev) / 1000);
    }

    @Override
    public LiveData<Double> gridFeedIn() {
        return gridFeedIn;
    }

    @Override
    public LiveData<Double> homeConsumption() {
        return homeConsumption;
    }

    @Override
    public LiveData<Double> evConsumption() {
        return evConsumption;
    }

    @Override
    public LiveData<Double> pvProduction() {
        return pvProduction;
    }
}
