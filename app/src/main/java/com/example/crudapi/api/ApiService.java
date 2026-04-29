package com.example.crudapi.api;
import com.example.crudapi.model.Prodi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @GET("prodi")
    Call<List<Prodi>> getProdi();

    @POST("prodi")
    Call<Prodi> tambahProdi(@Body Prodi prodi);

    @PUT("prodi/{id}")
    Call<Prodi> updateProdi(@Path("id") int id, @Body Prodi prodi);

    @DELETE("prodi/{id}")
    Call<Void> deleteProdi(@Path("id") int id);
}

