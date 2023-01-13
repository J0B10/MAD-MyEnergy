package io.github.j0b10.mad.myenergy.model.evcharger;

import java.util.List;

import io.github.j0b10.mad.myenergy.model.evcharger.dataQueries.SearchQueryItem;
import io.github.j0b10.mad.myenergy.model.evcharger.measurements.Measurement;
import io.github.j0b10.mad.myenergy.model.evcharger.parameters.ParameterPutQuery;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface EVChargerAPI {

    @POST("/api/v1/measurements/live/")
    Call<List<Measurement>> getLiveData(@Body List<SearchQueryItem> plant);

    @PUT("/api/v1/parameters/IGULD:SELF")
    Call<Void> setParameters(@Body ParameterPutQuery parameters);
}
