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
import com.example.crudapi.model.Mahasiswa;

import java.util.List;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onEdit(Mahasiswa m);

        void onDelete(Mahasiswa m);
    }

    private final List<Mahasiswa> list;
    private final OnItemActionListener listener;

    public MahasiswaAdapter(List<Mahasiswa> list, OnItemActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mahasiswa, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mahasiswa m = list.get(position);
        holder.txtNama.setText(m.getNama());
        holder.txtNim.setText(m.getNim());
        String prodiNama = m.getProdi() != null ? m.getProdi().getNama() : ("Prodi ID: " + m.getProdiId());
        holder.txtProdi.setText(prodiNama);

        if (m.getFoto() != null && !m.getFoto().isEmpty()) {
            Glide.with(holder.imgFoto.getContext())
                    .load(m.getFoto())
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
                    listener.onEdit(m);
                } else if ("Hapus".contentEquals(item.getTitle())) {
                    listener.onDelete(m);
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
        final TextView txtNim;
        final TextView txtProdi;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            txtNama = itemView.findViewById(R.id.txtNama);
            txtNim = itemView.findViewById(R.id.txtNim);
            txtProdi = itemView.findViewById(R.id.txtProdi);
        }
    }
}
