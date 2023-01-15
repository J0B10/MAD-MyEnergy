package io.github.j0b10.mad.myenergy.model.evcharger.adapter;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Response;

public class PostErrorsCallback<T> implements retrofit2.Callback<T> {

    private final MutableLiveData<Exception> error;
    private final Consumer<T> consumer;

    public PostErrorsCallback(MutableLiveData<Exception> error, Consumer<T> consumer) {
        this.error = error;
        this.consumer = consumer;
    }

    public PostErrorsCallback(MutableLiveData<Exception> error, Runnable runnable) {
        this(error, val -> runnable.run());
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        consumer.accept(response.body());
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        if (t instanceof Exception e) error.postValue(e);
        else error.postValue(new RuntimeException(t.getMessage(), t));
    }
}
