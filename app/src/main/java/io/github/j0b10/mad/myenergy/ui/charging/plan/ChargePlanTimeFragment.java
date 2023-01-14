package io.github.j0b10.mad.myenergy.ui.charging.plan;

import static java.lang.String.format;
import static io.github.j0b10.mad.myenergy.ui.charging.plan.ChargePlanActivity.TIME_FORMAT;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.R.attr;
import com.google.android.material.color.MaterialColors;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

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

    static LocalDateTime round5Min(LocalDateTime time) {
        int mod = time.getMinute() % 5;
        int correction = mod < 3 ? -mod : 5 - mod;
        return time.plusMinutes(correction);
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
                binding.cpInfoUntil.setText(TIME_FORMAT.format(time))
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
        //todo set default charging duration in config
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
        LocalDateTime startTime = Objects.requireNonNull(model.startTime.getValue());
        LocalDateTime endTime = round5Min(startTime.plus(timeDuration.getDuration()));
        model.planTime.setValue(endTime);
        model.duration.setValue(timeDuration.getDuration());
        if (timeDuration.getDurationMinutes() >= 720) {
            @ColorInt int color = MaterialColors.getColor(requireContext(),
                    attr.colorTertiaryContainer, Color.GRAY);
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
        binding.cpInfoHH.setText(format(Locale.getDefault(), "%02d", duration.toHours()));
        binding.cpInfoMm.setText(format(Locale.getDefault(), "%02d", duration.toMinutes() % 60));
    }
}