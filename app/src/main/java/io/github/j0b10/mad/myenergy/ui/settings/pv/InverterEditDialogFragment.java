package io.github.j0b10.mad.myenergy.ui.settings.pv;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Optional;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.DialogInverterEditBinding;
import io.github.j0b10.mad.myenergy.ui.login.IPv4AddressFilter;

public class InverterEditDialogFragment extends DialogFragment {

    public static final String TAG = "inverterEdit";

    public static final String
            ARG_NAME = "inverter_name",
            ARG_PORT = "inverter_port",
            ARG_IP = "inverter_ip";

    private DialogInverterEditBinding binding;
    private IPv4AddressFilter ipv4Filter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        binding = DialogInverterEditBinding.inflate(inflater, null, false);
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .setTitle("Add Inverter")//fixme change title for edit mode
                .setNegativeButton(R.string.btn_cancel, (d, i) -> requireDialog().cancel())
                .setPositiveButton("Add", this::onApply);
        loadArgument(ARG_NAME, binding.inverterName);
        loadArgument(ARG_IP, binding.inverterIp);
        loadArgument(ARG_PORT, binding.inverterPort);
        ipv4Filter = new IPv4AddressFilter();
        binding.inverterIp.setFilters(new InputFilter[]{ipv4Filter});
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadArgument(String key, EditText view) {
        if (getArguments() == null) return;
        Optional.ofNullable(getArguments().getString(key)).ifPresent(view::setText);
    }

    private boolean readArgument(Bundle bundle, String key, EditText view) {
        if (view.getText() == null) {
            view.setError("empty");
            return false;
        } else {
            bundle.putString(key, view.getText().toString());
            return true;
        }
    }

    private boolean validateInputs() {
        if (binding.inverterIp.getText() == null) return false;
        if (!ipv4Filter.isIPv4Address(binding.inverterIp.getText(), true)) {
            binding.inverterIp.setError(getString(R.string.error_invalid_ip, binding.inverterIp.getText()));
            return false;
        }
        return true;
    }

    private void onApply(DialogInterface d, int i) {
        if (!validateInputs()) return;
        Bundle result = new Bundle();
        boolean allSet = readArgument(result, ARG_NAME, binding.inverterName)
                | readArgument(result, ARG_IP, binding.inverterIp);
        // | readArgument(result, ARG_PORT, binding.inverterPort);
        if (allSet) {
            getParentFragmentManager().setFragmentResult(TAG, result);
            requireDialog().dismiss();
        }
    }
}
