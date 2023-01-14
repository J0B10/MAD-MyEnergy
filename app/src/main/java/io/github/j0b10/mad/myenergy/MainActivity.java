package io.github.j0b10.mad.myenergy;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import io.github.j0b10.mad.myenergy.databinding.ActivityMainBinding;
import io.github.j0b10.mad.myenergy.model.evcharger.SessionManager;
import io.github.j0b10.mad.myenergy.ui.settings.PreferencesFragment;

/**
 * Main Activity.
 * Contains a bottom nav bar for navigation and a fragment container that displays
 * status info / charging info / history / settings.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean demoMode = preferences.getBoolean(PreferencesFragment.KEY_DEMO, false);

        if (!demoMode) {
            SessionManager.getInstance(this).requireLoginSync(this);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_status, R.id.navigation_charging, R.id.navigation_history, R.id.navigation_settings)
                .build();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        if (fragment instanceof NavHostFragment navFragment) {
            nav = navFragment.getNavController();
            nav.setGraph(R.navigation.mobile_navigation);
            NavigationUI.setupActionBarWithNavController(this, nav, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, nav);
        } else {
            throw new IllegalStateException("no nav host fragment: " + fragment);
        }

        if (inLandscapeMode()) {
            binding.navView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean res = super.onCreateOptionsMenu(menu);
        if (!inLandscapeMode()) return res;
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        nav.navigate(item.getItemId());
        return true;
    }

    private boolean inLandscapeMode() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

}