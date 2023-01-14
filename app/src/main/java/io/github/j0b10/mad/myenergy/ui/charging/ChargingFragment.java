package io.github.j0b10.mad.myenergy.ui.charging;

import static com.google.android.material.color.MaterialColors.getColor;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.EXCESS_CHARGING;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.FAST_CHARGING;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.preference.PreferenceManager;

import com.google.android.material.R.attr;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.Snackbar;
import com.leinardi.android.speeddial.SpeedDialActionItem;

import java.time.Duration;
import java.util.Locale;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.FragmentChargingBinding;
import io.github.j0b10.mad.myenergy.model.demo.DemoChargingAdapter;
import io.github.j0b10.mad.myenergy.model.evcharger.SessionManager;
import io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVChargeControlAdapter;
import io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVChargeInfoAdapter;
import io.github.j0b10.mad.myenergy.model.target.ChargeControls;
import io.github.j0b10.mad.myenergy.model.target.ChargeInfoProvider;
import io.github.j0b10.mad.myenergy.model.target.ChargerState;
import io.github.j0b10.mad.myenergy.ui.charging.plan.ChargePlanActivity;
import io.github.j0b10.mad.myenergy.ui.settings.PreferencesFragment;

public class ChargingFragment extends Fragment {

    private FragmentChargingBinding binding;
    private ChargeInfoProvider chargeInfo;
    private ChargeControls chargeControls;
    private ValueAnimator batteryAnimation;
    private Snackbar errorMsg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChargingBinding.inflate(inflater, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean demoMode = preferences.getBoolean(PreferencesFragment.KEY_DEMO, false);
        String fetchRateS = preferences.getString(PreferencesFragment.KEY_FETCH_RATE, "5.0");
        Duration fetchInterval = Duration.ofMillis((long) (Float.parseFloat(fetchRateS) * 1000L));

        if (demoMode) {
            chargeInfo = DemoChargingAdapter.getInstance();
            chargeControls = DemoChargingAdapter.getInstance();
            chargeInfo.configureInterval(fetchInterval);
        } else {
            SessionManager sessionManager = SessionManager.getInstance(requireContext());
            if (sessionManager.isLoggedIn()) {
                chargeInfo = new EVChargeInfoAdapter(sessionManager.getAPI());
                chargeControls = new EVChargeControlAdapter(sessionManager.getAPI());
                chargeInfo.configureInterval(fetchInterval);
            } else {
                chargeInfo = null;
                chargeControls = null;
            }
        }

        batteryAnimation = binding.chargeBattery.animateSecondaryCharge();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSpeedDial();
        binding.fabStartFast.setOnClickListener(this::onFabClicked);
        binding.fabStopFast.setOnClickListener(this::onFabClicked);

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        if (chargeInfo != null) {
            chargeInfo.chargerState().observe(lifecycleOwner, this::onChargerStateChange);
            chargeInfo.evConsumption().observe(lifecycleOwner,
                    binding.energyFlowView.observePositiveFlowRate(attr.colorPrimary));
            chargeInfo.charge().observe(lifecycleOwner, this::onCharge);
            chargeInfo.error().observe(lifecycleOwner, this::onError);
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        if (chargeInfo == null) return;

        chargeInfo.start();

        ChargerState state = chargeInfo.chargerState().getValue();
        if (state != null && state.isCharging()) {
            if (batteryAnimation.isStarted()) batteryAnimation.resume();
            else batteryAnimation.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (chargeInfo != null) chargeInfo.stop();

        if (batteryAnimation.isRunning()) {
            batteryAnimation.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        batteryAnimation = null;
        binding = null;
    }

    private void onCharge(double charge) {
        Double goal = chargeInfo.goal().getValue();
        String text;
        if (goal == null) {
            text = String.format(Locale.getDefault(), "%.1f kWh", charge);
        } else {
            text = String.format(Locale.getDefault(), "%.1f / %.1f kWh", charge, goal);
        }
        binding.chargeInfo.setText(text);
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private void onChargerStateChange(ChargerState chargerState) {
        if (chargerState.isCharging()) {
            if (batteryAnimation.isStarted()) batteryAnimation.resume();
            else batteryAnimation.start();
        } else {
            if (batteryAnimation.isRunning()) {
                batteryAnimation.pause();
                binding.chargeBattery.setChargeSecondary(0.0f);
            }
        }

        binding.chargeMode.setText(switch (chargerState) {
            case FAST_CHARGING -> R.string.mode_fast;
            case SMART_CHARGING -> R.string.mode_plan;
            case EXCESS_CHARGING -> R.string.mode_excess;
            case UNCONNECTED -> R.string.mode_disconnect;
            default -> R.string.mode_stop;
        });
        binding.description.setText(switch (chargerState) {
            case FAST_CHARGING -> R.string.desc_fast;
            case SMART_CHARGING -> R.string.desc_plan;
            case EXCESS_CHARGING -> R.string.desc_excess;
            case UNCONNECTED -> R.string.desc_disconnect;
            default -> R.string.desc_stop;
        });
        binding.extraInfo.setText(switch (chargerState) {
            case SMART_CHARGING -> getString(R.string.cp_title_until) + " hh:mm";
            default -> "";
        });

        binding.fabStartFast.setVisibility(
                showIf(chargerState == ChargerState.FAST_CHARGING_STOPPED));
        binding.fabStopFast.setVisibility(
                showIf(chargerState == ChargerState.FAST_CHARGING));
        binding.speedDial.setVisibility(
                showIf(!chargerState.isQuickCharge()));
    }

    private void onError(@Nullable Exception e) {
        if (e == null) {
            if (errorMsg != null) errorMsg.dismiss();
            return;
        }
        binding.cpProgress.hide();
        View root = requireView();
        String msg = getString(R.string.error_charge_info, e.getMessage());
        errorMsg = Snackbar.make(root, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(MaterialColors.getColor(root, attr.colorError))
                .setTextColor(MaterialColors.getColor(root, attr.colorOnError))
                .setAnchorView(binding.speedDial);
        errorMsg.show();
        Log.w("Status", e);
    }

    private void setupSpeedDial() {
        binding.speedDial.addActionItem(
                new SpeedDialActionItem.Builder(R.id.sd_plan_charging, R.drawable.sd_plan)
                        .setLabel(R.string.mode_plan)
                        .setFabBackgroundColor(getColor(binding.speedDial, attr.colorPrimaryContainer))
                        .setFabImageTintColor(getColor(binding.speedDial, attr.colorOnPrimaryContainer))
                        .create()
                , 0);
        binding.speedDial.addActionItem(
                new SpeedDialActionItem.Builder(R.id.sd_excess_charging, R.drawable.sd_solar)
                        .setLabel(R.string.mode_excess)
                        .setFabBackgroundColor(getColor(binding.speedDial, attr.colorTertiaryContainer))
                        .setFabImageTintColor(getColor(binding.speedDial, attr.colorOnTertiaryContainer))
                        .create()
                , 1);
        binding.speedDial.addActionItem(
                new SpeedDialActionItem.Builder(R.id.sd_stop_charging, R.drawable.sd_stop)
                        .setLabel(R.string.sd_lbl_stop)
                        .setFabBackgroundColor(getColor(binding.speedDial, attr.colorErrorContainer))
                        .setFabImageTintColor(getColor(binding.speedDial, attr.colorOnErrorContainer))
                        .create()
                , 2);
        binding.overlay.setOnClickListener(v -> binding.speedDial.close(true));
        binding.speedDial.setOnActionSelectedListener(this::onActionSelected);
    }

    private boolean onActionSelected(SpeedDialActionItem selected) {
        if (selected.getId() == R.id.sd_plan_charging) {
            startActivity(new Intent(requireContext(), ChargePlanActivity.class));
        } else if (selected.getId() == R.id.sd_excess_charging && chargeControls != null) {
            chargeControls.startCharging(EXCESS_CHARGING, () -> {

            });
        } else if (selected.getId() == R.id.sd_stop_charging && chargeControls != null) {
            chargeControls.stopCharging(() -> {

            });
        }
        binding.speedDial.close();
        return false;
    }

    private void onFabClicked(View fab) {
        if (fab.getId() == R.id.fab_start_fast) {
            binding.fabStartFast.setVisibility(View.INVISIBLE);
            binding.cpProgress.show();
            chargeControls.startCharging(FAST_CHARGING, () -> {
                binding.cpProgress.hide();
            });
        } else if (fab.getId() == R.id.fab_stop_fast) {
            binding.fabStopFast.setVisibility(View.INVISIBLE);
            binding.cpProgress.show();
            chargeControls.stopCharging(() -> {
                binding.cpProgress.hide();
            });
        }
    }

    private static int showIf(boolean value) {
        if (value) return View.VISIBLE;
        else return View.INVISIBLE;
    }

}