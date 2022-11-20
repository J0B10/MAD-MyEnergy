package io.github.j0b10.mad.myenergy.ui.status;


import static com.google.android.material.R.attr;
import static com.google.android.material.color.MaterialColors.getColor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.github.j0b10.mad.myenergy.databinding.FragmentStatusBinding;
import io.github.j0b10.mad.myenergy.ui.views.EnergyFlowView;
import io.github.j0b10.mad.myenergy.ui.views.EnergyFlowView.Direction;

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

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        model.gridFeedIn.observe(lifecycleOwner, this::onGridFeedInChange);
        model.homeConsumption.observe(lifecycleOwner,
                observePositiveFlowRate(binding.statusHomeView, attr.colorError));
        model.evConsumption.observe(lifecycleOwner,
                observePositiveFlowRate(binding.statusEvView, attr.colorPrimary));
        model.pvProduction.observe(lifecycleOwner,
                observePositiveFlowRate(binding.statusPvView, attr.colorSecondary));

        //Test values
        model.pvProduction.postValue(4.43);
        model.evConsumption.postValue(10.5);
        model.homeConsumption.postValue(0.680);
        model.gridFeedIn.postValue(4.43 - 10.5 - 0.680);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void onGridFeedInChange(double newVal) {
        @ColorInt int colorPositive = getColor(requireContext(), attr.colorSecondary, Color.GRAY);
        @ColorInt int colorNegative = getColor(requireContext(), attr.colorError, Color.GRAY);
        @ColorInt int colorNone = getColor(requireContext(), attr.colorOutline, Color.GRAY);
        binding.statusGridView.setFlowAmount(newVal);
        if (newVal < 0) {
            binding.statusGridView.setFlowDirection(Direction.OUT);
            binding.statusGridView.setFlowColor(colorNegative);
        } else {
            binding.statusGridView.setFlowDirection(Direction.IN);
            binding.statusGridView.setFlowColor(newVal > 0 ? colorPositive : colorNone);
        }
    }

    private Observer<Double> observePositiveFlowRate(EnergyFlowView view, @AttrRes int color) {
        return val -> {
            @ColorInt int colorPositive = getColor(view.getContext(), color, Color.GRAY);
            @ColorInt int colorNone = getColor(view.getContext(), attr.colorOutline, Color.GRAY);
            view.setFlowAmount(val);
            view.setFlowColor(val > 0 ? colorPositive : colorNone);
        };
    }
}