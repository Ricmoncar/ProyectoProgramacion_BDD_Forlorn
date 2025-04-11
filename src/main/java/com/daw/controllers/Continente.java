package com.daw.controllers;

public class Continente {
    private Integer id;
    private String nombre;
    private Integer planetaId;
    private String planetaNombre;
    private String hemisferio;
    private String clima;
    private Float tamanio;
    private Boolean habitable;
    private String descripcion;
    
    public Continente() {
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public Integer getPlanetaId() {
        return planetaId;
    }
    
    public void setPlanetaId(Integer planetaId) {
        this.planetaId = planetaId;
    }
    
    public String getPlanetaNombre() {
        return planetaNombre;
    }
    
    public void setPlanetaNombre(String planetaNombre) {
        this.planetaNombre = planetaNombre;
    }
    
    public String getHemisferio() {
        return hemisferio;
    }
    
    public void setHemisferio(String hemisferio) {
        this.hemisferio = hemisferio;
    }
    
    public String getClima() {
        return clima;
    }
    
    public void setClima(String clima) {
        this.clima = clima;
    }
    
    public Float getTamanio() {
        return tamanio;
    }
    
    public void setTamanio(Float tamanio) {
        this.tamanio = tamanio;
    }
    
    public Boolean getHabitable() {
        return habitable;
    }
    
    public void setHabitable(Boolean habitable) {
        this.habitable = habitable;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}