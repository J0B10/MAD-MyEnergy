package io.github.j0b10.mad.myenergy.ui.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.LayoutBatteryBinding;

@SuppressWarnings("unused")
public class BatteryView extends ConstraintLayout {

    private static final float DEFAULT_WIDTH = 768;
    private static final float DEFAULT_TOP_HEIGHT = 168;
    private static final float DEFAULT_CORNER_RADIUS = 64;

    @ColorInt
    private static final int DEFAULT_COLOR_BLUE = Color.parseColor("#4397D0");
    @ColorInt
    private static final int DEFAULT_COLOR_GREEN = Color.parseColor("#87B237");

    private static final float DEFAULT_CHARGE_PRIMARY = 0.76f;
    private static final float DEFAULT_CHARGE_SECONDARY = 1.0f;

    @ColorInt
    private int batteryPrimaryColor;
    @ColorInt
    private int batterySecondaryColor;

    private float batteryChargePrimary;
    private float batteryChargeSecondary;

    private LayoutBatteryBinding binding;

    public BatteryView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public BatteryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public BatteryView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        binding = LayoutBatteryBinding.inflate(LayoutInflater.from(context), this, true);
        setWillNotDraw(false);

        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BatteryView, defStyleAttr, 0);
            setPrimaryColor(attributes.getColor(R.styleable.BatteryView_batteryPrimaryColor, DEFAULT_COLOR_GREEN));
            setSecondaryColor(attributes.getColor(R.styleable.BatteryView_batterySecondaryColor, DEFAULT_COLOR_BLUE));
            setChargePrimary(attributes.getFloat(R.styleable.BatteryView_batteryChargePrimary, DEFAULT_CHARGE_PRIMARY));
            setChargeSecondary(attributes.getFloat(R.styleable.BatteryView_batteryChargeSecondary, DEFAULT_CHARGE_SECONDARY));
            attributes.recycle();
        } else {
            setPrimaryColor(DEFAULT_COLOR_GREEN);
            setSecondaryColor(DEFAULT_COLOR_BLUE);
            setChargePrimary(DEFAULT_CHARGE_PRIMARY);
            setChargeSecondary(DEFAULT_CHARGE_SECONDARY);
        }
    }

    @ColorInt
    public int getPrimaryColor() {
        return batteryPrimaryColor;
    }

    public void setPrimaryColor(@ColorInt int primaryColor) {
        this.batteryPrimaryColor = primaryColor;
        binding.batteryPrimary.setBackgroundTintList(ColorStateList.valueOf(primaryColor));
    }

    @ColorInt
    public int getSecondaryColor() {
        return batterySecondaryColor;
    }

    public void setSecondaryColor(@ColorInt int secondaryColor) {
        this.batterySecondaryColor = secondaryColor;
        binding.batterySecondary.setBackgroundTintList(ColorStateList.valueOf(secondaryColor));
    }

    public float getChargePrimary() {
        return batteryChargePrimary;
    }

    public void setChargePrimary(float chargePrimary) {
        this.batteryChargePrimary = chargePrimary;
        invalidate();
    }

    public float getChargeSecondary() {
        return batteryChargeSecondary;
    }

    public void setChargeSecondary(float chargeSecondary) {
        this.batteryChargeSecondary = chargeSecondary;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float correctedTopHeight = binding.batteryTop.getMeasuredHeight() * (DEFAULT_TOP_HEIGHT - DEFAULT_CORNER_RADIUS) / DEFAULT_TOP_HEIGHT;
        float bodySizeFactor = (getMeasuredHeight() - correctedTopHeight) / getMeasuredHeight();
        binding.batteryStatePrimary.setGuidelinePercent(1 - bodySizeFactor * batteryChargePrimary);
        binding.batteryStateSecondary.setGuidelinePercent(1 - bodySizeFactor * batteryChargeSecondary);

        float cornerRadius = getMeasuredWidth() * DEFAULT_CORNER_RADIUS / DEFAULT_WIDTH;
        if (binding.batteryPrimary.getBackground() instanceof GradientDrawable background1) {
            background1.setCornerRadius(cornerRadius);
        } else {
            throw new IllegalStateException("not a GradientDrawable: " + binding.batteryPrimary.getBackground());
        }
        if (binding.batterySecondary.getBackground() instanceof GradientDrawable background2) {
            background2.setCornerRadius(cornerRadius);
        } else {
            throw new IllegalStateException("not a GradientDrawable: " + binding.batterySecondary.getBackground());
        }

        super.onDraw(canvas);
    }
}
