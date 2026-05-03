package com.example.crudapi.model;

public class Krs {
    private int id;
    private int mahasiswaId;
    private int matakuliahId;
    private Mahasiswa mahasiswa;
    private Matakuliah matakuliah;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMahasiswaId() {
        return mahasiswaId;
    }

    public void setMahasiswaId(int mahasiswaId) {
        this.mahasiswaId = mahasiswaId;
    }

    public int getMatakuliahId() {
        return matakuliahId;
    }

    public void setMatakuliahId(int matakuliahId) {
        this.matakuliahId = matakuliahId;
    }

    public Mahasiswa getMahasiswa() {
        return mahasiswa;
    }

    public void setMahasiswa(Mahasiswa mahasiswa) {
        this.mahasiswa = mahasiswa;
    }

    public Matakuliah getMatakuliah() {
        return matakuliah;
    }

    public void setMatakuliah(Matakuliah matakuliah) {
        this.matakuliah = matakuliah;
    }
}
