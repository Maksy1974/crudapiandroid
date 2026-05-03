package com.example.crudapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapi.adapter.KrsAdapter;
import com.example.crudapi.api.ApiClient;
import com.example.crudapi.api.ApiService;
import com.example.crudapi.model.Krs;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KrsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_krs);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, KrsFormActivity.class)));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.getKrs().enqueue(new Callback<List<Krs>>() {
            @Override
            public void onResponse(Call<List<Krs>> call, Response<List<Krs>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(KrsActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Krs> list = response.body() != null ? response.body() : new ArrayList<>();
                KrsAdapter adapter = new KrsAdapter(list, k -> new AlertDialog.Builder(KrsActivity.this)
                        .setTitle(R.string.konfirmasi_hapus)
                        .setPositiveButton(R.string.ya, (d, w) -> deleteKrs(k.getId()))
                        .setNegativeButton(R.string.batal, null)
                        .show());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Krs>> call, Throwable t) {
                Toast.makeText(KrsActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteKrs(int id) {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.deleteKrs(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(KrsActivity.this, R.string.data_dihapus, Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(KrsActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
