package io.github.j0b10.mad.myenergy.ui.charging;

import static androidx.lifecycle.Transformations.distinctUntilChanged;
import static androidx.lifecycle.Transformations.map;
import static com.google.android.material.color.MaterialColors.getColor;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.EXCESS_CHARGING;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.FAST_CHARGING;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.SMART_CHARGING;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.UNCONNECTED;
import static io.github.j0b10.mad.myenergy.ui.charging.plan.ChargePlanActivity.TIME_FORMAT;
import static io.github.j0b10.mad.myenergy.ui.settings.PreferencesFragment.KEY_QUICK_CHARGE_SPEED_ENABLED;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.R.attr;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.leinardi.android.speeddial.SpeedDialActionItem;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.FragmentChargingBinding;
import io.github.j0b10.mad.myenergy.model.target.ChargerState;
import io.github.j0b10.mad.myenergy.ui.charging.plan.ChargePlanActivity;
import io.github.j0b10.mad.myenergy.ui.settings.PreferencesFragment;

public class ChargingFragment extends Fragment implements FragmentResultListener {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy hh:mm");

    private FragmentChargingBinding binding;
    private ChargingViewModel model;
    private ValueAnimator batteryAnimation;
    private Snackbar errorMsg;
    private AlertDialog resetChargeLimPrompt;
    private boolean canSetQCSpeed;
    private int maxChargeAmp;

