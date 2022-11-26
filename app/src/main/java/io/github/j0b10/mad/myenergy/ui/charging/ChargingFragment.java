package io.github.j0b10.mad.myenergy.ui.charging;

import static com.google.android.material.color.MaterialColors.getColor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.R.attr;
import com.leinardi.android.speeddial.SpeedDialActionItem;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.FragmentChargingBinding;
import io.github.j0b10.mad.myenergy.ui.charging.plan.ChargePlanActivity;

public class ChargingFragment extends Fragment {

    private FragmentChargingBinding binding;
    private ChargingViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ChargingViewModel.class);
        binding = FragmentChargingBinding.inflate(inflater, container, false);

        createSpeedDial();

        binding.chargemodeView.setUserInputEnabled(false);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (binding.chargeBattery.animateSecondaryCharge().isStarted()) {
            binding.chargeBattery.animateSecondaryCharge().resume();
        } else {
            binding.chargeBattery.animateSecondaryCharge().start();
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        binding.chargeBattery.animateSecondaryCharge().pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createSpeedDial() {
        binding.speedDial.addActionItem(
                new SpeedDialActionItem.Builder(R.id.sd_plan_charging, R.drawable.sd_plan)
                        .setLabel(R.string.sd_lbl_plan)
                        .setFabBackgroundColor(getColor(binding.speedDial, attr.colorPrimaryContainer))
                        .setFabImageTintColor(getColor(binding.speedDial, attr.colorOnPrimaryContainer))
                        .create()
                , 0);
        binding.speedDial.addActionItem(
                new SpeedDialActionItem.Builder(R.id.sd_excess_charging, R.drawable.sd_solar)
                        .setLabel(R.string.sd_lbl_excess)
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
        } else {
            binding.speedDial.close();
        }
        return false;
    }

}