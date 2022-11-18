
package io.github.j0b10.mad.myenergy.ui.charging.plan;

import static io.github.j0b10.mad.myenergy.ui.charging.plan.ChargePlanActivity.TIME_FORMAT;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ss.svs.SegmentedVerticalSeekBar;

import java.time.format.DateTimeFormatter;

import io.github.j0b10.mad.myenergy.databinding.FragmentChargePlanEnergyBinding;

public class ChargePlanEnergyFragment extends Fragment {


    private FragmentChargePlanEnergyBinding binding;
    private ChargePlanViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity()).get(ChargePlanViewModel.class);
        binding = FragmentChargePlanEnergyBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LifecycleOwner owner = getViewLifecycleOwner();

        addPlanConstraints();

        //target time label
        model.planTime.observe(owner, time ->
                binding.cpInfoUntil.setText(time.toLocalTime().format(TIME_FORMAT))
        );

        //total charge label
        model.planCharge.observe(owner, i -> calcTotal());
        model.stateCharge.observe(owner, i -> calcTotal());
        model.total.observe(owner, value -> binding.cpInfoTotal.setText(value + " kWh"));

        //maxima
        model.carCapacity.observe(owner, binding.cpSelState::setMax);
        model.carCapacity.observe(owner, binding.cpSelPlan::setMax);

        //info labels
        model.planCharge.observe(owner, value -> {
            float percentage = chargePercentage(value);
            binding.cpBattery.setChargeSecondary(percentage);
            binding.cpGuidePlan.setGuidelinePercent(labelPosition(percentage));
            binding.cpInfoPlanKwh.setText(value + " kWh");
            binding.cpInfoPlanPc.setText(Math.round(percentage * 100) + "%");
            binding.cpInfoPlanKm.setText(carRangeKm(value) + " km");
        });
        model.stateCharge.observe(owner, value -> {
            float percentage = chargePercentage(value);
            binding.cpBattery.setChargePrimary(percentage);
            binding.cpGuideState.setGuidelinePercent(labelPosition(percentage));
            binding.cpInfoStateKwh.setText(value + " kWh");
            binding.cpInfoStatePc.setText(Math.round(percentage * 100) + "%");
            binding.cpInfoStateKm.setText(carRangeKm(value) + " km");
        });

        //noinspection ConstantConditions
        binding.cpSelPlan.setValue(model.planCharge.getValue());
        //noinspection ConstantConditions
        binding.cpSelState.setValue(model.stateCharge.getValue());
        new SeekBarValueBinding(owner, binding.cpSelPlan, model.planCharge);
        new SeekBarValueBinding(owner, binding.cpSelState, model.stateCharge);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addPlanConstraints() {
        model.stateCharge.observe(getViewLifecycleOwner(), value -> {
            //noinspection ConstantConditions
            if (value > model.planCharge.getValue()) model.planCharge.setValue(value);
        });
        model.planCharge.observe(getViewLifecycleOwner(), value -> {
            //noinspection ConstantConditions
            int min = model.stateCharge.getValue();
            if (value < min) model.planCharge.setValue(min);
        });
    }

    private int carRangeKm(int charge) {
        //noinspection ConstantConditions
        return Math.round(charge * 100 / model.carWLTP.getValue());
    }

    private float chargePercentage(int chargeValue) {
        //noinspection ConstantConditions
        return (float) chargeValue / model.carCapacity.getValue();
    }

    private void calcTotal() {
        //noinspection ConstantConditions
        int total = model.planCharge.getValue() - model.stateCharge.getValue();
        model.total.setValue(total);
    }

    private float labelPosition(float percentage) {
        float borderPercent = 100.0f / binding.cpInfoPlan.getHeight();
        if (percentage < borderPercent) return 1 - borderPercent;
        else if (percentage > 1 - borderPercent) return borderPercent;
        else return 1 - percentage;
    }

    static class SeekBarValueBinding implements SegmentedVerticalSeekBar.OnValuesChangeListener {

        private final MutableLiveData<Integer> valueData;

        private boolean trackingChanges;

        SeekBarValueBinding(LifecycleOwner lifecycleOwner, SegmentedVerticalSeekBar bar, MutableLiveData<Integer> valueData) {
            this.valueData = valueData;
            this.trackingChanges = true;
            bar.setOnBoxedPointsChangeListener(this);
            valueData.observe(lifecycleOwner, val -> {
                trackingChanges = false;
                bar.setValue(val);
                trackingChanges = true;
            });
        }

        @Override
        public void onProgressChanged(@Nullable SegmentedVerticalSeekBar ignored, int val) {
            if (trackingChanges) valueData.setValue(val);
        }

        @Override
        public void onStartTrackingTouch(@Nullable SegmentedVerticalSeekBar ignored) {
        }

        @Override
        public void onStopTrackingTouch(@Nullable SegmentedVerticalSeekBar ignored) {
        }
    }
}