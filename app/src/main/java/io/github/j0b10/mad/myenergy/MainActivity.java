package io.github.j0b10.mad.myenergy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import io.github.j0b10.mad.myenergy.databinding.ActivityMainBinding;

/**
 * Main Activity.
 * Contains a bottom nav bar for navigation and a fragment container that displays
 * status info / charging info / history / settings.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyEnergyApp) getApplication()).requireLogin(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_status, R.id.navigation_charging, R.id.navigation_history, R.id.navigation_settings)
                .build();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        if (fragment instanceof NavHostFragment navFragment) {
            NavController navController = navFragment.getNavController();
            navController.setGraph(R.navigation.mobile_navigation);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);
        } else {
            throw new IllegalStateException("no nav host fragment: " + fragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}