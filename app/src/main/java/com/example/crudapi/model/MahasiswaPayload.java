package com.example.crudapi.model;

public class MahasiswaPayload {
    private String nama;
    private String nim;
    private String jurusan;
    private int prodiId;

    public MahasiswaPayload(String nama, String nim, String jurusan, int prodiId) {
        this.nama = nama;
        this.nim = nim;
        this.jurusan = jurusan;
        this.prodiId = prodiId;
    }
}
