package com.example.crudapi;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crudapi.api.ApiClient;
import com.example.crudapi.api.ApiService;
import com.example.crudapi.model.Krs;
import com.example.crudapi.model.KrsPayload;
import com.example.crudapi.model.Mahasiswa;
import com.example.crudapi.model.Matakuliah;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KrsFormActivity extends AppCompatActivity {

    private final List<Integer> mahasiswaIds = new ArrayList<>();
    private final List<Integer> mkIds = new ArrayList<>();
    private Spinner spinnerMhs;
    private Spinner spinnerMk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_krs);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_tambah_krs);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        spinnerMhs = findViewById(R.id.spinnerMahasiswa);
        spinnerMk = findViewById(R.id.spinnerMatakuliah);
        MaterialButton btn = findViewById(R.id.btnSimpan);
        btn.setOnClickListener(v -> simpan());

        loadMahasiswaThenMk();
    }

    private void loadMahasiswaThenMk() {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.getMahasiswaList().enqueue(new Callback<List<Mahasiswa>>() {
            @Override
            public void onResponse(Call<List<Mahasiswa>> call, Response<List<Mahasiswa>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(KrsFormActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> labels = new ArrayList<>();
                mahasiswaIds.clear();
                labels.add(getString(R.string.pilih_placeholder_mhs));
                mahasiswaIds.add(-1);
                for (Mahasiswa m : response.body()) {
                    labels.add(m.getNim() + " — " + m.getNama());
                    mahasiswaIds.add(m.getId());
                }
                spinnerMhs.setAdapter(new ArrayAdapter<>(
                        KrsFormActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        labels
                ));
                loadMk();
            }

            @Override
            public void onFailure(Call<List<Mahasiswa>> call, Throwable t) {
                Toast.makeText(KrsFormActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMk() {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.getMatakuliah().enqueue(new Callback<List<Matakuliah>>() {
            @Override
            public void onResponse(Call<List<Matakuliah>> call, Response<List<Matakuliah>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(KrsFormActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> labels = new ArrayList<>();
                mkIds.clear();
                labels.add(getString(R.string.pilih_placeholder_mk));
                mkIds.add(-1);
                for (Matakuliah mk : response.body()) {
                    labels.add(mk.getNama());
                    mkIds.add(mk.getId());
                }
                spinnerMk.setAdapter(new ArrayAdapter<>(
                        KrsFormActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        labels
                ));
            }

            @Override
            public void onFailure(Call<List<Matakuliah>> call, Throwable t) {
                Toast.makeText(KrsFormActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void simpan() {
        int pM = spinnerMhs.getSelectedItemPosition();
        int pK = spinnerMk.getSelectedItemPosition();
        int mhsId = pM >= 0 && pM < mahasiswaIds.size() ? mahasiswaIds.get(pM) : -1;
        int mkId = pK >= 0 && pK < mkIds.size() ? mkIds.get(pK) : -1;
        if (mhsId <= 0 || mkId <= 0) {
            Toast.makeText(this, R.string.pilih_mahasiswa_mk, Toast.LENGTH_SHORT).show();
            return;
        }

        KrsPayload payload = new KrsPayload(mhsId, mkId);
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.createKrs(payload).enqueue(new Callback<Krs>() {
            @Override
            public void onResponse(Call<Krs> call, Response<Krs> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(KrsFormActivity.this, R.string.data_tersimpan, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(KrsFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Krs> call, Throwable t) {
                Toast.makeText(KrsFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
