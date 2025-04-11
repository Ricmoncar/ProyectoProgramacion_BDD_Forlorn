package com.daw.controllers;

import java.sql.SQLException;
import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daw.services.Servicio;

@RestController
public class AplicacionSQLController {
    
    @Autowired
    private Servicio servicio;
    
    // Endpoints para planetas
    @GetMapping("/consulta_planetas")
    public ResponseEntity<?> buscarPlanetas(@RequestParam(required = false, defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok().body(servicio.buscarPlanetas(nombre));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/aniadir_planeta")
    public ResponseEntity<?> aniadirPlaneta(@RequestParam String nombre, @RequestParam String ubicacion, @RequestParam Boolean habitable, @RequestParam Float nivelAgua, @RequestParam Date fechaCreacion, @RequestParam Float tamanio, @RequestParam Float densidad, @RequestParam String descripcion) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirPlaneta(nombre, ubicacion, habitable, nivelAgua, fechaCreacion, tamanio, densidad, descripcion));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/eliminar_planeta")
    public ResponseEntity<?> eliminarPlaneta(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarPlaneta(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/actualizar_planeta")
    public ResponseEntity<?> actualizarPlaneta(@RequestParam Integer id, @RequestParam String nombre, @RequestParam String ubicacion, @RequestParam Boolean habitable, @RequestParam Float nivelAgua, @RequestParam Date fechaCreacion, @RequestParam Float tamanio, @RequestParam Float densidad, @RequestParam String descripcion) {
        try {
            return ResponseEntity.ok().body(servicio.actualizarPlaneta(id, nombre, ubicacion, habitable, nivelAgua, fechaCreacion, tamanio, densidad, descripcion));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/listar_planetas")
    public ResponseEntity<?> listarPlanetas() {
        try {
            return ResponseEntity.ok().body(servicio.listarPlanetas());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    // NUEVOS ENDPOINTS PARA CONTINENTES
    
    @GetMapping("/consulta_continentes")
    public ResponseEntity<?> buscarContinentes(@RequestParam(required = false, defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok().body(servicio.buscarContinentes(nombre));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/aniadir_continente")
    public ResponseEntity<?> aniadirContinente(
            @RequestParam String nombre, 
            @RequestParam Integer planetaId, 
            @RequestParam String hemisferio, 
            @RequestParam String clima, 
            @RequestParam Float tamanio, 
            @RequestParam Boolean habitable, 
            @RequestParam String descripcion) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirContinente(nombre, planetaId, hemisferio, clima, tamanio, habitable, descripcion));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/eliminar_continente")
    public ResponseEntity<?> eliminarContinente(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarContinente(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/actualizar_continente")
    public ResponseEntity<?> actualizarContinente(
            @RequestParam Integer id,
            @RequestParam String nombre, 
            @RequestParam Integer planetaId, 
            @RequestParam String hemisferio, 
            @RequestParam String clima, 
            @RequestParam Float tamanio, 
            @RequestParam Boolean habitable, 
            @RequestParam String descripcion) {
        try {
            return ResponseEntity.ok().body(servicio.actualizarContinente(id, nombre, planetaId, hemisferio, clima, tamanio, habitable, descripcion));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/listar_continentes")
    public ResponseEntity<?> listarContinentes() {
        try {
            return ResponseEntity.ok().body(servicio.listarContinentes());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/obtener_continente")
    public ResponseEntity<?> obtenerContinentePorId(@RequestParam Integer id) {
        try {
            Continente continente = servicio.obtenerContinentePorId(id);
            if (continente != null) {
                return ResponseEntity.ok().body(continente);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Continente no encontrado");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @GetMapping("/filtrar_continentes")
    public ResponseEntity<?> filtrarContinentes(
            @RequestParam(required = false) Integer planetaId,
            @RequestParam(required = false) String hemisferio,
            @RequestParam(required = false) String clima,
            @RequestParam(required = false) Boolean habitable) {
        try {
            return ResponseEntity.ok().body(servicio.filtrarContinentes(planetaId, hemisferio, clima, habitable));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
    
