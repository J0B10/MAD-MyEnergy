package io.github.j0b10.mad.myenergy.model.evcharger;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ErrorInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.isSuccessful()) {
            return response;
        } else {
            throw new HttpException(response.code(), response.message());
        }
    }
}
