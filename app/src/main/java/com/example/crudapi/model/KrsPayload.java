package com.example.crudapi.model;

public class KrsPayload {
    private final int mahasiswaId;
    private final int matakuliahId;

    public KrsPayload(int mahasiswaId, int matakuliahId) {
        this.mahasiswaId = mahasiswaId;
        this.matakuliahId = matakuliahId;
    }
}
