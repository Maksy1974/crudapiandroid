package com.example.crudapi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapi.R;
import com.example.crudapi.model.Krs;

import java.util.List;

public class KrsAdapter extends RecyclerView.Adapter<KrsAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onDelete(Krs krs);
    }

    private final List<Krs> list;
    private final OnItemActionListener listener;

    public KrsAdapter(List<Krs> list, OnItemActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_krs, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Krs k = list.get(position);
        String mhs = k.getMahasiswa() != null ? k.getMahasiswa().getNama() : ("Mahasiswa #" + k.getMahasiswaId());
        String mk = k.getMatakuliah() != null ? k.getMatakuliah().getNama() : ("MK #" + k.getMatakuliahId());
        holder.txtMahasiswa.setText(mhs);
        holder.txtMk.setText(mk);

        holder.itemView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add("Hapus");
            popup.setOnMenuItemClickListener(item -> {
                listener.onDelete(k);
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
        final TextView txtMahasiswa;
        final TextView txtMk;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMahasiswa = itemView.findViewById(R.id.txtMahasiswa);
            txtMk = itemView.findViewById(R.id.txtMk);
        }
    }
}
