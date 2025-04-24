package com.daw.controllers;

public class Bioma {
    private Integer id;
    private String nombre;
    private Integer continenteId;
    private String continenteNombre;
    private String clima;
    private Float porcentajeHumedad;
    private String precipitaciones;
    private Float temperaturaMedia;
    
    public Bioma() {
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
    
    public Integer getContinenteId() {
        return continenteId;
    }
    
    public void setContinenteId(Integer continenteId) {
        this.continenteId = continenteId;
    }
    
    public String getContinenteNombre() {
        return continenteNombre;
    }
    
    public void setContinenteNombre(String continenteNombre) {
        this.continenteNombre = continenteNombre;
    }
    
    public String getClima() {
        return clima;
    }
    
    public void setClima(String clima) {
        this.clima = clima;
    }
    
    public Float getPorcentajeHumedad() {
        return porcentajeHumedad;
    }
    
    public void setPorcentajeHumedad(Float porcentajeHumedad) {
        this.porcentajeHumedad = porcentajeHumedad;
    }
    
    public String getPrecipitaciones() {
        return precipitaciones;
    }
    
    public void setPrecipitaciones(String precipitaciones) {
        this.precipitaciones = precipitaciones;
    }
    
    public Float getTemperaturaMedia() {
        return temperaturaMedia;
    }
    
    public void setTemperaturaMedia(Float temperaturaMedia) {
        this.temperaturaMedia = temperaturaMedia;
    }
}