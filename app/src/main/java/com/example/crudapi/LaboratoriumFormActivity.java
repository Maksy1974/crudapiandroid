package com.example.crudapi;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crudapi.api.ApiClient;
import com.example.crudapi.api.ApiService;
import com.example.crudapi.model.Laboratorium;
import com.example.crudapi.model.LaboratoriumPayload;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaboratoriumFormActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "lab_id";

    private int editId = -1;
    private TextInputEditText edtNama;
    private TextInputEditText edtLokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_laboratorium);

        editId = getIntent().getIntExtra(EXTRA_ID, -1);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        if (editId > 0) {
            toolbar.setTitle(R.string.title_edit_lab);
            loadLab(editId);
        } else {
            toolbar.setTitle(R.string.title_tambah_lab);
        }

        edtNama = findViewById(R.id.edtNama);
        edtLokasi = findViewById(R.id.edtLokasi);
        MaterialButton btn = findViewById(R.id.btnSimpan);
        btn.setOnClickListener(v -> simpan());
    }

    private void loadLab(int id) {
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.getLaboratoriumById(id).enqueue(new Callback<Laboratorium>() {
            @Override
            public void onResponse(Call<Laboratorium> call, Response<Laboratorium> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }
                Laboratorium lab = response.body();
                edtNama.setText(lab.getNama());
                if (lab.getLokasi() != null) {
                    edtLokasi.setText(lab.getLokasi());
                }
            }

            @Override
            public void onFailure(Call<Laboratorium> call, Throwable t) {
                // ignore
            }
        });
    }

    private void simpan() {
        String nama = edtNama.getText() != null ? edtNama.getText().toString().trim() : "";
        String lokasi = edtLokasi.getText() != null ? edtLokasi.getText().toString().trim() : "";
        if (nama.isEmpty() || lokasi.isEmpty()) {
            Toast.makeText(this, R.string.isi_semua, Toast.LENGTH_SHORT).show();
            return;
        }

        LaboratoriumPayload payload = new LaboratoriumPayload(nama, lokasi);
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);

        if (editId > 0) {
            api.updateLaboratorium(editId, payload).enqueue(new Callback<Laboratorium>() {
                @Override
                public void onResponse(Call<Laboratorium> call, Response<Laboratorium> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(LaboratoriumFormActivity.this, R.string.data_tersimpan, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(LaboratoriumFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Laboratorium> call, Throwable t) {
                    Toast.makeText(LaboratoriumFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            api.createLaboratorium(payload).enqueue(new Callback<Laboratorium>() {
                @Override
                public void onResponse(Call<Laboratorium> call, Response<Laboratorium> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(LaboratoriumFormActivity.this, R.string.data_tersimpan, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(LaboratoriumFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Laboratorium> call, Throwable t) {
                    Toast.makeText(LaboratoriumFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
