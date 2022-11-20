package io.github.j0b10.mad.myenergy.ui.login;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.Arrays;

public class IPv4AddressFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String insert = source.subSequence(start, end).toString();
        String unchanged = dest.subSequence(dstart, dend).toString();
        String resultTxt = dest.subSequence(0, dstart) + insert + dest.subSequence(dend, dest.length());
        if (isIPv4Address(resultTxt, false)) {
            return null;
        } else {
            return unchanged;
        }
    }

    public boolean isIPv4Address(CharSequence text, boolean strict) {
        int dots = (int) text.chars().filter(ch -> '.' == ch).count();
        if (dots > 3 || (strict && dots != 3)) {
            return false;
        }
        String[] octets = text.toString().split("\\.");
        return Arrays.stream(octets)
                .filter(s -> strict || !s.isEmpty())
                .allMatch(this::isUnsignedByte);
    }

    private boolean isUnsignedByte(String string) {
        if (!string.matches("\\d{1,3}")) {
            return false;
        }
        try {
            int value = Integer.parseInt(string);
            return 0 <= value && value <= 255;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