    public static int showIf(boolean value) {
        if (value) return View.VISIBLE;
        else return View.INVISIBLE;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChargingBinding.inflate(inflater, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean demoMode = preferences.getBoolean(PreferencesFragment.KEY_DEMO, false);
        String fetchRateS = preferences.getString(PreferencesFragment.KEY_FETCH_RATE, "5.0");
        String maxChargeS = preferences.getString(PreferencesFragment.KEY_QUICK_CHARGE_MAX_CURRENT, "16");
        canSetQCSpeed = preferences.getBoolean(KEY_QUICK_CHARGE_SPEED_ENABLED, false);
        maxChargeAmp = Integer.parseInt(maxChargeS);
        Duration fetchInterval = Duration.ofMillis((long) (Float.parseFloat(fetchRateS) * 1000L));

        model = new ViewModelProvider(this).get(ChargingViewModel.class);
        model.loadProviders(requireContext(), demoMode);
        if (model.info() != null) model.info().configureInterval(fetchInterval);

        batteryAnimation = binding.chargeBattery.animateSecondaryCharge();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSpeedDial();

        if (model.info() != null) {
            binding.fabStartFast.setOnClickListener(this::onFabClicked);
            binding.fabStopFast.setOnClickListener(this::onFabClicked);
            if (canSetQCSpeed)
                binding.energyFlowView.setOnLongClickListener(this::onChargeRatePressed);

            LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

            distinctUntilChanged(model.info().chargerState())
                    .observe(lifecycleOwner, this::onChargerStateChange);
            distinctUntilChanged(model.info().chargerState())
                    .observe(lifecycleOwner, state
                            -> onEndTimeChange(model.info().planEndTime().getValue(), state));
            if (canSetQCSpeed) {
                map(model.info().chargerState(), ChargerState::isQuickCharge)
                        .observe(lifecycleOwner, qc -> {
                            binding.energyFlowView.setClickable(qc);
                            binding.energyFlowView.setLongClickable(qc);
                        });
            }
            distinctUntilChanged(model.info().planEndTime())
                    .observe(lifecycleOwner, time
                            -> onEndTimeChange(time, model.info().chargerState().getValue()));
            model.info().evConsumption()
                    .observe(lifecycleOwner,
                            binding.energyFlowView.observePositiveFlowRate(attr.colorPrimary));
            model.info().evConsumption().observe(lifecycleOwner, this::onChargeRateUpdate);
            model.info().charge().observe(lifecycleOwner, this::onCharge);
            model.info().error().observe(lifecycleOwner, this::onError);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (model.info() == null) return;

        model.info().start();

        ChargerState state = model.info().chargerState().getValue();
        if (state != null && state.isCharging()) {
            if (batteryAnimation.isStarted()) batteryAnimation.resume();
            else batteryAnimation.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (model.info() != null) model.info().stop();

        if (batteryAnimation.isRunning()) {
            batteryAnimation.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        batteryAnimation = null;
        binding = null;
        errorMsg = null;
        model = null;
        resetChargeLimPrompt = null;
    }

    private void onCharge(double charge) {
        ChargerState state = model.info().chargerState().getValue();
        Double goal = model.info().goal().getValue();
        String text;
        if (goal == null || state != SMART_CHARGING) {
            text = String.format(Locale.getDefault(), "%.1f kWh", charge);
        } else {
            text = String.format(Locale.getDefault(), "%.1f / %.1f kWh", charge, goal);
        }
        binding.chargeInfo.setText(text);
    }

    private void onChargeRateUpdate(double evConsumption) {
        if (evConsumption > 0) {
            if (batteryAnimation.isStarted()) batteryAnimation.resume();
            else batteryAnimation.start();
        } else {
            if (batteryAnimation.isRunning()) {
                batteryAnimation.pause();
                binding.chargeBattery.setChargeSecondary(0.0f);
            }
        }
    }

    private void onChargerStateChange(ChargerState chargerState) {
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

        binding.fabStartFast.setVisibility(
                showIf(chargerState == ChargerState.FAST_CHARGING_STOPPED));
        binding.fabStopFast.setVisibility(
                showIf(chargerState == ChargerState.FAST_CHARGING));
        binding.speedDial.setVisibility(
                showIf(!chargerState.isQuickCharge() && chargerState != UNCONNECTED));

        int chargeAlim = Objects.requireNonNull(model.info().chargeAlim().getValue());
        if (canSetQCSpeed && model.showChargeLimPrompt
                && !chargerState.isQuickCharge() && maxChargeAmp != chargeAlim) {
            showResetChargeLimPrompt();
        }
    }

    private void onEndTimeChange(LocalDateTime endTime, ChargerState state) {
        String text, time;
        if (state == SMART_CHARGING) {
            if (endTime == null) {
                time = null;
            } else if (endTime.toLocalDate().isEqual(LocalDate.now())) {
                time = TIME_FORMAT.format(endTime);
            } else {
                time = DATE_TIME_FORMAT.format(endTime);
            }
            text = getString(R.string.cp_title_until) + " " + time;
        } else {
            text = "";
        }
        binding.extraInfo.setText(text);
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

    private boolean onActionSelected(SpeedDialActionItem selected) {
        if (selected.getId() == R.id.sd_plan_charging) {
            startActivity(new Intent(requireContext(), ChargePlanActivity.class));
        } else if (selected.getId() == R.id.sd_excess_charging && model.controls() != null) {
            binding.cpProgress.show();
            model.controls().startCharging(EXCESS_CHARGING, onUiThread(() -> {
                binding.cpProgress.hide();
                model.info().requestUpdateNow();
            }));
        } else if (selected.getId() == R.id.sd_stop_charging && model.controls() != null) {
            binding.cpProgress.show();
            model.controls().stopCharging(onUiThread(() -> {
                binding.cpProgress.hide();
                model.info().requestUpdateNow();
            }));
        }
        binding.speedDial.close();
        return false;
    }

    private void onFabClicked(View fab) {
        if (fab.getId() == R.id.fab_start_fast) {
            binding.fabStartFast.setVisibility(View.INVISIBLE);
            binding.cpProgress.show();
            model.controls().startCharging(FAST_CHARGING, onUiThread(() -> {
                binding.cpProgress.hide();
                model.info().requestUpdateNow();
            }));
        } else if (fab.getId() == R.id.fab_stop_fast) {
            binding.fabStopFast.setVisibility(View.INVISIBLE);
            binding.cpProgress.show();
            model.controls().stopCharging(onUiThread(() -> {
                binding.cpProgress.hide();
                model.info().requestUpdateNow();
            }));
        }
    }

    private boolean onChargeRatePressed(View energyFlowView) {
        ChargerState state = Optional.ofNullable(model.info().chargerState().getValue())
                .orElse(UNCONNECTED);
        if (!state.isQuickCharge()) return false;

        Bundle bundle = new Bundle();
        bundle.putInt("val", Objects.requireNonNull(model.info().chargeAlim().getValue()));
        ChargeSpeedDialogFragment dialogFragment = new ChargeSpeedDialogFragment();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getChildFragmentManager(), ChargeSpeedDialogFragment.TAG);
        dialogFragment.getParentFragmentManager().setFragmentResultListener(
                ChargeSpeedDialogFragment.TAG, getViewLifecycleOwner(), this);
        return true;
    }

    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        int value = result.getInt("val", -1);
        if (value == -1) throw new IllegalArgumentException("value not set");
        binding.cpProgress.show();
        model.controls().setChargeALim(value, binding.cpProgress::hide);
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

    private void showResetChargeLimPrompt() {
        int chargeAlim = Objects.requireNonNull(model.info().chargeAlim().getValue());
        String message = getString(R.string.desc_quick_charge_speed_reset, chargeAlim, maxChargeAmp);
        if (resetChargeLimPrompt != null) {
            resetChargeLimPrompt.setMessage(message);
        } else {
            resetChargeLimPrompt = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.title_quick_charge_speed_reset)
                    .setMessage(message)
                    .setPositiveButton(R.string.btn_apply, (d, i) -> {
                        binding.cpProgress.show();
                        model.controls().setChargeALim(this.maxChargeAmp, binding.cpProgress::hide);
                        resetChargeLimPrompt.dismiss();
                        resetChargeLimPrompt = null;
                    })
                    .setNegativeButton(R.string.btn_cancel, (d, i) -> {
                        model.showChargeLimPrompt = false;
                        resetChargeLimPrompt.cancel();
                        resetChargeLimPrompt = null;
                    })
                    .create();
            resetChargeLimPrompt.show();
        }
    }

    private Runnable onUiThread(Runnable runnable) {
        return () -> requireActivity().runOnUiThread(runnable);
    }

}