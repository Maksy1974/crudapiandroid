package com.example.crudapi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapi.R;
import com.example.crudapi.model.Matakuliah;

import java.util.List;

public class MatakuliahAdapter extends RecyclerView.Adapter<MatakuliahAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onEdit(Matakuliah mk);

        void onDelete(Matakuliah mk);
    }

    private final List<Matakuliah> list;
    private final OnItemActionListener listener;

    public MatakuliahAdapter(List<Matakuliah> list, OnItemActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matakuliah, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Matakuliah mk = list.get(position);
        holder.txtNama.setText(mk.getNama());
        holder.itemView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add("Edit");
            popup.getMenu().add("Hapus");
            popup.setOnMenuItemClickListener(item -> {
                if ("Edit".contentEquals(item.getTitle())) {
                    listener.onEdit(mk);
                } else if ("Hapus".contentEquals(item.getTitle())) {
                    listener.onDelete(mk);
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

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNama);
        }
    }
}
