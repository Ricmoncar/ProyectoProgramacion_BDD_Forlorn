/**
 * Clase modelo para Arcana
 */
package com.daw.controllers;

import java.sql.Date;

public class Arcana {
    private Integer id;
    private String tipo;
    private String maestria;
    private String dificultad;
    private Date fecha;
    
    public Arcana() {
    }
    
    // Getters y Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getMaestria() {
        return maestria;
    }
    
    public void setMaestria(String maestria) {
        this.maestria = maestria;
    }
    
    public String getDificultad() {
        return dificultad;
    }
    
    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}