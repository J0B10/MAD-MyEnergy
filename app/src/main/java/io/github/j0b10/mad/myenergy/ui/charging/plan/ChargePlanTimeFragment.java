package io.github.j0b10.mad.myenergy.ui.charging.plan;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.color.MaterialColors;

import java.time.LocalDateTime;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.FragmentChargePlanTimeBinding;
import nl.joery.timerangepicker.TimeRangePicker;

public class ChargePlanTimeFragment extends Fragment implements TimeRangePicker.OnDragChangeListener, TimeRangePicker.OnTimeChangeListener {


    private LocalDateTime startTime;
    private FragmentChargePlanTimeBinding binding;
    private ChargePlanViewModel model;

    @Nullable
    static TimeRangePicker.Time convert(@Nullable LocalDateTime time) {
        if (time == null) return null;
        return new TimeRangePicker.Time(time.getHour(), time.getMinute());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChargePlanTimeBinding.inflate(getLayoutInflater(), container, false);
        model = new ViewModelProvider(requireActivity()).get(ChargePlanViewModel.class);

        binding.picker.setOnTimeChangeListener(this);
        binding.picker.setOnDragChangeListener(this);
        Log.i("test", "test");
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        //todo set default charging time in config
        //fixme setting a custom end time is buggy as fuck.
        //probably must fork & fix https://github.com/Droppers/TimeRangePicker
        //maybe it's a compatibility issue? unit tests in forked repo seem to work but in this repo they don't
        startTime = LocalDateTime.now();
        binding.picker.setStartTime(convert(startTime));
        binding.cpInfoUntil.setText(binding.picker.getEndTime().toString());
        onDurationChange(binding.picker.getDuration());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onDragStart(@NonNull TimeRangePicker.Thumb thumb) {
        return thumb == TimeRangePicker.Thumb.END;
    }

    @Override
    public void onEndTimeChange(@NonNull TimeRangePicker.Time time) {
        binding.cpInfoUntil.setText(time.toString());
    }

    @Override
    public void onDurationChange(@NonNull TimeRangePicker.TimeDuration timeDuration) {
        model.planTime.setValue(startTime.plus(timeDuration.getDuration()));
        if (timeDuration.getDurationMinutes() >= 720) {
            @ColorInt int color = MaterialColors.getColor(requireContext(),
                    com.google.android.material.R.attr.colorTertiaryContainer, Color.GRAY);
            binding.picker.setSliderColor(color);
        } else {
            binding.picker.setSliderColorRes(R.color.variant_gray);
        }
    }

    @Override
    public void onDragStop(@NonNull TimeRangePicker.Thumb thumb) {
        //Do nothing.
    }

    @Override
    public void onStartTimeChange(@NonNull TimeRangePicker.Time time) {
        //Do nothing.
    }
}