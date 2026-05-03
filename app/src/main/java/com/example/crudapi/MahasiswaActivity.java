package com.example.crudapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapi.adapter.MahasiswaAdapter;
import com.example.crudapi.api.ApiClient;
import com.example.crudapi.api.ApiService;
import com.example.crudapi.model.Mahasiswa;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MahasiswaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mahasiswa);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, MahasiswaFormActivity.class)));

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
        api.getMahasiswaList().enqueue(new Callback<List<Mahasiswa>>() {
            @Override
            public void onResponse(Call<List<Mahasiswa>> call, Response<List<Mahasiswa>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MahasiswaActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Mahasiswa> list = response.body() != null ? response.body() : new ArrayList<>();
                MahasiswaAdapter adapter = new MahasiswaAdapter(list, new MahasiswaAdapter.OnItemActionListener() {
                    @Override
                    public void onEdit(Mahasiswa m) {
                        Intent i = new Intent(MahasiswaActivity.this, MahasiswaFormActivity.class);
                        i.putExtra(MahasiswaFormActivity.EXTRA_ID, m.getId());
                        startActivity(i);
                    }

                    @Override
                    public void onDelete(Mahasiswa m) {
                        new AlertDialog.Builder(MahasiswaActivity.this)
                                .setTitle(R.string.konfirmasi_hapus)
                                .setMessage(m.getNama())
                                .setPositiveButton(R.string.ya, (d, w) -> deleteMahasiswa(m.getId()))
                                .setNegativeButton(R.string.batal, null)
                                .show();
                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Mahasiswa>> call, Throwable t) {
                Toast.makeText(MahasiswaActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteMahasiswa(int id) {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.deleteMahasiswa(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(MahasiswaActivity.this, R.string.data_dihapus, Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MahasiswaActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
