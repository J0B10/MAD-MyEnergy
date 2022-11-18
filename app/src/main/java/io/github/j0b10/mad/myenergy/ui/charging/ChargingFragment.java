package io.github.j0b10.mad.myenergy.ui.charging;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
        binding.button4.setOnClickListener(this::onStartCharging);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onStartCharging(View view) {
        startActivity(new Intent(requireContext(), ChargePlanActivity.class));
    }

}