package io.github.j0b10.mad.myenergy.model.evcharger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Optional;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    private final SessionManager sessionManager;

    public TokenAuthenticator(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
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
            // Access token is refreshed in another thread.
            if (!token.equals(newToken)) {
                return newRequestWithAccessToken(response.request(), newToken);
            }

            // Need to refresh an access token
            final String updatedAccessToken = sessionManager.refreshAuthToken();
            return newRequestWithAccessToken(response.request(), updatedAccessToken);
        }
    }

    private boolean isRequestWithAccessToken(@NonNull Response response) {
        String header = response.request().header("Authorization");
        return header != null && header.startsWith("Bearer");
    }

    @NonNull
    private Request newRequestWithAccessToken(@NonNull Request request, @NonNull String accessToken) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }
}