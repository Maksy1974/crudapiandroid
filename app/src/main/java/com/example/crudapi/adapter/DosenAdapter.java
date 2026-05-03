package com.example.crudapi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.crudapi.R;
import com.example.crudapi.model.Dosen;

import java.util.ArrayList;
import java.util.List;

public class DosenAdapter extends RecyclerView.Adapter<DosenAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onEdit(Dosen d);

        void onDelete(Dosen d);
    }

    private final List<Dosen> list;
    private final OnItemActionListener listener;

    public DosenAdapter(List<Dosen> list, OnItemActionListener listener) {
        this.list = list != null ? list : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dosen, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dosen d = list.get(position);
        holder.txtNama.setText(d.getNama());
        holder.txtNip.setText(d.getNip());
        String prodiNama = d.getProdi() != null ? d.getProdi().getNama() : ("Prodi ID: " + d.getProdiId());
        holder.txtProdi.setText(prodiNama);

        if (d.getFoto() != null && !d.getFoto().isEmpty()) {
            Glide.with(holder.imgFoto.getContext())
                    .load(d.getFoto())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.imgFoto);
        } else {
            holder.imgFoto.setImageResource(R.mipmap.ic_launcher);
        }

        holder.itemView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add("Edit");
            popup.getMenu().add("Hapus");
            popup.setOnMenuItemClickListener(item -> {
                if ("Edit".contentEquals(item.getTitle())) {
                    listener.onEdit(d);
                } else if ("Hapus".contentEquals(item.getTitle())) {
                    listener.onDelete(d);
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
        final ImageView imgFoto;
        final TextView txtNama;
        final TextView txtNip;
        final TextView txtProdi;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            txtNama = itemView.findViewById(R.id.txtNama);
            txtNip = itemView.findViewById(R.id.txtNip);
            txtProdi = itemView.findViewById(R.id.txtProdi);
        }
    }
}
