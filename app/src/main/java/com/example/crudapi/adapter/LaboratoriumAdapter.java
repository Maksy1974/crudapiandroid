package com.example.crudapi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapi.R;
import com.example.crudapi.model.Laboratorium;

import java.util.ArrayList;
import java.util.List;

public class LaboratoriumAdapter extends RecyclerView.Adapter<LaboratoriumAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onEdit(Laboratorium lab);

        void onDelete(Laboratorium lab);
    }

    private final List<Laboratorium> list;
    private final OnItemActionListener listener;

    public LaboratoriumAdapter(List<Laboratorium> list, OnItemActionListener listener) {
        this.list = list != null ? list : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_laboratorium, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Laboratorium lab = list.get(position);
        holder.txtNama.setText(lab.getNama());
        String lokasi = lab.getLokasi() != null ? lab.getLokasi() : "";
        holder.txtLokasi.setText(lokasi);

        holder.itemView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add("Edit");
            popup.getMenu().add("Hapus");
            popup.setOnMenuItemClickListener(item -> {
                if ("Edit".contentEquals(item.getTitle())) {
                    listener.onEdit(lab);
                } else if ("Hapus".contentEquals(item.getTitle())) {
                    listener.onDelete(lab);
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView txtNama;
        final TextView txtLokasi;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNama);
            txtLokasi = itemView.findViewById(R.id.txtLokasi);
        }
    }
}
