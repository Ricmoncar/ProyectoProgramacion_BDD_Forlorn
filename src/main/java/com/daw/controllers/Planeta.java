package com.daw.controllers;
import java.util.Date;

public class Planeta {

    private Integer id;
    
    private String nombre;
    
    private String ubicacion;
    
    private Boolean habitable = false;
    
    private Float nivelAgua;

    private Date fechaCreacion;
    
    private Float tamanio;
    
    private Float densidad;

    private String descripcion;

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

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Boolean getHabitable() {
        return habitable;
    }

    public void setHabitable(Boolean habitable) {
        this.habitable = habitable;
    }

    public Float getNivelAgua() {
        return nivelAgua;
    }

    public void setNivelAgua(Float nivelAgua) {
        this.nivelAgua = nivelAgua;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Float getTamanio() {
        return tamanio;
    }

    public void setTamanio(Float tamanio) {
        this.tamanio = tamanio;
    }

    public Float getDensidad() {
        return densidad;
    }

    public void setDensidad(Float densidad) {
        this.densidad = densidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}