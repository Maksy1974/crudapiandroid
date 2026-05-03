package com.example.crudapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudapi.adapter.LaboratoriumAdapter;
import com.example.crudapi.api.ApiClient;
import com.example.crudapi.api.ApiService;
import com.example.crudapi.model.Laboratorium;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaboratoriumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laboratorium);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(this, LaboratoriumFormActivity.class)));

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
        api.getLaboratoriumList().enqueue(new Callback<List<Laboratorium>>() {
            @Override
            public void onResponse(Call<List<Laboratorium>> call, Response<List<Laboratorium>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(LaboratoriumActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Laboratorium> list = response.body() != null ? response.body() : new ArrayList<>();
                LaboratoriumAdapter adapter = new LaboratoriumAdapter(list, new LaboratoriumAdapter.OnItemActionListener() {
                    @Override
                    public void onEdit(Laboratorium lab) {
                        Intent i = new Intent(LaboratoriumActivity.this, LaboratoriumFormActivity.class);
                        i.putExtra(LaboratoriumFormActivity.EXTRA_ID, lab.getId());
                        startActivity(i);
                    }

                    @Override
                    public void onDelete(Laboratorium lab) {
                        new AlertDialog.Builder(LaboratoriumActivity.this)
                                .setTitle(R.string.konfirmasi_hapus)
                                .setMessage(lab.getNama())
                                .setPositiveButton(R.string.ya, (dialog, which) -> deleteLab(lab.getId()))
                                .setNegativeButton(R.string.batal, null)
                                .show();
                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Laboratorium>> call, Throwable t) {
                Toast.makeText(LaboratoriumActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteLab(int id) {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.deleteLaboratorium(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(LaboratoriumActivity.this, R.string.data_dihapus, Toast.LENGTH_SHORT).show();
                loadData();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(LaboratoriumActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
