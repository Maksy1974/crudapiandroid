package com.example.crudapi;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crudapi.api.ApiClient;
import com.example.crudapi.api.ApiService;
import com.example.crudapi.model.Mahasiswa;
import com.example.crudapi.model.MahasiswaPayload;
import com.example.crudapi.model.Prodi;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MahasiswaFormActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "mahasiswa_id";

    private int editId = -1;
    private final List<Integer> prodiIds = new ArrayList<>();
    private Spinner spinnerProdi;
    private TextInputEditText edtNama;
    private TextInputEditText edtNim;
    private TextInputEditText edtJurusan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_mahasiswa);

        editId = getIntent().getIntExtra(EXTRA_ID, -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        if (editId > 0) {
            toolbar.setTitle(R.string.title_edit_mahasiswa);
        } else {
            toolbar.setTitle(R.string.title_tambah_mahasiswa);
        }

        edtNama = findViewById(R.id.edtNama);
        edtNim = findViewById(R.id.edtNim);
        edtJurusan = findViewById(R.id.edtJurusan);
        spinnerProdi = findViewById(R.id.spinnerProdi);
        MaterialButton btnSimpan = findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(v -> simpan());

        loadProdiSpinnerThenMaybeMahasiswa();
    }

    private void loadProdiSpinnerThenMaybeMahasiswa() {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.getProdi().enqueue(new Callback<List<Prodi>>() {
            @Override
            public void onResponse(Call<List<Prodi>> call, Response<List<Prodi>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MahasiswaFormActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> labels = new ArrayList<>();
                prodiIds.clear();
                labels.add(getString(R.string.pilih_prodi));
                prodiIds.add(-1);
                for (Prodi p : response.body()) {
                    labels.add(p.getNama());
                    prodiIds.add(p.getId());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        MahasiswaFormActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        labels
                );
                spinnerProdi.setAdapter(adapter);

                if (editId > 0) {
                    loadMahasiswaDetail(editId);
                }
            }

            @Override
            public void onFailure(Call<List<Prodi>> call, Throwable t) {
                Toast.makeText(MahasiswaFormActivity.this, R.string.gagal_muat, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMahasiswaDetail(int id) {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.getMahasiswaById(id).enqueue(new Callback<Mahasiswa>() {
            @Override
            public void onResponse(Call<Mahasiswa> call, Response<Mahasiswa> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }
                Mahasiswa m = response.body();
                edtNama.setText(m.getNama());
                edtNim.setText(m.getNim());
                edtJurusan.setText(m.getJurusan());
                int idx = prodiIds.indexOf(m.getProdiId());
                if (idx >= 0) {
                    spinnerProdi.setSelection(idx);
                }
            }

            @Override
            public void onFailure(Call<Mahasiswa> call, Throwable t) {
                // ignore
            }
        });
    }

    private void simpan() {
        String nama = text(edtNama);
        String nim = text(edtNim);
        String jurusan = text(edtJurusan);
        int pos = spinnerProdi.getSelectedItemPosition();
        int prodiId = pos >= 0 && pos < prodiIds.size() ? prodiIds.get(pos) : -1;

        if (nama.isEmpty() || nim.isEmpty() || jurusan.isEmpty() || prodiId <= 0) {
            Toast.makeText(this, R.string.isi_semua, Toast.LENGTH_SHORT).show();
            return;
        }

        MahasiswaPayload payload = new MahasiswaPayload(nama, nim, jurusan, prodiId);
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);

        if (editId > 0) {
            api.updateMahasiswa(editId, payload).enqueue(new Callback<Mahasiswa>() {
                @Override
                public void onResponse(Call<Mahasiswa> call, Response<Mahasiswa> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MahasiswaFormActivity.this, R.string.data_tersimpan, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(MahasiswaFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Mahasiswa> call, Throwable t) {
                    Toast.makeText(MahasiswaFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            api.createMahasiswa(payload).enqueue(new Callback<Mahasiswa>() {
                @Override
                public void onResponse(Call<Mahasiswa> call, Response<Mahasiswa> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MahasiswaFormActivity.this, R.string.data_tersimpan, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(MahasiswaFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Mahasiswa> call, Throwable t) {
                    Toast.makeText(MahasiswaFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private static String text(TextInputEditText e) {
        return e.getText() != null ? e.getText().toString().trim() : "";
    }
}
