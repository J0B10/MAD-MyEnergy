package io.github.j0b10.mad.myenergy.ui.status;


import static com.google.android.material.R.attr;
import static com.google.android.material.color.MaterialColors.getColor;

import android.content.SharedPreferences;
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
import androidx.preference.PreferenceManager;

import io.github.j0b10.mad.myenergy.databinding.FragmentStatusBinding;
import io.github.j0b10.mad.myenergy.model.demo.DemoStatusAdapter;
import io.github.j0b10.mad.myenergy.model.evcharger.SessionManager;
import io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVStatusAdapter;
import io.github.j0b10.mad.myenergy.model.target.StatusProvider;
import io.github.j0b10.mad.myenergy.ui.settings.PreferencesFragment;
import io.github.j0b10.mad.myenergy.ui.views.EnergyFlowView;
import io.github.j0b10.mad.myenergy.ui.views.EnergyFlowView.Direction;

public class StatusFragment extends Fragment {

    private FragmentStatusBinding binding;

    private StatusProvider status;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatusBinding.inflate(inflater, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean demoMode = preferences.getBoolean(PreferencesFragment.KEY_DEMO, false);

        SessionManager sessionManager = SessionManager.getInstance(requireContext());
        if (demoMode) {
            status = new DemoStatusAdapter();
        } else {
            status = new EVStatusAdapter(sessionManager.getAPI());
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        status.getGridFeedIn().observe(lifecycleOwner, this::onGridFeedInChange);
        status.getHomeConsumption().observe(lifecycleOwner,
                observePositiveFlowRate(binding.statusHomeView, attr.colorError));
        status.getEvConsumption().observe(lifecycleOwner,
                observePositiveFlowRate(binding.statusEvView, attr.colorPrimary));
        status.getPvProduction().observe(lifecycleOwner,
                observePositiveFlowRate(binding.statusPvView, attr.colorSecondary));
    }

    @Override
    public void onResume() {
        super.onResume();
        status.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        status.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        status = null;
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