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
}


    
