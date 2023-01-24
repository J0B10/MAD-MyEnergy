package io.github.j0b10.mad.myenergy.ui.charging;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.DialogChargeSpeedBinding;
import io.github.j0b10.mad.myenergy.ui.settings.PreferencesFragment;

public class ChargeSpeedDialogFragment extends DialogFragment {

    public final static String TAG = "chargeSpeed";

    private DialogChargeSpeedBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        int val = args != null ? args.getInt("val", 6) : 6;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = DialogChargeSpeedBinding.inflate(inflater, null, false);
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .setTitle(R.string.lbl_status_ev)
                .setNegativeButton(R.string.btn_cancel, (d, i) -> requireDialog().cancel())
                .setPositiveButton(R.string.btn_apply, this::onApply);
        String maxString = preferences.getString(PreferencesFragment.KEY_QUICK_CHARGE_MAX_CURRENT, "16");
        int max = Integer.parseInt(maxString);
        binding.speedSlider.setLabelFormatter(value -> {
            double p = 230.0 * value * 3; //P=U*I * 3phases
            return getString(R.string.lbl_charge_rate_kW, p / 1000);
        });
        binding.speedSlider.setValueFrom(6);
        binding.speedSlider.setValueTo(max);
        binding.speedSlider.setValue(val);
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onApply(DialogInterface dialog, int id) {
        int val = (int) binding.speedSlider.getValue();
        Bundle bundle = new Bundle();
        bundle.putInt("val", val);
        getParentFragmentManager().setFragmentResult(TAG, bundle);
        ChargeSpeedDialogFragment.this.requireDialog().dismiss();
    }
}
