package io.github.j0b10.mad.myenergy.ui.views;

import static com.google.android.material.color.MaterialColors.getColor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;

import com.google.android.material.card.MaterialCardView;

import java.util.Locale;
import java.util.Optional;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.LayoutEnergyFlowBinding;

@SuppressWarnings("unused")
public class EnergyFlowView extends MaterialCardView {

    @ColorInt
    private int flowColor = Color.GRAY;
    private Drawable flowImage;
    private Direction flowDirection = Direction.OUT;
    private double flowAmount;
    private String flowLabel;


    private LayoutEnergyFlowBinding binding;

    public EnergyFlowView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public EnergyFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public EnergyFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attributes, int defStyleAttr) {
        binding = LayoutEnergyFlowBinding.inflate(LayoutInflater.from(context), this, true);
        if (attributes != null) {
            TypedArray attrib = context.obtainStyledAttributes(attributes, R.styleable.EnergyFlowView, defStyleAttr, 0);
            loadAttributes(attrib);
            attrib.recycle();
        }
        Optional.ofNullable(flowImage).ifPresent(binding.flowImg::setImageDrawable);
        Optional.ofNullable(flowLabel).ifPresent(binding.flowLbl::setText);
        binding.flowIcon.setImageDrawable(flowDirection.getIcon(context));
        binding.flowIcon.setImageTintList(ColorStateList.valueOf(flowColor));
        binding.flowText.setText(getFlowAmountTxt());
        binding.flowText.setTextColor(flowColor);
    }

    private void loadAttributes(TypedArray attributes) {
        flowImage = attributes.getDrawable(R.styleable.EnergyFlowView_flowImage);
        flowDirection = Direction.values()[
                attributes.getInt(R.styleable.EnergyFlowView_flowDirection, 0)];
        flowColor = attributes.getColor(R.styleable.EnergyFlowView_flowColor, flowColor);
        flowAmount = attributes.getFloat(R.styleable.EnergyFlowView_flowAmount, 0f);
        flowLabel = attributes.getString(R.styleable.EnergyFlowView_flowLabel);
    }


    public Drawable getFlowImage() {
        return flowImage;
    }

    public void setFlowImage(Drawable flowImage) {
        this.flowImage = flowImage;
        binding.flowIcon.setImageDrawable(flowImage);
    }

    public Direction getFlowDirection() {
        return flowDirection;
    }

    public void setFlowDirection(Direction flowDirection) {
        this.flowDirection = flowDirection;
        binding.flowIcon.setImageDrawable(flowDirection.getIcon(getContext()));
    }

    public double getFlowAmount() {
        return flowAmount;
    }

    public String getFlowAmountTxt() {
        return String.format(Locale.getDefault(), "%.2f kW", flowAmount);
    }

    public void setFlowAmount(double flowAmount) {
        this.flowAmount = flowAmount;
        binding.flowText.setText(getFlowAmountTxt());
    }

    public String getFlowLabel() {
        return flowLabel;
    }

    public void setFlowLabel(String flowLabel) {
        this.flowLabel = flowLabel;
        binding.flowText.setText(flowLabel);
    }

    @ColorInt
    public int getFlowColor() {
        return flowColor;
    }

    public void setFlowColor(int flowColor) {
        this.flowColor = flowColor;
        binding.flowIcon.setImageTintList(ColorStateList.valueOf(flowColor));
        binding.flowText.setTextColor(flowColor);
    }



    public Observer<Double> observePositiveFlowRate(@AttrRes int color) {
        return val -> {
            @ColorInt int colorPositive = getColor(getContext(), color, Color.GRAY);
            @ColorInt int colorNone = getColor(getContext(), com.google.android.material.R.attr.colorOutline, Color.GRAY);
            setFlowAmount(val);
            setFlowColor(val > 0 ? colorPositive : colorNone);
        };
    }

    public enum Direction {

        IN("flow_in"), OUT("flow_out");

        private final String resourceName;

        Direction(String resourceName) {
            this.resourceName = resourceName;
        }

        @SuppressLint("DiscouragedApi")
        public Drawable getIcon(Context context) {
            int id = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
            if (id == 0) return new ColorDrawable(Color.TRANSPARENT);
            return ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
        }
    }
}
