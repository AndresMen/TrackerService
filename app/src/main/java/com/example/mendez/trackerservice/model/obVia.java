package com.example.mendez.trackerservice.model;

public class obVia {

    private String fecha;
    private String hora;
    private String orig;
    private String dest;
    private String oriLit;
    private String desLit;
    private String est;
    private String nomCli;

    public obVia(String fecha, String hora, String orig, String dest, String oriLit, String desLit, String est, String nomCli) {
        this.fecha = fecha;
        this.hora = hora;
        this.orig = orig;
        this.dest = dest;
        this.oriLit = oriLit;
        this.desLit = desLit;
        this.est = est;
        this.nomCli = nomCli;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getOrig() {
        return orig;
    }

    public void setOrig(String orig) {
        this.orig = orig;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getOriLit() {
        return oriLit;
    }

    public void setOriLit(String oriLit) {
        this.oriLit = oriLit;
    }

    public String getDesLit() {
        return desLit;
    }

    public void setDesLit(String desLit) {
        this.desLit = desLit;
    }

    public String getEst() {
        return est;
    }

    public void setEst(String est) {
        this.est = est;
    }

    public String getNomCli() {
        return nomCli;
    }

    public void setNomCli(String nomCli) {
        this.nomCli = nomCli;
    }
}
