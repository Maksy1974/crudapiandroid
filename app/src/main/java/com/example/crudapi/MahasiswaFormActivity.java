package com.example.crudapi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.crudapi.api.ApiClient;
import com.example.crudapi.api.ApiService;
import com.example.crudapi.model.Mahasiswa;
import com.example.crudapi.model.MahasiswaPayload;
import com.example.crudapi.model.Prodi;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MahasiswaFormActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "mahasiswa_id";
    private static final String AUTHORITY = "com.example.crudapi.fileprovider";

    private int editId = -1;
    private final List<Integer> prodiIds = new ArrayList<>();
    private Spinner spinnerProdi;
    private TextInputEditText edtNama;
    private TextInputEditText edtNim;
    private TextInputEditText edtJurusan;
    private MaterialButton btnAmbilFoto;
    private ImageView imgPreview;
    private View txtFotoHintBaru;

    @Nullable
    private String uploadedFotoUrl;

    /** File JPEG hasil TangPicture terakhir; dipakai saat mengunggah ke Cloudinary. */
    @Nullable
    private File capturedFile;

    private Uri cameraTargetUri;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (Boolean.TRUE.equals(granted)) {
                    launchCameraCapture();
                } else {
                    Toast.makeText(this, R.string.err_upload_camera, Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (!success || capturedFile == null || !capturedFile.exists()) {
                    return;
                }
                Toast.makeText(this, R.string.mengunggah_foto, Toast.LENGTH_SHORT).show();
                CloudinaryUploader.uploadMahasiswaPhoto(getApplicationContext(), capturedFile, new CloudinaryUploader.UploadListener() {
                    @Override
                    public void onUploaded(String secureUrl) {
                        MahasiswaFormActivity.this.runOnUiThread(() -> {
                            uploadedFotoUrl = secureUrl;
                            imgPreview.setVisibility(View.VISIBLE);
                            Glide.with(imgPreview.getContext()).load(secureUrl).centerCrop().into(imgPreview);
                        });
                    }

                    @Override
                    public void onError(String message) {
                        MahasiswaFormActivity.this.runOnUiThread(() ->
                                Toast.makeText(MahasiswaFormActivity.this, message, Toast.LENGTH_LONG).show());
                    }
                });
            });

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
        btnAmbilFoto = findViewById(R.id.btnAmbilFoto);
        imgPreview = findViewById(R.id.imgPreview);
        txtFotoHintBaru = findViewById(R.id.txtFotoHintBaru);

        if (editId > 0) {
            btnAmbilFoto.setVisibility(View.GONE);
            txtFotoHintBaru.setVisibility(View.GONE);
            uploadedFotoUrl = null;
            imgPreview.setVisibility(View.GONE);
        } else {
            btnAmbilFoto.setVisibility(View.VISIBLE);
            txtFotoHintBaru.setVisibility(View.VISIBLE);
            imgPreview.setVisibility(View.GONE);
        }

        btnAmbilFoto.setOnClickListener(v -> mulaiAmbilFoto());

        MaterialButton btnSimpan = findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(v -> simpan());

        loadProdiSpinnerThenMaybeMahasiswa();
    }

    private void mulaiAmbilFoto() {
        if (!CloudinaryUploader.isConfigured(this)) {
            Toast.makeText(this, R.string.err_cloudinary_config, Toast.LENGTH_LONG).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }
        launchCameraCapture();
    }

    private void launchCameraCapture() {
        try {
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (dir == null) dir = getFilesDir();
            if (!dir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            }
            String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            capturedFile = File.createTempFile("MHS_" + stamp + "_", ".jpg", dir);
            cameraTargetUri = FileProvider.getUriForFile(this, AUTHORITY, capturedFile);
            takePictureLauncher.launch(cameraTargetUri);
        } catch (IOException e) {
            Toast.makeText(this, R.string.err_upload_camera, Toast.LENGTH_SHORT).show();
        }
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

                imgPreview.setVisibility(View.VISIBLE);
                if (m.getFoto() != null && !m.getFoto().isEmpty()) {
                    Glide.with(imgPreview.getContext()).load(m.getFoto()).centerCrop().into(imgPreview);
                } else {
                    imgPreview.setImageResource(R.mipmap.ic_launcher);
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

        if (editId <= 0) {
            if (uploadedFotoUrl == null || uploadedFotoUrl.isEmpty()) {
                Toast.makeText(this, R.string.err_need_photo_baru, Toast.LENGTH_LONG).show();
                return;
            }
        }

        MahasiswaPayload payload = new MahasiswaPayload(nama, nim, jurusan, prodiId);
        if (uploadedFotoUrl != null && !uploadedFotoUrl.isEmpty()) {
            payload.setFotoUrl(uploadedFotoUrl);
        }

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
