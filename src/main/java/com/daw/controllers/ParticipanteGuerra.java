package com.daw.controllers;

public class ParticipanteGuerra {
    private Integer imperioId;
    private String imperioNombre;
    private String rol;
    private Boolean ganador;
    
    public ParticipanteGuerra() {
    }
    
    public Integer getImperioId() {
        return imperioId;
    }
    
    public void setImperioId(Integer imperioId) {
        this.imperioId = imperioId;
    }
    
    public String getImperioNombre() {
        return imperioNombre;
    }
    
    public void setImperioNombre(String imperioNombre) {
        this.imperioNombre = imperioNombre;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public Boolean getGanador() {
        return ganador;
    }
    
    public void setGanador(Boolean ganador) {
        this.ganador = ganador;
    }
}