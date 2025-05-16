package com.daw.controllers;

import java.sql.Date;
import java.sql.SQLException;

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
    
    /* ----- Endpoints para gestión de planetas ----- */
    
    /**
     * Busca planetas por nombre (o todos si no se proporciona nombre)
     */
    @GetMapping("/consulta_planetas")
    public ResponseEntity<?> buscarPlanetas(@RequestParam(required = false, defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok().body(servicio.buscarPlanetas(nombre));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Añade un nuevo planeta a la base de datos
     */
    @GetMapping("/aniadir_planeta")
    public ResponseEntity<?> aniadirPlaneta(
            @RequestParam String nombre, 
            @RequestParam String ubicacion, 
            @RequestParam Boolean habitable, 
            @RequestParam Float nivelAgua, 
            @RequestParam Date fechaCreacion, 
            @RequestParam Float tamanio, 
            @RequestParam Float densidad, 
            @RequestParam String descripcion) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirPlaneta(nombre, ubicacion, habitable, nivelAgua, fechaCreacion, tamanio, densidad, descripcion));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Elimina un planeta por su ID
     */
    @GetMapping("/eliminar_planeta")
    public ResponseEntity<?> eliminarPlaneta(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarPlaneta(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Actualiza los datos de un planeta existente
     */
    @GetMapping("/actualizar_planeta")
    public ResponseEntity<?> actualizarPlaneta(
            @RequestParam Integer id, 
            @RequestParam String nombre, 
            @RequestParam String ubicacion, 
            @RequestParam Boolean habitable, 
            @RequestParam Float nivelAgua, 
            @RequestParam Date fechaCreacion, 
            @RequestParam Float tamanio, 
            @RequestParam Float densidad, 
            @RequestParam String descripcion) {
        try {
            return ResponseEntity.ok().body(servicio.actualizarPlaneta(id, nombre, ubicacion, habitable, nivelAgua, fechaCreacion, tamanio, densidad, descripcion));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Lista todos los planetas
     */
    @GetMapping("/listar_planetas")
    public ResponseEntity<?> listarPlanetas() {
        try {
            return ResponseEntity.ok().body(servicio.listarPlanetas());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /* ----- Endpoints para gestión de continentes ----- */
    
    /**
     * Busca continentes por nombre (o todos si no se proporciona nombre)
     */
    @GetMapping("/consulta_continentes")
    public ResponseEntity<?> buscarContinentes(@RequestParam(required = false, defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok().body(servicio.buscarContinentes(nombre));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Añade un nuevo continente a la base de datos
     */
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
    
    /**
     * Elimina un continente por su ID
     */
    @GetMapping("/eliminar_continente")
    public ResponseEntity<?> eliminarContinente(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarContinente(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Actualiza los datos de un continente existente
     */
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
    
    /**
     * Lista todos los continentes
     */
    @GetMapping("/listar_continentes")
    public ResponseEntity<?> listarContinentes() {
        try {
            return ResponseEntity.ok().body(servicio.listarContinentes());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Obtiene un continente específico por su ID
     */
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
    
    /**
     * Filtra continentes según varios criterios opcionales
     */
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

    /* ----- Endpoints para gestión de biomas ----- */

    /**
     * Añade un nuevo bioma a la base de datos
     */
    @GetMapping("/aniadir_bioma")
    public ResponseEntity<?> aniadirBioma(
            @RequestParam String nombre, 
            @RequestParam Integer continenteId, 
            @RequestParam String clima, 
            @RequestParam(required = false) Float porcentajeHumedad, 
            @RequestParam(required = false) String precipitaciones, 
            @RequestParam(required = false) Float temperaturaMedia) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirBioma(nombre, continenteId, clima, porcentajeHumedad, precipitaciones, temperaturaMedia));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Elimina un bioma por su ID
     */
    @GetMapping("/eliminar_bioma")
    public ResponseEntity<?> eliminarBioma(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarBioma(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un bioma existente
     */
    @GetMapping("/actualizar_bioma")
    public ResponseEntity<?> actualizarBioma(
            @RequestParam Integer id,
            @RequestParam String nombre, 
            @RequestParam Integer continenteId, 
            @RequestParam String clima, 
            @RequestParam(required = false) Float porcentajeHumedad, 
            @RequestParam(required = false) String precipitaciones, 
            @RequestParam(required = false) Float temperaturaMedia) {
        try {
            return ResponseEntity.ok().body(servicio.actualizarBioma(id, nombre, continenteId, clima, 
                                         porcentajeHumedad, precipitaciones, temperaturaMedia));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Lista todos los biomas
     */
    @GetMapping("/listar_biomas")
    public ResponseEntity<?> listarBiomas() {
        try {
            return ResponseEntity.ok().body(servicio.listarBiomas());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Obtiene un bioma específico por su ID
     */
    @GetMapping("/obtener_bioma")
    public ResponseEntity<?> obtenerBiomaPorId(@RequestParam Integer id) {
        try {
            Bioma bioma = servicio.obtenerBiomaPorId(id);
            if (bioma != null) {
                return ResponseEntity.ok().body(bioma);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bioma no encontrado");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Filtra biomas según varios criterios opcionales
     */
    @GetMapping("/filtrar_biomas")
    public ResponseEntity<?> filtrarBiomas(
            @RequestParam(required = false) Integer continenteId,
            @RequestParam(required = false) String clima,
            @RequestParam(required = false) Float humedadMinima,
            @RequestParam(required = false) Float tempMin,
            @RequestParam(required = false) Float tempMax) {
        try {
            return ResponseEntity.ok().body(servicio.filtrarBiomas(continenteId, clima, humedadMinima, tempMin, tempMax));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

     /**
     * Busca razas por nombre (o todas si no se proporciona nombre)
     */
    @GetMapping("/consulta_razas")
    public ResponseEntity<?> buscarRazas(@RequestParam(required = false, defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok().body(servicio.buscarRazas(nombre));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Añade una nueva raza a la base de datos
     */
    @GetMapping("/aniadir_raza")
    public ResponseEntity<?> aniadirRaza(
            @RequestParam String nombre, 
            @RequestParam(required = false) String descripcionFisica, 
            @RequestParam(required = false) Date fechaConcepcion, 
            @RequestParam(required = false) Float alturaPromedia, 
            @RequestParam(required = false) Float anchoPromedio,
            @RequestParam(defaultValue = "10") Integer atk,
            @RequestParam(defaultValue = "10") Integer def,
            @RequestParam(defaultValue = "10") Integer hp,
            @RequestParam(defaultValue = "10") Integer spe,
            @RequestParam(defaultValue = "10") Integer mat,
            @RequestParam(defaultValue = "10") Integer mdf) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirRaza(nombre, descripcionFisica, fechaConcepcion, alturaPromedia, anchoPromedio, atk, def, hp, spe, mat, mdf));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Elimina una raza por su ID
     */
    @GetMapping("/eliminar_raza")
    public ResponseEntity<?> eliminarRaza(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarRaza(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Actualiza los datos de una raza existente
     */
    @GetMapping("/actualizar_raza")
    public ResponseEntity<?> actualizarRaza(
            @RequestParam Integer id, 
            @RequestParam String nombre, 
            @RequestParam(required = false) String descripcionFisica, 
            @RequestParam(required = false) Date fechaConcepcion, 
            @RequestParam(required = false) Float alturaPromedia, 
            @RequestParam(required = false) Float anchoPromedio,
            @RequestParam(defaultValue = "10") Integer atk,
            @RequestParam(defaultValue = "10") Integer def,
            @RequestParam(defaultValue = "10") Integer hp,
            @RequestParam(defaultValue = "10") Integer spe,
            @RequestParam(defaultValue = "10") Integer mat,
            @RequestParam(defaultValue = "10") Integer mdf) {
        try {
            return ResponseEntity.ok().body(servicio.actualizarRaza(id, nombre, descripcionFisica, fechaConcepcion, alturaPromedia, anchoPromedio, atk, def, hp, spe, mat, mdf));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Lista todas las razas
     */
    @GetMapping("/listar_razas")
    public ResponseEntity<?> listarRazas() {
        try {
            return ResponseEntity.ok().body(servicio.listarRazas());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Obtiene una raza específica por su ID
     */
    @GetMapping("/obtener_raza")
    public ResponseEntity<?> obtenerRazaPorId(@RequestParam Integer id) {
        try {
            Raza raza = servicio.obtenerRazaPorId(id);
            if (raza != null) {
                return ResponseEntity.ok().body(raza);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Raza no encontrada");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Filtra razas según varios criterios opcionales
     */
    @GetMapping("/filtrar_razas")
    public ResponseEntity<?> filtrarRazas(
            @RequestParam(required = false) Float alturaMinima,
            @RequestParam(required = false) Float anchoMinimo) {
        try {
            return ResponseEntity.ok().body(servicio.filtrarRazas(alturaMinima, anchoMinimo));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Busca imperios por nombre (o todos si no se proporciona nombre)
     */
    @GetMapping("/consulta_imperios")
    public ResponseEntity<?> buscarImperios(@RequestParam(required = false, defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok().body(servicio.buscarImperios(nombre));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Añade un nuevo imperio a la base de datos
     */
    @GetMapping("/aniadir_imperio")
    public ResponseEntity<?> aniadirImperio(
            @RequestParam String nombre, 
            @RequestParam(required = false) Integer poblacion, 
            @RequestParam(required = false) String descripcion, 
            @RequestParam(required = false) Date fechaCreacion, 
            @RequestParam(required = false) String lider, 
            @RequestParam(required = false) String ideologia, 
            @RequestParam(required = false) Float gdp, 
            @RequestParam(required = false) Float tamanio, 
            @RequestParam(required = false, defaultValue = "false") Boolean enGuerra) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirImperio(nombre, poblacion, descripcion, fechaCreacion, lider, ideologia, gdp, tamanio, enGuerra));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Elimina un imperio por su ID
     */
    @GetMapping("/eliminar_imperio")
    public ResponseEntity<?> eliminarImperio(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarImperio(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Actualiza los datos de un imperio existente
     */
    @GetMapping("/actualizar_imperio")
    public ResponseEntity<?> actualizarImperio(
            @RequestParam Integer id, 
            @RequestParam String nombre, 
            @RequestParam(required = false) Integer poblacion, 
            @RequestParam(required = false) String descripcion, 
            @RequestParam(required = false) Date fechaCreacion, 
            @RequestParam(required = false) String lider, 
            @RequestParam(required = false) String ideologia, 
            @RequestParam(required = false) Float gdp, 
            @RequestParam(required = false) Float tamanio, 
            @RequestParam(required = false, defaultValue = "false") Boolean enGuerra) {
        try {
            return ResponseEntity.ok().body(servicio.actualizarImperio(id, nombre, poblacion, descripcion, fechaCreacion, lider, ideologia, gdp, tamanio, enGuerra));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Lista todos los imperios
     */
    @GetMapping("/listar_imperios")
    public ResponseEntity<?> listarImperios() {
        try {
            return ResponseEntity.ok().body(servicio.listarImperios());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Obtiene un imperio específico por su ID
     */
    @GetMapping("/obtener_imperio")
    public ResponseEntity<?> obtenerImperioPorId(@RequestParam Integer id) {
        try {
            Imperio imperio = servicio.obtenerImperioPorId(id);
            if (imperio != null) {
                return ResponseEntity.ok().body(imperio);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Imperio no encontrado");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Filtra imperios según varios criterios opcionales
     */
    @GetMapping("/filtrar_imperios")
    public ResponseEntity<?> filtrarImperios(
            @RequestParam(required = false) Integer poblacionMinima,
            @RequestParam(required = false) String ideologia,
            @RequestParam(required = false) Boolean enGuerra) {
        try {
            return ResponseEntity.ok().body(servicio.filtrarImperios(poblacionMinima, ideologia, enGuerra));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    /**
     * Busca guerras por nombre (o todas si no se proporciona nombre)
     */
    @GetMapping("/consulta_guerras")
    public ResponseEntity<?> buscarGuerras(@RequestParam(required = false, defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok().body(servicio.buscarGuerras(nombre));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Añade una nueva guerra a la base de datos
     */
    @GetMapping("/aniadir_guerra")
    public ResponseEntity<?> aniadirGuerra(@RequestParam Guerra guerra) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirGuerra(guerra));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Elimina una guerra por su ID
     */
    @GetMapping("/eliminar_guerra")
    public ResponseEntity<?> eliminarGuerra(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarGuerra(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Actualiza los datos de una guerra existente
     */
    @GetMapping("/actualizar_guerra")
    public ResponseEntity<?> actualizarGuerra(@RequestParam Guerra guerra) {
        try {
            return ResponseEntity.ok().body(servicio.actualizarGuerra(guerra));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Lista todas las guerras
     */
    @GetMapping("/listar_guerras")
    public ResponseEntity<?> listarGuerras() {
        try {
            return ResponseEntity.ok().body(servicio.listarGuerras());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Obtiene una guerra específica por su ID
     */
    @GetMapping("/obtener_guerra")
    public ResponseEntity<?> obtenerGuerraPorId(@RequestParam Integer id) {
        try {
            Guerra guerra = servicio.obtenerGuerraPorId(id);
            if (guerra != null) {
                return ResponseEntity.ok().body(guerra);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Guerra no encontrada");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Filtra guerras según varios criterios opcionales
     */
    @GetMapping("/filtrar_guerras")
    public ResponseEntity<?> filtrarGuerras(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer imperioId) {
        try {
            return ResponseEntity.ok().body(servicio.filtrarGuerras(estado, imperioId));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
/**
 * Añade una nueva persona a la base de datos
 */
@GetMapping("/aniadir_persona")
public ResponseEntity<?> aniadirPersona(
        @RequestParam String nombre,
        @RequestParam(required = false) String apellido,
        @RequestParam(required = false) Float ancho,
        @RequestParam(required = false) Float alto,
        @RequestParam(required = false) String descripcionFisica,
        @RequestParam(required = false) Float porcentajeGrasaCorporal,
        @RequestParam(required = false) String personalidad,
        @RequestParam(required = false, defaultValue = "0") Integer oro,
        @RequestParam(required = false) Date fechaNacimiento,
        @RequestParam(required = false) String profesion,
        @RequestParam(required = false) String direccion,
        @RequestParam Integer razaId,
        @RequestParam(required = false) Integer imperioId,
        @RequestParam(required = false, defaultValue = "Personal") String estadisticasTipo,
        @RequestParam(required = false, defaultValue = "10") Integer atk,
        @RequestParam(required = false, defaultValue = "10") Integer def,
        @RequestParam(required = false, defaultValue = "10") Integer hp,
        @RequestParam(required = false, defaultValue = "10") Integer spe,
        @RequestParam(required = false, defaultValue = "10") Integer mat,
        @RequestParam(required = false, defaultValue = "10") Integer mdf,
        @RequestParam(required = false, defaultValue = "0") Integer xp,
        @RequestParam(required = false, defaultValue = "1") Integer lvl) {
    try {
        // Create a Persona object from the parameters
        Persona persona = new Persona();
        persona.setNombre(nombre);
        persona.setApellido(apellido);
        persona.setAncho(ancho);
        persona.setAlto(alto);
        persona.setDescripcionFisica(descripcionFisica);
        persona.setPorcentajeGrasaCorporal(porcentajeGrasaCorporal);
        persona.setPersonalidad(personalidad);
        persona.setOro(oro);
        persona.setFechaNacimiento(fechaNacimiento);
        persona.setProfesion(profesion);
        persona.setDireccion(direccion);
        persona.setRazaId(razaId);
        persona.setImperioId(imperioId);
        
        // Create and set estadisticas
        Estadisticas stats = new Estadisticas();
        stats.setTipo(estadisticasTipo);
        stats.setAtk(atk);
        stats.setDef(def);
        stats.setHp(hp);
        stats.setSpe(spe);
        stats.setMat(mat);
        stats.setMdf(mdf);
        stats.setXp(xp);
        stats.setLvl(lvl);
        persona.setEstadisticas(stats);
        
        return ResponseEntity.ok().body(servicio.aniadirPersona(persona));
    } catch (SQLException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}

/**
 * Actualiza una persona existente
 */
@GetMapping("/actualizar_persona")
public ResponseEntity<?> actualizarPersona(
        @RequestParam Integer id,
        @RequestParam String nombre,
        @RequestParam(required = false) String apellido,
        @RequestParam(required = false) Float ancho,
        @RequestParam(required = false) Float alto,
        @RequestParam(required = false) String descripcionFisica,
        @RequestParam(required = false) Float porcentajeGrasaCorporal,
        @RequestParam(required = false) String personalidad,
        @RequestParam(required = false, defaultValue = "0") Integer oro,
        @RequestParam(required = false) Date fechaNacimiento,
        @RequestParam(required = false) String profesion,
        @RequestParam(required = false) String direccion,
        @RequestParam Integer razaId,
        @RequestParam(required = false) Integer imperioId,
        @RequestParam(required = false, defaultValue = "Personal") String estadisticasTipo,
        @RequestParam(required = false, defaultValue = "10") Integer atk,
        @RequestParam(required = false, defaultValue = "10") Integer def,
        @RequestParam(required = false, defaultValue = "10") Integer hp,
        @RequestParam(required = false, defaultValue = "10") Integer spe,
        @RequestParam(required = false, defaultValue = "10") Integer mat,
        @RequestParam(required = false, defaultValue = "10") Integer mdf,
        @RequestParam(required = false, defaultValue = "0") Integer xp,
        @RequestParam(required = false, defaultValue = "1") Integer lvl) {
    try {

        Persona persona = new Persona();
        persona.setId(id);
        persona.setNombre(nombre);
        persona.setApellido(apellido);
        persona.setAncho(ancho);
        persona.setAlto(alto);
        persona.setDescripcionFisica(descripcionFisica);
        persona.setPorcentajeGrasaCorporal(porcentajeGrasaCorporal);
        persona.setPersonalidad(personalidad);
        persona.setOro(oro);
        persona.setFechaNacimiento(fechaNacimiento);
        persona.setProfesion(profesion);
        persona.setDireccion(direccion);
        persona.setRazaId(razaId);
        persona.setImperioId(imperioId);
        

        Estadisticas stats = new Estadisticas();
        stats.setTipo(estadisticasTipo);
        stats.setAtk(atk);
        stats.setDef(def);
        stats.setHp(hp);
        stats.setSpe(spe);
        stats.setMat(mat);
        stats.setMdf(mdf);
        stats.setXp(xp);
        stats.setLvl(lvl);
        persona.setEstadisticas(stats);
        
        return ResponseEntity.ok().body(servicio.actualizarPersona(persona));
    } catch (SQLException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
    /**
     * Elimina una persona por su ID
     */
    @GetMapping("/eliminar_persona")
    public ResponseEntity<?> eliminarPersona(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarPersona(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Lista todas las personas
     */
    @GetMapping("/listar_personas")
    public ResponseEntity<?> listarPersonas() {
        try {
            return ResponseEntity.ok().body(servicio.listarPersonas());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Obtiene una persona específica por su ID
     */
    @GetMapping("/obtener_persona")
    public ResponseEntity<?> obtenerPersonaPorId(@RequestParam Integer id) {
        try {
            Persona persona = servicio.obtenerPersonaPorId(id);
            if (persona != null) {
                return ResponseEntity.ok().body(persona);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Persona no encontrada");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Filtra personas según varios criterios opcionales
     */
    @GetMapping("/filtrar_personas")
    public ResponseEntity<?> filtrarPersonas(
            @RequestParam(required = false) Integer razaId,
            @RequestParam(required = false) Integer imperioId,
            @RequestParam(required = false) String profesion,
            @RequestParam(required = false) Integer nivelMin) {
        try {
            return ResponseEntity.ok().body(servicio.filtrarPersonas(razaId, imperioId, profesion, nivelMin));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Lista todas las armas
     */
    @GetMapping("/listar_armas")
    public ResponseEntity<?> listarArmas() {
        try {
            return ResponseEntity.ok().body(servicio.listarArmas());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Lista todas las armaduras
     */
    @GetMapping("/listar_armaduras")
    public ResponseEntity<?> listarArmaduras() {
        try {
            return ResponseEntity.ok().body(servicio.listarArmaduras());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Lista todas las herramientas
     */
    @GetMapping("/listar_herramientas")
    public ResponseEntity<?> listarHerramientas() {
        try {
            return ResponseEntity.ok().body(servicio.listarHerramientas());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    /**
     * Lista todas las arcanas
     */
    @GetMapping("/listar_arcanas")
    public ResponseEntity<?> listarArcanas() {
        try {
            return ResponseEntity.ok().body(servicio.listarArcanas());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}