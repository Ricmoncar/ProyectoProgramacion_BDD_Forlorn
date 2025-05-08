
package com.daw.controllers;
import java.sql.Date;

public class Raza {
    private Integer id;
    private String nombre;
    private String descripcionFisica;
    private Date fechaConcepcion;
    private Float alturaPromedia;
    private Float anchoPromedio;
    private Estadisticas estadisticasBase;
    
    public Raza() {
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
    
    public String getDescripcionFisica() {
        return descripcionFisica;
    }
    
    public void setDescripcionFisica(String descripcionFisica) {
        this.descripcionFisica = descripcionFisica;
    }
    
    public Date getFechaConcepcion() {
        return fechaConcepcion;
    }
    
    public void setFechaConcepcion(Date fechaConcepcion) {
        this.fechaConcepcion = fechaConcepcion;
    }
    
    public Float getAlturaPromedia() {
        return alturaPromedia;
    }
    
    public void setAlturaPromedia(Float alturaPromedia) {
        this.alturaPromedia = alturaPromedia;
    }
    
    public Float getAnchoPromedio() {
        return anchoPromedio;
    }
    
    public void setAnchoPromedio(Float anchoPromedio) {
        this.anchoPromedio = anchoPromedio;
    }
    
    public Estadisticas getEstadisticasBase() {
        return estadisticasBase;
    }
    
    public void setEstadisticasBase(Estadisticas estadisticasBase) {
        this.estadisticasBase = estadisticasBase;
    }
}