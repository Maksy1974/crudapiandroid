package com.example.crudapi.model;

public class LaboratoriumPayload {
    private final String nama;
    private final String lokasi;

    public LaboratoriumPayload(String nama, String lokasi) {
        this.nama = nama;
        this.lokasi = lokasi;
    }
}
