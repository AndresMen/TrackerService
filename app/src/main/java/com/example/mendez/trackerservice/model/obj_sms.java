package com.example.mendez.trackerservice.model;

public class obj_sms {
    private   String mmessage;
    private   String mfecha;
    private   String mhora;
    private   String mnombreCli;
    private  String tipo;
    private boolean checked;

    public obj_sms(String mmessage, String mfecha, String mhora, String mnombreCli, String tipo) {
        this.mmessage = mmessage;
        this.mfecha = mfecha;
        this.mhora = mhora;
        this.mnombreCli = mnombreCli;
        this.tipo = tipo;
    }

    public String getMmessage() {
        return mmessage;
    }

    public void setMmessage(String mmessage) {
        this.mmessage = mmessage;
    }

    public String getMhora() {
        return mhora;
    }

    public void setMhora(String mhora) {
        this.mhora = mhora;
    }

    public String getMfecha() {
        return mfecha;
    }

    public void setMfecha(String mfecha) {
        this.mfecha = mfecha;
    }

    public String getMnombreCli() {
        return mnombreCli;
    }

    public void setMnombreCli(String mnombreCli) {
        this.mnombreCli = mnombreCli;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
