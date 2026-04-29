package com.example.crudapi;

//package com.example.crudapi.activity;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crudapi.R;
import com.example.crudapi.api.*;
import com.example.crudapi.model.Prodi;

import retrofit2.*;

public class TambahProdiActivity extends AppCompatActivity {

    EditText edtNama;
    Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_prodi);

        edtNama = findViewById(R.id.edtNama);
        btnSimpan = findViewById(R.id.btnSimpan);

        btnSimpan.setOnClickListener(v -> simpanData());
    }

    private void simpanData() {
        String nama = edtNama.getText().toString();

        if (nama.isEmpty()) {
            edtNama.setError("Tidak boleh kosong");
            return;
        }

        ApiService api = ApiClient.getRetrofit().create(ApiService.class);

        Prodi prodi = new Prodi();
        prodi.setNama(nama);

        api.tambahProdi(prodi).enqueue(new Callback<Prodi>() {
            @Override
            public void onResponse(Call<Prodi> call, Response<Prodi> response) {
                Toast.makeText(TambahProdiActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                finish(); // kembali ke MainActivity
            }

            @Override
            public void onFailure(Call<Prodi> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}