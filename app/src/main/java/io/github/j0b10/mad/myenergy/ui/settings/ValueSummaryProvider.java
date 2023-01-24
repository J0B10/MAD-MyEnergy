package io.github.j0b10.mad.myenergy.ui.settings;

import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;

import io.github.j0b10.mad.myenergy.R;

public class ValueSummaryProvider implements Preference.SummaryProvider<EditTextPreference> {

    @StringRes
    private final int description;
    private final String unit;

    public ValueSummaryProvider(@StringRes int description, String unit) {
        this.description = description;
        this.unit = unit;
    }

    @Nullable
    @Override
    public CharSequence provideSummary(@NonNull EditTextPreference preference) {
        CharSequence description = preference.getContext().getText(this.description);
        String text = preference.getText();
        String value = preference.getContext().getString(R.string.value, text, unit);
        return new SpannableStringBuilder(description)
                .append("\n")
                .append(value);
    }
}
