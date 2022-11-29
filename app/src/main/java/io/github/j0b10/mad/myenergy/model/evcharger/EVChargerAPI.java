package io.github.j0b10.mad.myenergy.model.evcharger;

import java.util.List;

import io.github.j0b10.mad.myenergy.model.evcharger.results.Measurement;
import retrofit2.Call;
import retrofit2.http.GET;

public interface EVChargerAPI {

    @GET("todo")
    Call<List<Measurement>> getLiveData();
}
