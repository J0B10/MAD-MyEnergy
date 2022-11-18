package io.github.j0b10.mad.myenergy.ui.status;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.github.j0b10.mad.myenergy.databinding.FragmentStatusBinding;

public class StatusFragment extends Fragment {

    private FragmentStatusBinding binding;

    private StatusViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        model = new ViewModelProvider(this).get(StatusViewModel.class);
        binding = FragmentStatusBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    /*
        EnergyFlowFragment homeFragment = binding.statusHomeView.getFragment();
        EnergyFlowFragment evFragment = binding.statusEvView.getFragment();
        EnergyFlowFragment pvFragment = binding.statusPvView.getFragment();
        homeModel = homeFragment.requireViewModel();
        evModel = evFragment.requireViewModel();
        pvModel = pvFragment.requireViewModel();

        model.gridFeedIn.observe(getViewLifecycleOwner(), this::onGridFeedInChange);
        model.homeConsumption.observe(getViewLifecycleOwner(), observePositiveFlowRate(homeModel, attr.colorError));
        model.evConsumption.observe(getViewLifecycleOwner(), observePositiveFlowRate(evModel, attr.colorPrimary));
        model.pvProduction.observe(getViewLifecycleOwner(), observePositiveFlowRate(pvModel, attr.colorSecondary));

        homeModel.flowDirection.postValue(EnergyFlowDirection.IN);
        evModel.flowDirection.postValue(EnergyFlowDirection.IN);

        //Test values
        model.pvProduction.postValue(4.43);
        model.evConsumption.postValue(3.333);
        model.homeConsumption.postValue(0.680);
        model.gridFeedIn.postValue(4.43 - 3.333 - 0.680);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
/*
    private void onGridFeedInChange(double newVal) {
        @ColorInt int colorPositive = MaterialColors.getColor(requireContext(), attr.colorSecondary, Color.GRAY);
        @ColorInt int colorNegative = MaterialColors.getColor(requireContext(), attr.colorError, Color.GRAY);
        @ColorInt int colorNone = MaterialColors.getColor(requireContext(), attr.colorOnSurfaceVariant, Color.GRAY);
        if (newVal < 0) {
            gridModel.flowText.postValue(String.format(Locale.getDefault(), "-%.2f kW", newVal));
            gridModel.color.postValue(colorNegative);
            gridModel.flowDirection.postValue(EnergyFlowDirection.OUT);
        } else {
            gridModel.flowText.postValue(String.format(Locale.getDefault(), "+%.2f kW", newVal));
            gridModel.flowDirection.postValue(EnergyFlowDirection.IN);
            if (newVal == 0) {
                gridModel.color.postValue(colorNone);
            } else {
                gridModel.color.postValue(colorPositive);
            }
        }
    }*/
/*
    private Observer<Double> observePositiveFlowRate(EnergyFlowViewModel model, @AttrRes int color) {
        return val -> {
            @ColorInt int colorPositive = MaterialColors.getColor(requireContext(), color, Color.GRAY);
            @ColorInt int colorNone = MaterialColors.getColor(requireContext(), attr.colorOnSurfaceVariant, Color.GRAY);
            model.flowText.postValue(String.format(Locale.getDefault(), "%.2f kW", val));
            if (val > 0) {
                model.color.postValue(colorPositive);
            } else {
                model.color.postValue(colorNone);
            }
        };
    }*/
}