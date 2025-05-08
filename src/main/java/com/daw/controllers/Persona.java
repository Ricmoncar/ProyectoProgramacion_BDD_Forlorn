/**
 * Clase modelo para Persona
 */
package com.daw.controllers;

import java.sql.Date;
import java.util.List;

public class Persona {
    private Integer id;
    private String nombre;
    private String apellido;
    private Float ancho;
    private Float alto;
    private String descripcionFisica;
    private Float porcentajeGrasaCorporal;
    private String personalidad;
    private Integer oro;
    private Date fechaNacimiento;
    private String profesion;
    private String direccion;
    private Integer razaId;
    private Raza raza;
    private Integer imperioId;
    private Imperio imperio;
    private Integer estadisticasId;
    private Estadisticas estadisticas;
    private List<Arma> armas;
    private List<Armadura> armaduras;
    private List<Herramienta> herramientas;
    private List<Arcana> arcanas;
    
    public Persona() {
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
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public Float getAncho() {
        return ancho;
    }
    
    public void setAncho(Float ancho) {
        this.ancho = ancho;
    }
    
    public Float getAlto() {
        return alto;
    }
    
    public void setAlto(Float alto) {
        this.alto = alto;
    }
    
    public String getDescripcionFisica() {
        return descripcionFisica;
    }
    
    public void setDescripcionFisica(String descripcionFisica) {
        this.descripcionFisica = descripcionFisica;
    }
    
    public Float getPorcentajeGrasaCorporal() {
        return porcentajeGrasaCorporal;
    }
    
    public void setPorcentajeGrasaCorporal(Float porcentajeGrasaCorporal) {
        this.porcentajeGrasaCorporal = porcentajeGrasaCorporal;
    }
    
    public String getPersonalidad() {
        return personalidad;
    }
    
    public void setPersonalidad(String personalidad) {
        this.personalidad = personalidad;
    }
    
    public Integer getOro() {
        return oro;
    }
    
    public void setOro(Integer oro) {
        this.oro = oro;
    }
    
    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public String getProfesion() {
        return profesion;
    }
    
    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public Integer getRazaId() {
        return razaId;
    }
    
    public void setRazaId(Integer razaId) {
        this.razaId = razaId;
    }
    
    public Raza getRaza() {
        return raza;
    }
    
    public void setRaza(Raza raza) {
        this.raza = raza;
    }
    
    public Integer getImperioId() {
        return imperioId;
    }
    
    public void setImperioId(Integer imperioId) {
        this.imperioId = imperioId;
    }
    
    public Imperio getImperio() {
        return imperio;
    }
    
    public void setImperio(Imperio imperio) {
        this.imperio = imperio;
    }
    
    public Integer getEstadisticasId() {
        return estadisticasId;
    }
    
    public void setEstadisticasId(Integer estadisticasId) {
        this.estadisticasId = estadisticasId;
    }
    
    public Estadisticas getEstadisticas() {
        return estadisticas;
    }
    
    public void setEstadisticas(Estadisticas estadisticas) {
        this.estadisticas = estadisticas;
    }
    
    public List<Arma> getArmas() {
        return armas;
    }
    
    public void setArmas(List<Arma> armas) {
        this.armas = armas;
    }
    
    public List<Armadura> getArmaduras() {
        return armaduras;
    }
    
    public void setArmaduras(List<Armadura> armaduras) {
        this.armaduras = armaduras;
    }
    
    public List<Herramienta> getHerramientas() {
        return herramientas;
    }
    
    public void setHerramientas(List<Herramienta> herramientas) {
        this.herramientas = herramientas;
    }
    
    public List<Arcana> getArcanas() {
        return arcanas;
    }
    
    public void setArcanas(List<Arcana> arcanas) {
        this.arcanas = arcanas;
    }
}