package com.example.crudapi;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crudapi.api.ApiClient;
import com.example.crudapi.api.ApiService;
import com.example.crudapi.model.Matakuliah;
import com.example.crudapi.model.MatakuliahPayload;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatakuliahFormActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "mk_id";
    public static final String EXTRA_NAMA = "mk_nama";

    private int editId = -1;
    private TextInputEditText edtNama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_matakuliah);

        editId = getIntent().getIntExtra(EXTRA_ID, -1);
        String namaAwal = getIntent().getStringExtra(EXTRA_NAMA);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        if (editId > 0) {
            toolbar.setTitle(R.string.title_edit_mk);
        } else {
            toolbar.setTitle(R.string.title_tambah_mk);
        }

        edtNama = findViewById(R.id.edtNama);
        if (namaAwal != null) {
            edtNama.setText(namaAwal);
        }

        MaterialButton btn = findViewById(R.id.btnSimpan);
        btn.setOnClickListener(v -> simpan());
    }

    private void simpan() {
        String nama = edtNama.getText() != null ? edtNama.getText().toString().trim() : "";
        if (nama.isEmpty()) {
            edtNama.setError(getString(R.string.isi_semua));
            return;
        }

        MatakuliahPayload payload = new MatakuliahPayload(nama);
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);

        if (editId > 0) {
            api.updateMatakuliah(editId, payload).enqueue(new Callback<Matakuliah>() {
                @Override
                public void onResponse(Call<Matakuliah> call, Response<Matakuliah> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MatakuliahFormActivity.this, R.string.data_tersimpan, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(MatakuliahFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Matakuliah> call, Throwable t) {
                    Toast.makeText(MatakuliahFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            api.createMatakuliah(payload).enqueue(new Callback<Matakuliah>() {
                @Override
                public void onResponse(Call<Matakuliah> call, Response<Matakuliah> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MatakuliahFormActivity.this, R.string.data_tersimpan, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(MatakuliahFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Matakuliah> call, Throwable t) {
                    Toast.makeText(MatakuliahFormActivity.this, R.string.gagal_simpan, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
