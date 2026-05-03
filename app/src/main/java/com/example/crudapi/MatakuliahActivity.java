package com.example.crudapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapi.adapter.MatakuliahAdapter;
import com.example.crudapi.api.ApiClient;
import com.example.crudapi.api.ApiService;
import com.example.crudapi.model.Matakuliah;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatakuliahActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matakuliah);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, MatakuliahFormActivity.class)));

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
        api.getMatakuliah().enqueue(new Callback<List<Matakuliah>>() {
            @Override
            public void onResponse(Call<List<Matakuliah>> call, Response<List<Matakuliah>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MatakuliahActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Matakuliah> list = response.body() != null ? response.body() : new ArrayList<>();
                MatakuliahAdapter adapter = new MatakuliahAdapter(list, new MatakuliahAdapter.OnItemActionListener() {
                    @Override
                    public void onEdit(Matakuliah mk) {
                        Intent i = new Intent(MatakuliahActivity.this, MatakuliahFormActivity.class);
                        i.putExtra(MatakuliahFormActivity.EXTRA_ID, mk.getId());
                        i.putExtra(MatakuliahFormActivity.EXTRA_NAMA, mk.getNama());
                        startActivity(i);
                    }

                    @Override
                    public void onDelete(Matakuliah mk) {
                        new AlertDialog.Builder(MatakuliahActivity.this)
                                .setTitle(R.string.konfirmasi_hapus)
                                .setMessage(mk.getNama())
                                .setPositiveButton(R.string.ya, (d, w) -> deleteMk(mk.getId()))
                                .setNegativeButton(R.string.batal, null)
                                .show();
                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Matakuliah>> call, Throwable t) {
                Toast.makeText(MatakuliahActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMk(int id) {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.deleteMatakuliah(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MatakuliahActivity.this, R.string.data_dihapus, Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(MatakuliahActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MatakuliahActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
