/**
 * Clase modelo para Armadura
 */
package com.daw.controllers;

import java.sql.Date;

public class Armadura {
    private Integer id;
    private String nombre;
    private String bufoEstadisticas;
    private String material;
    private String descripcion;
    private Float peso;
    private Float pvp;
    private String origen;
    private Date fechaCreacion;
    private Boolean equipada; // Campo adicional para UI
    
    public Armadura() {
    }
    
    // Getters y Setters
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
    
    public String getBufoEstadisticas() {
        return bufoEstadisticas;
    }
    
    public void setBufoEstadisticas(String bufoEstadisticas) {
        this.bufoEstadisticas = bufoEstadisticas;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public void setMaterial(String material) {
        this.material = material;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Float getPeso() {
        return peso;
    }
    
    public void setPeso(Float peso) {
        this.peso = peso;
    }
    
    public Float getPvp() {
        return pvp;
    }
    
    public void setPvp(Float pvp) {
        this.pvp = pvp;
    }
    
    public String getOrigen() {
        return origen;
    }
    
    public void setOrigen(String origen) {
        this.origen = origen;
    }
    
    public Date getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public Boolean getEquipada() {
        return equipada;
    }
    
    public void setEquipada(Boolean equipada) {
        this.equipada = equipada;
    }
}
