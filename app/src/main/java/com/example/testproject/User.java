package com.example.testproject;

public class User {

    private int human;
    private int open;
    private int nf;
    private String cctv_ip;
    private int human_onoff;
    private int open_onoff;
    private int flame;
    private int flame_onoff;
    private int start;



    private int temp;
    private int humi;
    private int temp_humi_onoff;




    public User(){}


    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getHuman() {
        return human;
    }

    public void setHuman(int human) {
        this.human = human;
    }

    public int getNf() {
        return nf;
    }

    public void setNf(int nf) {
        this.nf = nf;
    }

    public String getCctv_ip() {
        return cctv_ip;
    }

    public void setCctv_ip(String cctv_ip) {
        this.cctv_ip = cctv_ip;
    }

    public int getHuman_onoff() {
        return human_onoff;
    }

    public void setHuman_onoff(int human_onoff) {
        this.human_onoff = human_onoff;
    }

    public int getOpen_onoff() {
        return open_onoff;
    }

    public void setOpen_onoff(int open_onoff) {
        this.open_onoff = open_onoff;
    }

    public int getFlame() {
        return flame;
    }

    public void setFlame(int flame) {
        this.flame = flame;
    }

    public int getFlame_onoff() {
        return flame_onoff;
    }

    public void setFlame_onoff(int flame_onoff) {
        this.flame_onoff = flame_onoff;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getHumi() {
        return humi;
    }

    public void setHumi(int humi) {
        this.humi = humi;
    }

    public int getTemp_humi_onoff() {
        return temp_humi_onoff;
    }

    public void setTemp_humi_onoff(int temp_humi_onoff) {
        this.temp_humi_onoff = temp_humi_onoff;
    }
}
