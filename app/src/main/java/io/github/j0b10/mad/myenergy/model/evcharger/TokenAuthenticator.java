package io.github.j0b10.mad.myenergy.model.evcharger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator, Interceptor {

    private final SessionManager sessionManager;

    public TokenAuthenticator(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (isRequestWithAccessToken(request)) {
            return chain.proceed(request);
        } else {
            final String token = sessionManager.getAuthToken();
            Request authRequest = newRequestWithAccessToken(chain.request(), token);
            return chain.proceed(authRequest);
        }
    }

    @Nullable
    @Override
    public Request authenticate(Route route, Response response) {
        final String token = sessionManager.getAuthToken();
        if (token == null) {
            return null;
        }
        synchronized (this) {
            final String newToken = sessionManager.getAuthToken();

            if (isRequestWithAccessToken(response.request())) {
                //Request already had token, refresh it.

                // Access token is refreshed in another thread.
                if (!token.equals(newToken)) {
                    return newRequestWithAccessToken(response.request(), newToken);
                }

                // Need to refresh an access token
                final String updatedAccessToken = sessionManager.refreshAuthToken();
                return newRequestWithAccessToken(response.request(), updatedAccessToken);
            } else {
                // Request without token, add one
                return newRequestWithAccessToken(response.request(), newToken);
            }
        }
    }

    private boolean isRequestWithAccessToken(@NonNull Request request) {
        String header = request.header("Authorization");
        return header != null && header.startsWith("Bearer");
    }

    @NonNull
    private Request newRequestWithAccessToken(@NonNull Request request, @NonNull String accessToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }
}