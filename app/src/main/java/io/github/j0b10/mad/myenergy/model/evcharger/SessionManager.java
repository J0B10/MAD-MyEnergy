package io.github.j0b10.mad.myenergy.model.evcharger;

import static io.github.j0b10.mad.myenergy.model.evcharger.authentication.AuthenticationGrantType.PASSWORD;
import static io.github.j0b10.mad.myenergy.model.evcharger.authentication.AuthenticationGrantType.REFRESH_TOKEN;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.github.j0b10.mad.myenergy.model.evcharger.authentication.AccountPreferences;
import io.github.j0b10.mad.myenergy.model.evcharger.authentication.AuthenticationGrantType;
import io.github.j0b10.mad.myenergy.model.evcharger.authentication.Token;
import io.github.j0b10.mad.myenergy.model.evcharger.gson.GsonInstantAdapter;
import io.github.j0b10.mad.myenergy.ui.login.LoginActivity;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionManager {

    private static final String ACCOUNT_PREFERENCE_NAME = "myenergy.account";
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new GsonInstantAdapter())
            .create();

    private static SessionManager instance;

    private final Context appContext;
    private final AccountPreferences accountPreferences;

    private EVChargerAuth auth;
    private EVChargerAPI api;

    private volatile String authToken;

    SessionManager(Context applicationContext) {
        this.appContext = applicationContext;
        this.accountPreferences = new AccountPreferences(appContext, ACCOUNT_PREFERENCE_NAME);
    }

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    private void createEndpoints(String ipAddress) {
        String baseUrl = "http://" + ipAddress;

        OkHttpClient okHttp = new OkHttpClient.Builder()
                .addInterceptor(new ErrorInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttp)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .build();
        auth = retrofit.create(EVChargerAuth.class);
        TokenAuthenticator authenticator = new TokenAuthenticator(this);
        OkHttpClient okHttpAuthenticated = okHttp.newBuilder()
                .addInterceptor(authenticator)
                .authenticator(authenticator)
                .build();
        api = retrofit.newBuilder().client(okHttpAuthenticated).build().create(EVChargerAPI.class);
    }

    public void requireLoginSync(Activity context) {
        if (isLoggedIn()) return;
        if (accountPreferences.isLoginStored()) {
            createEndpoints(accountPreferences.getIpAddress());

            StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(threadPolicy)
                    .permitNetwork().build());

            boolean success;
            try {
                success = loginSync(REFRESH_TOKEN)
                        || loginSync(PASSWORD);
                Log.i("login", "connected to " + accountPreferences.getIpAddress());
            } finally {
                StrictMode.setThreadPolicy(threadPolicy);
            }
            if (success) return;
        }
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    private boolean loginSync(AuthenticationGrantType grantType) {
        if (grantType == REFRESH_TOKEN && !accountPreferences.isRefreshTokenStored()) {
            return false;
        }
        if (grantType == PASSWORD
                && !(accountPreferences.isPasswordStored() && accountPreferences.isLoginStored())) {
            return false;
        }
        boolean success = false;
        try {
            Call<Token> call = switch (grantType) {
                case PASSWORD -> auth.login(PASSWORD, accountPreferences.getUsername(), accountPreferences.getPassword());
                case REFRESH_TOKEN -> auth.login(REFRESH_TOKEN, accountPreferences.getRefreshToken());
            };
            Response<Token> response = call.execute();
            Token token = response.body();
            success = response.isSuccessful() && token != null;
            if (success) {
                accountPreferences.putRefreshToken(token.refresh_token);
                synchronized (this) {
                    authToken = token.access_token;
                }
            }
        } catch (HttpException e) {
            Log.d("login", grantType + " login failed: " + e.getMessage());
        } catch (IOException | JsonParseException e) {
            Log.w("login", e);
        }
        return success;
    }

    public void login(String ip, String username, String password, boolean storePassword,
                      Runnable onSuccess, Consumer<Throwable> onError) {
        createEndpoints(ip);
        auth.login(PASSWORD, username, password)
                .enqueue(new CallbackAdapter<>(appContext, token -> {
                    accountPreferences.putCredentials(ip, username, storePassword ? password : null);
                    accountPreferences.putRefreshToken(token.refresh_token);
                    synchronized (this) {
                        authToken = token.access_token;
                    }
                    onSuccess.run();
                }, onError));
    }

    public synchronized boolean isLoggedIn() {
        return authToken != null;
    }

    public AccountPreferences getAccountPreferences() {
        return accountPreferences;
    }

    synchronized String getAuthToken() {
        return authToken;
    }

    String refreshAuthToken() {
        loginSync(REFRESH_TOKEN);
        return getAuthToken();
    }

    public EVChargerAPI getAPI() throws IllegalStateException {
        if (api == null) throw new IllegalStateException("not logged in");
        return api;
    }
}
