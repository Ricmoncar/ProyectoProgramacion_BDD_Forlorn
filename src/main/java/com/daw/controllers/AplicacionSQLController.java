package com.daw.controllers;

import java.sql.Date;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import com.daw.services.Servicio;

@RestController
@CrossOrigin(origins = "*")
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
     * Registra una nueva guerra en el sistema. Recibe el objeto Guerra
     * completo, incluyendo los imperios participantes, en el cuerpo de la
     * solicitud.
     */
    @PostMapping("/aniadir_guerra")
    public ResponseEntity<?> aniadirGuerra(@RequestBody Guerra guerra) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirGuerra(guerra));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de base de datos al añadir guerra: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Argumento inválido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en aniadirGuerra controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en los datos de la guerra: " + e.getMessage());
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

    //Actualiza los datos de una guerra existente.
    //Recibe el objeto Guerra completo, incluyendo los imperios participantes, en el cuerpo de la solicitud.
    //
    @PostMapping("/actualizar_guerra")
    public ResponseEntity<?> actualizarGuerra(@RequestBody Guerra guerra) {
        try {
            if (guerra.getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID de la guerra es requerido para la actualización.");
            }
            return ResponseEntity.ok().body(servicio.actualizarGuerra(guerra));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de base de datos al actualizar guerra: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Argumento inválido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en actualizarGuerra controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en los datos de la guerra: " + e.getMessage());
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
     * Añade una nueva persona al sistema
     */
    @PostMapping("/aniadir_persona") // Cambiado de @GetMapping a @PostMapping
    public ResponseEntity<?> aniadirPersona(@RequestBody Persona persona) {
        try {
            // Spring automáticamente intentará mapear el JSON del cuerpo de la solicitud
            // al objeto 'persona'. Esto incluye el intento de mapear
            // campos anidados como 'estadisticas' y listas como 'armas'.

            // El objeto 'persona' que llega aquí ya debería tener sus campos rellenaos
            return ResponseEntity.ok().body(servicio.aniadirPersona(persona));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            // si el JSON no se puede convertir a objeto Persona.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en los datos de la persona: " + e.getMessage());
        }
    }

    /**
     * Actualiza una persona existente. Recibe los datos de la persona,
     * incluidas sus estadísticas y equipamiento, en el cuerpo de la solicitud
     * como un objeto JSON.
     */
    @PostMapping("/actualizar_persona")
    public ResponseEntity<?> actualizarPersona(@RequestBody Persona persona) {
        try {
            // El ID de la persona debe venir dentro del objeto 'persona' enviado en el JSON.
            if (persona.getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID de la persona es requerido para la actualización.");
            }

            // Spring mapea el JSON al objeto 'persona'.
            // El servicio 'actualizarPersona' ya espera un objeto Persona.
            return ResponseEntity.ok().body(servicio.actualizarPersona(persona));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en actualizarPersona controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en los datos de la persona: " + e.getMessage());
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

// Agregar estos métodos al final de AplicacionSQLController.java

    /* ----- Endpoints para gestión de Armas ----- */
    /**
     * Busca armas por nombre (o todas si no se proporciona nombre)
     */
    @GetMapping("/consulta_armas")
    public ResponseEntity<?> buscarArmas(@RequestParam(required = false, defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok().body(servicio.buscarArmas(nombre));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Añade una nueva arma a la base de datos
     */
    @GetMapping("/aniadir_arma")
    public ResponseEntity<?> aniadirArma(
            @RequestParam String nombre,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Float peso,
            @RequestParam(required = false) Float pvp,
            @RequestParam Integer imperioOrigen,
            @RequestParam(required = false) Date fechaCreacion,
            @RequestParam(required = false, defaultValue = "0") Integer bufAtk,
            @RequestParam(required = false, defaultValue = "0") Integer bufDef,
            @RequestParam(required = false, defaultValue = "0") Integer bufHp,
            @RequestParam(required = false, defaultValue = "0") Integer bufSpe,
            @RequestParam(required = false, defaultValue = "0") Integer bufMat,
            @RequestParam(required = false, defaultValue = "0") Integer bufMdf) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirArma(nombre, material, descripcion, peso, pvp,
                    imperioOrigen, fechaCreacion, bufAtk, bufDef,
                    bufHp, bufSpe, bufMat, bufMdf));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Obtiene un arma específica por su ID
     */
    @GetMapping("/obtener_arma")
    public ResponseEntity<?> obtenerArmaPorId(@RequestParam Integer id) {
        try {
            Arma arma = servicio.obtenerArmaPorId(id);
            if (arma != null) {
                return ResponseEntity.ok().body(arma);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Arma no encontrada");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Actualiza un arma existente
     */
    @GetMapping("/actualizar_arma")
    public ResponseEntity<?> actualizarArma(
            @RequestParam Integer id,
            @RequestParam String nombre,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Float peso,
            @RequestParam(required = false) Float pvp,
            @RequestParam Integer imperioOrigen,
            @RequestParam(required = false) Date fechaCreacion,
            @RequestParam(required = false, defaultValue = "0") Integer bufAtk,
            @RequestParam(required = false, defaultValue = "0") Integer bufDef,
            @RequestParam(required = false, defaultValue = "0") Integer bufHp,
            @RequestParam(required = false, defaultValue = "0") Integer bufSpe,
            @RequestParam(required = false, defaultValue = "0") Integer bufMat,
            @RequestParam(required = false, defaultValue = "0") Integer bufMdf) {
        try {
            return ResponseEntity.ok().body(servicio.actualizarArma(id, nombre, material, descripcion, peso, pvp,
                    imperioOrigen, fechaCreacion, bufAtk, bufDef,
                    bufHp, bufSpe, bufMat, bufMdf));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Elimina un arma por su ID
     */
    @GetMapping("/eliminar_arma")
    public ResponseEntity<?> eliminarArma(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarArma(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /* ----- Endpoints para gestión de Armaduras ----- */
    /**
     * Busca armaduras por nombre (o todas si no se proporciona nombre)
     */
    @GetMapping("/consulta_armaduras")
    public ResponseEntity<?> buscarArmaduras(@RequestParam(required = false, defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok().body(servicio.buscarArmaduras(nombre));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Añade una nueva armadura a la base de datos
     */
    @GetMapping("/aniadir_armadura")
    public ResponseEntity<?> aniadirArmadura(
            @RequestParam String nombre,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Float peso,
            @RequestParam(required = false) Float pvp,
            @RequestParam Integer imperioOrigen,
            @RequestParam(required = false) Date fechaCreacion,
            @RequestParam(required = false, defaultValue = "0") Integer bufAtk,
            @RequestParam(required = false, defaultValue = "0") Integer bufDef,
            @RequestParam(required = false, defaultValue = "0") Integer bufHp,
            @RequestParam(required = false, defaultValue = "0") Integer bufSpe,
            @RequestParam(required = false, defaultValue = "0") Integer bufMat,
            @RequestParam(required = false, defaultValue = "0") Integer bufMdf) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirArmadura(nombre, material, descripcion, peso, pvp,
                    imperioOrigen, fechaCreacion, bufAtk, bufDef,
                    bufHp, bufSpe, bufMat, bufMdf));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Obtiene una armadura específica por su ID
     */
    @GetMapping("/obtener_armadura")
    public ResponseEntity<?> obtenerArmaduraPorId(@RequestParam Integer id) {
        try {
            Armadura armadura = servicio.obtenerArmaduraPorId(id);
            if (armadura != null) {
                return ResponseEntity.ok().body(armadura);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Armadura no encontrada");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Actualiza una armadura existente
     */
    @GetMapping("/actualizar_armadura")
    public ResponseEntity<?> actualizarArmadura(
            @RequestParam Integer id,
            @RequestParam String nombre,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Float peso,
            @RequestParam(required = false) Float pvp,
            @RequestParam Integer imperioOrigen,
            @RequestParam(required = false) Date fechaCreacion,
            @RequestParam(required = false, defaultValue = "0") Integer bufAtk,
            @RequestParam(required = false, defaultValue = "0") Integer bufDef,
            @RequestParam(required = false, defaultValue = "0") Integer bufHp,
            @RequestParam(required = false, defaultValue = "0") Integer bufSpe,
            @RequestParam(required = false, defaultValue = "0") Integer bufMat,
            @RequestParam(required = false, defaultValue = "0") Integer bufMdf) {
        try {
            return ResponseEntity.ok().body(servicio.actualizarArmadura(id, nombre, material, descripcion, peso, pvp,
                    imperioOrigen, fechaCreacion, bufAtk, bufDef,
                    bufHp, bufSpe, bufMat, bufMdf));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Elimina una armadura por su ID
     */
    @GetMapping("/eliminar_armadura")
    public ResponseEntity<?> eliminarArmadura(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarArmadura(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /* ----- Endpoints para gestión de Herramientas ----- */
    /**
     * Busca herramientas por nombre (o todas si no se proporciona nombre)
     */
    @GetMapping("/consulta_herramientas")
    public ResponseEntity<?> buscarHerramientas(@RequestParam(required = false, defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok().body(servicio.buscarHerramientas(nombre));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Añade una nueva herramienta a la base de datos
     */
    @GetMapping("/aniadir_herramienta")
    public ResponseEntity<?> aniadirHerramienta(
            @RequestParam String nombre,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String uso,
            @RequestParam(required = false) Float peso,
            @RequestParam(required = false) Float pvp,
            @RequestParam Integer imperioOrigen,
            @RequestParam(required = false) Date fechaCreacion,
            @RequestParam(required = false, defaultValue = "0") Integer bufAtk,
            @RequestParam(required = false, defaultValue = "0") Integer bufDef,
            @RequestParam(required = false, defaultValue = "0") Integer bufHp,
            @RequestParam(required = false, defaultValue = "0") Integer bufSpe,
            @RequestParam(required = false, defaultValue = "0") Integer bufMat,
            @RequestParam(required = false, defaultValue = "0") Integer bufMdf) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirHerramienta(nombre, material, descripcion, uso, peso, pvp,
                    imperioOrigen, fechaCreacion, bufAtk, bufDef,
                    bufHp, bufSpe, bufMat, bufMdf));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Obtiene una herramienta específica por su ID
     */
    @GetMapping("/obtener_herramienta")
    public ResponseEntity<?> obtenerHerramientaPorId(@RequestParam Integer id) {
        try {
            Herramienta herramienta = servicio.obtenerHerramientaPorId(id);
            if (herramienta != null) {
                return ResponseEntity.ok().body(herramienta);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Herramienta no encontrada");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Actualiza una herramienta existente
     */
    @GetMapping("/actualizar_herramienta")
    public ResponseEntity<?> actualizarHerramienta(
            @RequestParam Integer id,
            @RequestParam String nombre,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String uso,
            @RequestParam(required = false) Float peso,
            @RequestParam(required = false) Float pvp,
            @RequestParam Integer imperioOrigen,
            @RequestParam(required = false) Date fechaCreacion,
            @RequestParam(required = false, defaultValue = "0") Integer bufAtk,
            @RequestParam(required = false, defaultValue = "0") Integer bufDef,
            @RequestParam(required = false, defaultValue = "0") Integer bufHp,
            @RequestParam(required = false, defaultValue = "0") Integer bufSpe,
            @RequestParam(required = false, defaultValue = "0") Integer bufMat,
            @RequestParam(required = false, defaultValue = "0") Integer bufMdf) {
        try {
            return ResponseEntity.ok().body(servicio.actualizarHerramienta(id, nombre, material, descripcion, uso, peso, pvp,
                    imperioOrigen, fechaCreacion, bufAtk, bufDef,
                    bufHp, bufSpe, bufMat, bufMdf));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Elimina una herramienta por su ID
     */
    @GetMapping("/eliminar_herramienta")
    public ResponseEntity<?> eliminarHerramienta(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarHerramienta(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /* ----- Endpoints para gestión de Arcanas ----- */
    /**
     * Busca arcanas por tipo (o todas si no se proporciona tipo)
     */
    @GetMapping("/consulta_arcanas")
    public ResponseEntity<?> buscarArcanas(@RequestParam(required = false, defaultValue = "") String tipo) {
        try {
            return ResponseEntity.ok().body(servicio.buscarArcanas(tipo));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Añade una nueva arcana a la base de datos
     */
    @GetMapping("/aniadir_arcana")
    public ResponseEntity<?> aniadirArcana(
            @RequestParam String tipo,
            @RequestParam(required = false, defaultValue = "Novato") String maestria,
            @RequestParam(required = false, defaultValue = "Fácil") String dificultad,
            @RequestParam(required = false) Date fecha) {
        try {
            return ResponseEntity.ok().body(servicio.aniadirArcana(tipo, maestria, dificultad, fecha));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Obtiene una arcana específica por su ID
     */
    @GetMapping("/obtener_arcana")
    public ResponseEntity<?> obtenerArcanaPorId(@RequestParam Integer id) {
        try {
            Arcana arcana = servicio.obtenerArcanaPorId(id);
            if (arcana != null) {
                return ResponseEntity.ok().body(arcana);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Arcana no encontrada");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Actualiza una arcana existente
     */
    @GetMapping("/actualizar_arcana")
    public ResponseEntity<?> actualizarArcana(
            @RequestParam Integer id,
            @RequestParam String tipo,
            @RequestParam(required = false, defaultValue = "Novato") String maestria,
            @RequestParam(required = false, defaultValue = "Fácil") String dificultad,
            @RequestParam(required = false) Date fecha) {
        try {
            return ResponseEntity.ok().body(servicio.actualizarArcana(id, tipo, maestria, dificultad, fecha));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Elimina una arcana por su ID
     */
    @GetMapping("/eliminar_arcana")
    public ResponseEntity<?> eliminarArcana(@RequestParam Integer id) {
        try {
            return ResponseEntity.ok().body(servicio.eliminarArcana(id));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /* ----- Endpoints para equipamiento de personas ----- */
    /**
     * Lista las armas de una persona específica
     */
    @GetMapping("/listar_armas_persona")
    public ResponseEntity<?> listarArmasPersona(@RequestParam Integer personaId) {
        try {
            return ResponseEntity.ok().body(servicio.listarArmasPersona(personaId));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Lista las armaduras de una persona específica
     */
    @GetMapping("/listar_armaduras_persona")
    public ResponseEntity<?> listarArmadurasPersona(@RequestParam Integer personaId) {
        try {
            return ResponseEntity.ok().body(servicio.listarArmadurasPersona(personaId));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Lista las herramientas de una persona específica
     */
    @GetMapping("/listar_herramientas_persona")
    public ResponseEntity<?> listarHerramientasPersona(@RequestParam Integer personaId) {
        try {
            return ResponseEntity.ok().body(servicio.listarHerramientasPersona(personaId));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Lista las arcanas de una persona específica
     */
    @GetMapping("/listar_arcanas_persona")
    public ResponseEntity<?> listarArcanasPersona(@RequestParam Integer personaId) {
        try {
            return ResponseEntity.ok().body(servicio.listarArcanasPersona(personaId));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * TABLA MAESTRA UNIVERSAL
     */
    @GetMapping("/universo/estadisticas_globales")
    public ResponseEntity<?> getEstadisticasGlobales() {
        try {
            return ResponseEntity.ok().body(servicio.obtenerEstadisticasGlobales());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener estadísticas globales: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: " + e.getMessage());
        }
    }

    @GetMapping("/universo/planetas_recientes")
    public ResponseEntity<?> getPlanetasRecientes(@RequestParam(defaultValue = "5") int limite) {
        try {
            return ResponseEntity.ok().body(servicio.getPlanetasRecientes(limite));
        } catch (Exception e) {
            System.err.println("Error en getPlanetasRecientes controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener planetas recientes: " + e.getMessage());
        }
    }

    @GetMapping("/universo/imperios_recientes")
    public ResponseEntity<?> getImperiosRecientes(@RequestParam(defaultValue = "5") int limite) {
        try {
            return ResponseEntity.ok().body(servicio.getImperiosRecientes(limite));
        } catch (SQLException e) {
            System.err.println("Error SQL en getImperiosRecientes controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de base de datos al obtener imperios recientes: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en getImperiosRecientes controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al obtener imperios recientes: " + e.getMessage());
        }
    }

    @GetMapping("/universo/personas_recientes")
    public ResponseEntity<?> getPersonasRecientes(@RequestParam(defaultValue = "5") int limite) {
        try {
            return ResponseEntity.ok().body(servicio.getPersonasRecientes(limite));
        } catch (SQLException e) {
            System.err.println("Error SQL en getPersonasRecientes controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de base de datos al obtener personas recientes: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en getPersonasRecientes controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al obtener personas recientes: " + e.getMessage());
        }
    }

    @GetMapping("/universo/guerras_recientes")
    public ResponseEntity<?> getGuerrasRecientes(@RequestParam(defaultValue = "5") int limite) {
        try {
            return ResponseEntity.ok().body(servicio.getGuerrasRecientes(limite));
        } catch (SQLException e) {
            System.err.println("Error SQL en getGuerrasRecientes controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de base de datos al obtener guerras recientes: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en getGuerrasRecientes controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al obtener guerras recientes: " + e.getMessage());
        }
    }

     /**
     * Obtiene datos consolidados de varias entidades para la tabla maestra del universo.
     *
     * @param limitePorEntidad El número máximo de ítems a cargar por cada tipo de entidad (opcional, por defecto 5 o 10).
     * @return Una lista de objetos FilaTablaMaestra representando los datos para la tabla.
     */
    @GetMapping("/universo/tabla_maestra")
    public ResponseEntity<?> getTablaMaestra(@RequestParam(defaultValue = "5") int limitePorEntidad) {
        try {
            // Llama al método del servicio
            // para incluir planetas, imperios, guerras, personas, continentes, biomas y equipamiento.
            return ResponseEntity.ok().body(servicio.getDatosTablaMaestra(limitePorEntidad));
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener datos para tabla maestra: " + e.getMessage());
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error de base de datos al obtener datos para la tabla maestra: " + e.getMessage());
        } catch (Exception e) {

            System.err.println("Error inesperado al obtener datos para tabla maestra: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error inesperado al procesar la solicitud: " + e.getMessage());
        }
    }
}
