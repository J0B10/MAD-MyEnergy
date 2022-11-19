package io.github.j0b10.mad.myenergy.ui.charging.plan;

import static io.github.j0b10.mad.myenergy.ui.charging.plan.ChargePlanActivity.TIME_FORMAT;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.color.MaterialColors;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.FragmentChargePlanTimeBinding;
import nl.joery.timerangepicker.TimeRangePicker;

public class ChargePlanTimeFragment extends Fragment implements TimeRangePicker.OnDragChangeListener, TimeRangePicker.OnTimeChangeListener {

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

        LifecycleOwner owner = getViewLifecycleOwner();

        //from time info
        model.startTime.observe(owner, time ->
                binding.cpInfoFrom.setText(TIME_FORMAT.format(time))
        );
        //until time info
        model.planTime.observe(owner, time ->
                binding.cpInfoFrom.setText(TIME_FORMAT.format(time))
        );

        //duration time info
        model.startTime.observe(owner, time -> updateDurationLbL(time, model.planTime.getValue()));
        model.planTime.observe(owner, time -> updateDurationLbL(model.startTime.getValue(), time));

        binding.picker.setOnTimeChangeListener(this);
        binding.picker.setOnDragChangeListener(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        //todo set default charging time in config
        //fixme setting a custom end time is buggy as fuck.
        //probably must fork & fix https://github.com/Droppers/TimeRangePicker
        //maybe it's a compatibility issue? unit tests in forked repo seem to work but in this repo they don't
        LocalDateTime startTime = LocalDateTime.now();
        model.startTime.setValue(startTime);
        binding.picker.setStartTime(convert(startTime));
        TimeRangePicker.TimeDuration duration = binding.picker.getDuration();
        onDurationChange(duration);
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
    public void onDurationChange(@NonNull TimeRangePicker.TimeDuration timeDuration) {
        Optional.ofNullable(model.startTime.getValue())
                .map(time -> time.plus(timeDuration.getDuration()))
                .ifPresent(model.planTime::setValue);
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

    @Override
    public void onEndTimeChange(@NonNull TimeRangePicker.Time time) {
        //Do nothing.
    }

    private void updateDurationLbL(@Nullable LocalDateTime from, @Nullable LocalDateTime to) {
        if (from == null || to == null) return;
        Duration duration = Duration.between(from, to);
        binding.cpInfoHH.setText(String.valueOf(duration.toHours()));
        //backwards compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.cpInfoMm.setText(String.format(Locale.getDefault(), "%2d", duration.toMinutesPart()));
        }
    }
}