package io.github.j0b10.mad.myenergy.ui.settings.pv;

import static io.github.j0b10.mad.myenergy.ui.charging.ChargingFragment.showIf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.LayoutInverterBinding;
import io.github.j0b10.mad.myenergy.model.pv.PVInverter;

public class InvertersListAdapter extends RecyclerView.Adapter<InvertersListAdapter.ViewHolder> {

    private final List<PVInverter> list;
    private final Consumer<Integer> selectionChangeListener;
    private int selectedPos = RecyclerView.NO_POSITION;

    public InvertersListAdapter(List<PVInverter> list, Consumer<Integer> selectionChangeListener) {
        this.list = list;
        this.selectionChangeListener = selectionChangeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutInverterBinding binding = LayoutInverterBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PVInverter pvInverter = list.get(position);
        Context context = holder.binding.getRoot().getContext();
        holder.binding.inverterName.setText(pvInverter.getName());
        holder.binding.inverterModel.setText(
                context.getString(R.string.lbl_inverter_model, pvInverter.getModel()));
        holder.binding.inverterIp.setText(
                context.getString(R.string.lbl_inverter_ip, pvInverter.getIP(), pvInverter.getPort()));
        holder.binding.getRoot().setSelected(selectedPos == position);
        Drawable background = selectedPos == position
                ? AppCompatResources.getDrawable(context, R.color.sel_tint) : null;
        holder.binding.sel.setVisibility(showIf(selectedPos == position));
        holder.binding.getRoot().setBackground(background);
        holder.binding.getRoot().setOnLongClickListener(v -> {
            if (selectedPos != position) setSelected(position);
            else setSelected(RecyclerView.NO_POSITION);
            return true;
        });
        holder.binding.getRoot().setOnClickListener(v -> {
            if (selectedPos == position) setSelected(RecyclerView.NO_POSITION);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public int getSelected() {
        return selectedPos;
    }

    public void setSelected(int pos) {
        int prev = selectedPos;
        if (pos >= list.size()) throw new ArrayIndexOutOfBoundsException(pos);
        selectedPos = pos;
        if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos);
        if (prev != RecyclerView.NO_POSITION) notifyItemChanged(prev);
        if (selectionChangeListener != null) selectionChangeListener.accept(selectedPos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LayoutInverterBinding binding;

        public ViewHolder(@NonNull LayoutInverterBinding inverterBinding) {
            super(inverterBinding.getRoot());
            this.binding = inverterBinding;
        }
    }
}
