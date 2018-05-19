package com.adans.app_10;

public class PerfModelo {

    private String ts,ax,ay,az,gx,gy,gz,agx,agy,agz,log,lat,alt,nosats;


    public PerfModelo(String ts,String ax,String ay,String az,String gx,String gy,String gz,
                      String agx,String agy,String agz,String log,String lat,String alt,String nosats) {

        this.ts = ts;
        this.ax = ax;
        this.ay = ay;
        this.az = az;
        this.gx = gx;
        this.gy = gy;
        this.gz = gz;
        this.agx = gx;
        this.agy = gy;
        this.agz = gz;
        this.log= log;
        this.lat= lat;
        this.alt= alt;
        this.nosats= nosats;



    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getAx() {
        return ax;
    }

    public void setAx(String ax) {
        this.ax = ax;
    }

    public String getAy() {
        return ay;
    }

    public void setAy(String ay) {
        this.ay = ay;
    }

    public String getAz() {
        return az;
    }

    public void setAz(String az) {
        this.az = az;
    }

    public String getGx() {
        return gx;
    }

    public void setGx(String gx) {
        this.gx = gx;
    }

    public String getGy() {
        return gy;
    }

    public void setGy(String gy) {
        this.gy = gy;
    }

    public String getGz() {
        return gz;
    }

    public void setGz(String gz) {
        this.gz = gz;
    }

    public String getAgx() {
        return agx;
    }

    public void setAgx(String agx) {
        this.agx = agx;
    }

    public String getAgy() {
        return agy;
    }

    public void setAgy(String agy) {
        this.agy = agy;
    }

    public String getAgz() {
        return agz;
    }

    public void setAgz(String agz) {
        this.agz = agz;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getNosats() {
        return nosats;
    }

    public void setNosats(String nosats) {
        this.nosats = nosats;
    }
}
