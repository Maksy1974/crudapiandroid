package com.example.crudapi.adapter;

import android.view.*;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapi.R;
import com.example.crudapi.model.Prodi;

import java.util.List;

//public class ProdiAdapter extends RecyclerView.Adapter<ProdiAdapter.ViewHolder> {
//
//    private List<Prodi> list;
//
//    public ProdiAdapter(List<Prodi> list) {
//        this.list = list;
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView txtNama;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            txtNama = itemView.findViewById(R.id.txtNama);
//        }
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_prodi, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.txtNama.setText(list.get(position).getNama());
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//}

public class ProdiAdapter extends RecyclerView.Adapter<ProdiAdapter.ViewHolder> {

    private List<Prodi> list;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(Prodi prodi);
        void onDelete(Prodi prodi);
    }

    public ProdiAdapter(List<Prodi> list, OnItemActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama;
        Button btnEdit, btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNama);
//            btnEdit = itemView.findViewById(R.id.btnEdit);
//            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prodi, parent, false);
        return new ViewHolder(view);
    }

//    @Override
////    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
////        Prodi prodi = list.get(position);
////
////        holder.txtNama.setText(prodi.getNama());
////
////        holder.btnEdit.setOnClickListener(v -> listener.onEdit(prodi));
////        holder.btnDelete.setOnClickListener(v -> listener.onDelete(prodi));
////    }
@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Prodi prodi = list.get(position);

    holder.txtNama.setText(prodi.getNama());

    holder.itemView.setOnClickListener(v -> {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenu().add("Edit");
        popup.getMenu().add("Hapus");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Edit")) {
                listener.onEdit(prodi);
            } else if (item.getTitle().equals("Hapus")) {
                listener.onDelete(prodi);
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
}
