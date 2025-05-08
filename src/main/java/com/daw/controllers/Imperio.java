package com.daw.controllers;

import java.sql.Date;

public class Imperio {
    private Integer id;
    private String nombre;
    private Integer poblacion;
    private String descripcion;
    private Date fechaCreacion;
    private String lider;
    private String ideologia;
    private Float gdp;
    private Float tamanio;
    private Boolean enGuerra;
    
    public Imperio() {
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
    
    public Integer getPoblacion() {
        return poblacion;
    }
    
    public void setPoblacion(Integer poblacion) {
        this.poblacion = poblacion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public String getLider() {
        return lider;
    }
    
    public void setLider(String lider) {
        this.lider = lider;
    }
    
    public String getIdeologia() {
        return ideologia;
    }
    
    public void setIdeologia(String ideologia) {
        this.ideologia = ideologia;
    }
    
    public Float getGdp() {
        return gdp;
    }
    
    public void setGdp(Float gdp) {
        this.gdp = gdp;
    }
    
    public Float getTamanio() {
        return tamanio;
    }
    
    public void setTamanio(Float tamanio) {
        this.tamanio = tamanio;
    }
    
    public Boolean getEnGuerra() {
        return enGuerra;
    }
    
    public void setEnGuerra(Boolean enGuerra) {
        this.enGuerra = enGuerra;
    }
}