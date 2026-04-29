package com.example.crudapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.example.crudapi.adapter.ProdiAdapter;
import com.example.crudapi.api.*;
import com.example.crudapi.model.Prodi;

import java.util.List;

import retrofit2.*;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnTambah = findViewById(R.id.btnTambah);

        btnTambah.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TambahProdiActivity.class);
            startActivity(intent);
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // reload data setiap kembali ke halaman
    }

    private void loadData() {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);

        api.getProdi().enqueue(new Callback<List<Prodi>>() {
            @Override
            public void onResponse(Call<List<Prodi>> call, Response<List<Prodi>> response) {
                if (response.isSuccessful()) {
                    List<Prodi> list = response.body();

                    ProdiAdapter adapter = new ProdiAdapter(list, new ProdiAdapter.OnItemActionListener() {

                        @Override
                        public void onEdit(Prodi prodi) {
                            // sementara tampilkan toast dulu
                            Toast.makeText(MainActivity.this, "Edit: " + prodi.getNama(), Toast.LENGTH_SHORT).show();
                        }

//                        @Override
//                        public void onDelete(Prodi prodi) {
//                            deleteData(prodi.getId());
//                        }
@Override
public void onDelete(Prodi prodi) {
    new AlertDialog.Builder(MainActivity.this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah data akan dihapus?")
            .setPositiveButton("Ya", (dialog, which) -> {
                deleteData(prodi.getId());
            })
            .setNegativeButton("Batal", null)
            .show();
}
                    });

                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Prodi>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void deleteData(int id) {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);

        api.deleteProdi(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(MainActivity.this, "Data dihapus", Toast.LENGTH_SHORT).show();
                loadData(); // refresh
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}