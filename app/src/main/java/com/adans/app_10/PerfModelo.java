package com.adans.app_10;

public class PerfModelo {

    private String codigo,curso,carrera,gx,gy,gz,log,lat;


    public PerfModelo(String codigo, String curso, String carrera, String gx, String gy, String gz, String lat, String log) {
        this.codigo = codigo;
        this.curso = curso;
        this.carrera = carrera;
        this.gx = gx;
        this.gy = gy;
        this.gz = gz;
        this.log= log;
        this.lat= lat;

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
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
}
