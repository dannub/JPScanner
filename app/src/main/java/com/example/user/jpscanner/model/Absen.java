package com.example.user.jpscanner.model;

public class Absen {
    private int jenis;
    private String name,time, tgl;

    public String getName_Abs() {
        return name;
    }

    public void setName_Abs(String name) {
        this.name = name;
    }

    public int getJenis_Abs() {
        return jenis;
    }

    public void setJenis_Abs(int jenis) {
        this.jenis = jenis;
    }

    public String getTime_Abs() {
        return time;
    }

    public void setTime_Abs(String time) {
        this.time = time;
    }

    public String getTgl_Abs() {
        return tgl;
    }

    public void setTgl_Abs(String tgl) {
        this.tgl = tgl;
    }

}
