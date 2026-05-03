package com.example.crudapi.model;

public class DosenPayload {
    private final String nama;
    private final String nip;
    private final String jurusan;
    private final int prodiId;
    private String foto;

    public DosenPayload(String nama, String nip, String jurusan, int prodiId) {
        this.nama = nama;
        this.nip = nip;
        this.jurusan = jurusan;
        this.prodiId = prodiId;
    }

    public void setFotoUrl(String foto) {
        this.foto = foto;
    }

    @SuppressWarnings("unused")
    public String getFoto() {
        return foto;
    }
}
