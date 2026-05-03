package com.example.crudapi.api;

import com.example.crudapi.model.Krs;
import com.example.crudapi.model.KrsPayload;
import com.example.crudapi.model.Mahasiswa;
import com.example.crudapi.model.MahasiswaPayload;
import com.example.crudapi.model.Matakuliah;
import com.example.crudapi.model.MatakuliahPayload;
import com.example.crudapi.model.Prodi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @GET("prodi")
    Call<List<Prodi>> getProdi();

    @POST("prodi")
    Call<Prodi> tambahProdi(@Body Prodi prodi);

    @PUT("prodi/{id}")
    Call<Prodi> updateProdi(@Path("id") int id, @Body Prodi prodi);

    @DELETE("prodi/{id}")
    Call<Void> deleteProdi(@Path("id") int id);

    @GET("mahasiswa")
    Call<List<Mahasiswa>> getMahasiswaList();

    @GET("mahasiswa/{id}")
    Call<Mahasiswa> getMahasiswaById(@Path("id") int id);

    @POST("mahasiswa")
    Call<Mahasiswa> createMahasiswa(@Body MahasiswaPayload body);

    @PUT("mahasiswa/{id}")
    Call<Mahasiswa> updateMahasiswa(@Path("id") int id, @Body MahasiswaPayload body);

    @DELETE("mahasiswa/{id}")
    Call<Void> deleteMahasiswa(@Path("id") int id);

    @GET("matakuliah")
    Call<List<Matakuliah>> getMatakuliah();

    @POST("matakuliah")
    Call<Matakuliah> createMatakuliah(@Body MatakuliahPayload body);

    @PUT("matakuliah/{id}")
    Call<Matakuliah> updateMatakuliah(@Path("id") int id, @Body MatakuliahPayload body);

    @DELETE("matakuliah/{id}")
    Call<Void> deleteMatakuliah(@Path("id") int id);

    @GET("krs")
    Call<List<Krs>> getKrs();

    @POST("krs")
    Call<Krs> createKrs(@Body KrsPayload body);

    @DELETE("krs/{id}")
    Call<Void> deleteKrs(@Path("id") int id);
}
