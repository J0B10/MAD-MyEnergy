package io.github.j0b10.mad.myenergy.model.evcharger;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallbackAdapter<T> implements Callback<T> {

    private final Handler handler;
    private final Consumer<T> onSuccess;
    private final Consumer<Throwable> onFailure;

    public CallbackAdapter(@NonNull Handler handler, @Nullable Consumer<T> onSuccess, @Nullable Consumer<Throwable> onFailure) {
        this.handler = handler;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    public CallbackAdapter(@NonNull Context context, @Nullable Consumer<T> onSuccess, @Nullable Consumer<Throwable> onFailure) {
        this(new Handler(context.getMainLooper()), onSuccess, onFailure);
    }

    public CallbackAdapter(@NonNull Context context, @Nullable Consumer<T> onSuccess) {
        this(context, onSuccess, null);
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (onSuccess != null) handler.post(() -> onSuccess.accept(response.body()));
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        if (onFailure != null) handler.post(() -> onFailure.accept(t));
    }
}
