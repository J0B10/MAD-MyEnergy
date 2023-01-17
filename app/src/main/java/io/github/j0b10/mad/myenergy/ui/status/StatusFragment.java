package io.github.j0b10.mad.myenergy.ui.status;

import static com.google.android.material.R.attr;
import static com.google.android.material.color.MaterialColors.getColor;

import android.content.SharedPreferences;
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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.Snackbar;

import java.time.Duration;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.FragmentStatusBinding;
import io.github.j0b10.mad.myenergy.ui.settings.PreferencesFragment;
import io.github.j0b10.mad.myenergy.ui.views.EnergyFlowView.Direction;

public class StatusFragment extends Fragment {

    private FragmentStatusBinding binding;
    private Snackbar errorMsg;
    private StatusViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatusBinding.inflate(inflater, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean demoMode = preferences.getBoolean(PreferencesFragment.KEY_DEMO, false);
        String fetchRateS = preferences.getString(PreferencesFragment.KEY_FETCH_RATE, "5.0");
        Duration fetchInterval = Duration.ofMillis((long) (Float.parseFloat(fetchRateS) * 1000L));

        model = new ViewModelProvider(this).get(StatusViewModel.class);
        model.loadProviders(requireContext(), demoMode);
        if (model.status() != null) model.status().configureInterval(fetchInterval);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        if (model.status() != null) {
            model.status().gridFeedIn().observe(lifecycleOwner, this::onGridFeedInChange);
            model.status().homeConsumption().observe(lifecycleOwner,
                    binding.statusHomeView.observePositiveFlowRate(attr.colorError));
            model.status().evConsumption().observe(lifecycleOwner,
                    binding.statusEvView.observePositiveFlowRate(attr.colorPrimary));
            model.status().pvProduction().observe(lifecycleOwner,
                    binding.statusPvView.observePositiveFlowRate(attr.colorSecondary));
            model.status().error().observe(lifecycleOwner, this::onError);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (model.status() != null) model.status().start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (model.status() != null) model.status().stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void onError(@Nullable Exception e) {
        if (e == null) {
            if (errorMsg != null) errorMsg.dismiss();
            return;
        }
        View root = requireView();
        String msg = getString(R.string.error_status, e.getMessage());
        errorMsg = Snackbar.make(root, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(MaterialColors.getColor(root, attr.colorError))
                .setTextColor(MaterialColors.getColor(root, attr.colorOnError))
                .setAnchorView(binding.anchorError);
        errorMsg.show();
        Log.w("Status", e);
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
}