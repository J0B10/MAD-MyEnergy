package io.github.j0b10.mad.myenergy.ui.settings.pv;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.j0b10.mad.myenergy.R;
import io.github.j0b10.mad.myenergy.databinding.ActivityInvertersListBinding;
import io.github.j0b10.mad.myenergy.model.pv.PVInverter;
import io.github.j0b10.mad.myenergy.model.pv.tripower.TripowerPVInverter;

public class InvertersListActivity extends AppCompatActivity {


    private ActivityInvertersListBinding binding;

    private List<PVInverter> inverters;
    private MenuItem add, del, edit;
    private InvertersListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInvertersListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inverters = new ArrayList<>();
        inverters.add(new TripowerPVInverter("192.168.0.31", "Test1"));
        inverters.add(new TripowerPVInverter("192.168.0.32", "Test2"));
        adapter = new InvertersListAdapter(inverters, this::onSelectListItem);
        binding.invertersList.setAdapter(adapter);
        binding.invertersList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.invertersList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_inverters_list_menu, menu);
        add = menu.findItem(R.id.add);
        edit = menu.findItem(R.id.edit);
        del = menu.findItem(R.id.del);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            onAddItem();
        } else if (item.getItemId() == R.id.edit) {
            int sel = adapter.getSelected();
            adapter.setSelected(RecyclerView.NO_POSITION);
            onEditItem(sel);
        } else if (item.getItemId() == R.id.del) {
            int sel = adapter.getSelected();
            adapter.setSelected(RecyclerView.NO_POSITION);
            inverters.remove(sel);
            adapter.notifyItemRemoved(sel);
            //Update callbacks of following items, as position changed:
            adapter.notifyItemRangeChanged(sel, adapter.getItemCount() - sel);
            onRemoveItem();
        }
        return true;
    }

    private void onSelectListItem(int pos) {
        boolean itemSelected = pos != RecyclerView.NO_POSITION;
        edit.setVisible(itemSelected);
        del.setVisible(itemSelected);
        add.setVisible(!itemSelected);
    }

    private void onAddItem() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.setFragmentResultListener(InverterEditDialogFragment.TAG, this, (key, result) -> {
            String name = result.getString(InverterEditDialogFragment.ARG_NAME);
            String ip = result.getString(InverterEditDialogFragment.ARG_IP);
            //int port = Integer.parseInt(result.getString(InverterEditDialogFragment.ARG_PORT));
            //fixme custom port & compatibility with other inverter types
            PVInverter inverter = new TripowerPVInverter(ip, name);
            inverters.add(inverter);
            adapter.notifyItemInserted(inverters.size() - 1);
        });
        new InverterEditDialogFragment().show(fragmentManager, InverterEditDialogFragment.TAG);
    }

    private void onEditItem(int position) {
        PVInverter inverter = inverters.get(position);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.setFragmentResultListener(InverterEditDialogFragment.TAG, this, (key, result) -> {
            String name = result.getString(InverterEditDialogFragment.ARG_NAME);
            String ip = result.getString(InverterEditDialogFragment.ARG_IP);
            //int port = Integer.parseInt(result.getString(InverterEditDialogFragment.ARG_PORT));
            PVInverter newInverter = new TripowerPVInverter(ip, name);
            inverters.set(position, newInverter);
            adapter.notifyItemChanged(position);
        });
        Bundle args = new Bundle();
        args.putString(InverterEditDialogFragment.ARG_NAME, inverter.getName());
        args.putString(InverterEditDialogFragment.ARG_IP, inverter.getIP());
        args.putString(InverterEditDialogFragment.ARG_PORT, String.valueOf(inverter.getPort()));
        InverterEditDialogFragment dialogFragment = new InverterEditDialogFragment();
        dialogFragment.setArguments(args);
        dialogFragment.show(fragmentManager, InverterEditDialogFragment.TAG);
    }

    private void onRemoveItem() {

    }
}
