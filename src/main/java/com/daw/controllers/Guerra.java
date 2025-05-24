package com.daw.controllers;

import java.sql.Date;
import java.util.List;
import java.util.ArrayList;
public class Guerra {
    private Integer id;
    private String nombre;
    private Date fechaInicio;
    private Date fechaFin;
    private String descripcion;
    private List<ParticipanteGuerra> imperiosParticipantes;
    

     public Guerra() {
        // Inicializar la lista para evitar NullPointerExceptions si se accede antes de ser seteada
        this.imperiosParticipantes = new ArrayList<>(); 
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
    
    public Date getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public Date getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<ParticipanteGuerra> getImperiosParticipantes() {
        return imperiosParticipantes;
    }
    
    public void setImperiosParticipantes(List<ParticipanteGuerra> imperiosParticipantes) {
        this.imperiosParticipantes = imperiosParticipantes;
    }
}