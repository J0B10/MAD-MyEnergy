package io.github.j0b10.mad.myenergy.ui.charging.plan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

import java.time.format.DateTimeFormatter;
import java.util.List;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.ActivityChargePlanBinding;

public class ChargePlanActivity extends AppCompatActivity {

    static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private static final List<Integer> TAB_LABEL_RES_IDS = List.of(
            R.string.cp_title_time, R.string.cp_title_plan
    );

    private FragmentAdapter adapter;
    private ActivityChargePlanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChargePlanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new FragmentAdapter(this);
        binding.cpViewPager.setAdapter(adapter);
        binding.cpViewPager.setOffscreenPageLimit(adapter.getItemCount() - 1);
        binding.cpViewPager.setUserInputEnabled(false);
        binding.cpViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.cpBtnNext.setText(getButtonText(position));
            }
        });
        new TabLayoutMediator(binding.cpTab, binding.cpViewPager,
                (tab, pos) -> tab.setText(TAB_LABEL_RES_IDS.get(pos))
        ).attach();
        binding.cpBtnNext.setOnClickListener(b -> turnPage(+1));
        binding.cpBtnCancel.setOnClickListener(b -> cancel());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        turnPage(-1);
    }

    private void cancel() {
        //TODO on cancel
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void apply() {
        //TODO on apply
        binding.cpProgress.setVisibility(View.VISIBLE);
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void turnPage(int dir) {
        int position = binding.cpViewPager.getCurrentItem();
        int newPosition = position + dir;
        if (newPosition < 0) {
            cancel();
        } else if (newPosition < adapter.getItemCount()) {
            binding.cpViewPager.setCurrentItem(newPosition);
        } else {
            apply();
        }
    }

    private String getButtonText(int position) {
        final int lastPage = adapter.getItemCount() - 1;
        return position < lastPage ? getString(R.string.btn_next) : getString(R.string.btn_apply);
    }

    static class FragmentAdapter extends FragmentStateAdapter {

        FragmentAdapter(FragmentActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return switch (position) {
                case 0 -> new ChargePlanTimeFragment();
                case 1 -> new ChargePlanEnergyFragment();
                default -> throw new IndexOutOfBoundsException("fragment position out of bounds: " + position);
            };
        }

        @Override
        public int getItemCount() {
            return TAB_LABEL_RES_IDS.size();
        }
    }
}