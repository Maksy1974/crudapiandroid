package com.example.crudapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapi.adapter.DosenAdapter;
import com.example.crudapi.api.ApiClient;
import com.example.crudapi.api.ApiService;
import com.example.crudapi.model.Dosen;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DosenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosen);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, DosenFormActivity.class)));

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
        api.getDosenList().enqueue(new Callback<List<Dosen>>() {
            @Override
            public void onResponse(Call<List<Dosen>> call, Response<List<Dosen>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(DosenActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Dosen> list = response.body() != null ? response.body() : new ArrayList<>();
                DosenAdapter adapter = new DosenAdapter(list, new DosenAdapter.OnItemActionListener() {
                    @Override
                    public void onEdit(Dosen d) {
                        Intent i = new Intent(DosenActivity.this, DosenFormActivity.class);
                        i.putExtra(DosenFormActivity.EXTRA_ID, d.getId());
                        startActivity(i);
                    }

                    @Override
                    public void onDelete(Dosen d) {
                        new AlertDialog.Builder(DosenActivity.this)
                                .setTitle(R.string.konfirmasi_hapus)
                                .setMessage(d.getNama())
                                .setPositiveButton(R.string.ya, (dialog, which) -> deleteDosen(d.getId()))
                                .setNegativeButton(R.string.batal, null)
                                .show();
                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Dosen>> call, Throwable t) {
                Toast.makeText(DosenActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteDosen(int id) {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.deleteDosen(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(DosenActivity.this, R.string.data_dihapus, Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DosenActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
