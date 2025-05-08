package com.daw.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.daw.controllers.Bioma;
import com.daw.controllers.Continente;
import com.daw.controllers.Planeta;
import com.daw.controllers.Raza;
import com.daw.controllers.Imperio;
import com.daw.controllers.Guerra;
import com.daw.controllers.ParticipanteGuerra;
import com.daw.controllers.Estadisticas;


@Service
public class Servicio {

    /* Configuración de conexión a la base de datos */
    static final String url = "jdbc:mysql://localhost:3306/mundo_fantasia?user=usuario&password=usuario";

    /* Carga del driver JDBC */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("¡Driver MySQL JDBC no encontrado!", e);
        }
    }

    /* ----- Métodos para gestión de Planetas ----- */
    
    /**
     * Busca planetas que coincidan con el nombre proporcionado
     */
    public List<Planeta> buscarPlanetas(String nombre) throws SQLException {
        List<Planeta> lista = new ArrayList<>();
        
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement("SELECT * FROM planeta WHERE nombre LIKE ?")) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Planeta planeta = new Planeta();
                    planeta.setId(rs.getInt("id"));
                    planeta.setNombre(rs.getString("nombre"));
                    planeta.setUbicacion(rs.getString("ubicacion"));
                    planeta.setHabitable(rs.getBoolean("habitable"));
                    planeta.setNivelAgua(rs.getFloat("nivelAgua"));
                    planeta.setFechaCreacion(rs.getDate("FechaCreacion"));
                    planeta.setTamanio(rs.getFloat("Tamanio"));
                    planeta.setDensidad(rs.getFloat("densidad"));
                    planeta.setDescripcion(rs.getString("descripcion"));
                    lista.add(planeta);
                }
            }
        }
        return lista;
    }

    /**
     * Añade un nuevo planeta a la base de datos
     */
    public String aniadirPlaneta(String nombre, String ubicacion, Boolean habitable, Float nivelAgua, Date fechaCreacion, Float tamanio, Float densidad, String descripcion) throws SQLException {
        int rowsAffected = 0;
        
        try (Connection con = DriverManager.getConnection(url);
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO planeta (nombre, ubicacion, habitable, nivelAgua, FechaCreacion, Tamanio, densidad, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, nombre);
            ps.setString(2, ubicacion);
            ps.setBoolean(3, habitable);
            ps.setFloat(4, nivelAgua);
            ps.setDate(5, fechaCreacion);
            ps.setFloat(6, tamanio);
            ps.setFloat(7, densidad);
            ps.setString(8, descripcion);
            rowsAffected = ps.executeUpdate();
        }
        
        return rowsAffected + " filas afectadas";
    }

    /**
     * Elimina un planeta por su ID
     */
    public String eliminarPlaneta(Integer id) throws SQLException {
        int rowsAffected = 0;
        
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement("DELETE FROM planeta WHERE id = ?")) {
            ps.setInt(1, id);
            rowsAffected = ps.executeUpdate();
        }
        
        return rowsAffected + " filas afectadas";
    }

    /**
     * Actualiza los datos de un planeta existente
     */
    public String actualizarPlaneta(Integer id, String nombre, String ubicacion, Boolean habitable, Float nivelAgua, Date fechaCreacion, Float tamanio, Float densidad, String descripcion) throws SQLException {
        int rowsAffected = 0;
        
        try (Connection con = DriverManager.getConnection(url);
            PreparedStatement ps = con.prepareStatement(
                "UPDATE planeta SET nombre = ?, ubicacion = ?, habitable = ?, nivelAgua = ?, FechaCreacion = ?, Tamanio = ?, densidad = ?, descripcion = ? WHERE id = ?")) {
            ps.setString(1, nombre);
            ps.setString(2, ubicacion);
            ps.setBoolean(3, habitable);
            ps.setFloat(4, nivelAgua);
            ps.setDate(5, fechaCreacion);
            ps.setFloat(6, tamanio);
            ps.setFloat(7, densidad);
            ps.setString(8, descripcion);
            ps.setInt(9, id);
            rowsAffected = ps.executeUpdate();
        }
        
        return rowsAffected + " filas afectadas";
    }

    /**
     * Obtiene la lista de todos los planetas
     */
    public List<Planeta> listarPlanetas() throws SQLException {
        List<Planeta> lista = new ArrayList<>();
        
        try (Connection con = DriverManager.getConnection(url);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM planeta")) {
            while (rs.next()) {
                Planeta planeta = new Planeta();
                planeta.setId(rs.getInt("id"));
                planeta.setNombre(rs.getString("nombre"));
                planeta.setUbicacion(rs.getString("ubicacion"));
                planeta.setHabitable(rs.getBoolean("habitable"));
                planeta.setNivelAgua(rs.getFloat("nivelAgua"));
                planeta.setFechaCreacion(rs.getDate("FechaCreacion"));
                planeta.setTamanio(rs.getFloat("Tamanio"));
                planeta.setDensidad(rs.getFloat("densidad"));
                planeta.setDescripcion(rs.getString("descripcion"));
                lista.add(planeta);
            }
        }
        
        return lista;
    }

    /* ----- Métodos para gestión de Continentes ----- */

    /**
     * Busca continentes que coincidan con el nombre proporcionado
     */
    public List<Continente> buscarContinentes(String nombre) throws SQLException {
        List<Continente> lista = new ArrayList<>();
        
        String sql = "SELECT c.*, p.nombre as planetaNombre FROM continente c JOIN planeta p ON c.PlanetaID = p.id WHERE c.nombre LIKE ?";
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Continente continente = mapRowToContinente(rs);
                    lista.add(continente);
                }
            }
        }
        
        return lista;
    }

    /**
     * Añade un nuevo continente a la base de datos
     */
    public String aniadirContinente(String nombre, Integer planetaId, String hemisferio, String clima, Float tamanio, Boolean habitable, String descripcion) throws SQLException {
        int rowsAffected = 0;
        
        String sql = "INSERT INTO continente (nombre, PlanetaID, hemisferio, clima, Tamanio, habitable, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, planetaId);
            ps.setString(3, hemisferio);
            ps.setString(4, clima);
            
            if (tamanio != null) {
                ps.setFloat(5, tamanio);
            } else {
                ps.setNull(5, java.sql.Types.FLOAT);
            }
            
            ps.setBoolean(6, habitable);
            ps.setString(7, descripcion);
            rowsAffected = ps.executeUpdate();
        }
        
        return rowsAffected + " filas afectadas";
    }

    /**
     * Elimina un continente por su ID
     */
    public String eliminarContinente(Integer id) throws SQLException {
        int rowsAffected = 0;
        
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement("DELETE FROM continente WHERE id = ?")) {
            ps.setInt(1, id);
            rowsAffected = ps.executeUpdate();
        }
        
        return rowsAffected + " filas afectadas";
    }

    /**
     * Actualiza los datos de un continente existente
     */
    public String actualizarContinente(Integer id, String nombre, Integer planetaId, String hemisferio, String clima, Float tamanio, Boolean habitable, String descripcion) throws SQLException {
        int rowsAffected = 0;
        
        String sql = "UPDATE continente SET nombre = ?, PlanetaID = ?, hemisferio = ?, clima = ?, Tamanio = ?, habitable = ?, descripcion = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, planetaId);
            ps.setString(3, hemisferio);
            ps.setString(4, clima);
            
            if (tamanio != null) {
                ps.setFloat(5, tamanio);
            } else {
                ps.setNull(5, java.sql.Types.FLOAT);
            }
            
            ps.setBoolean(6, habitable);
            ps.setString(7, descripcion);
            ps.setInt(8, id);
            rowsAffected = ps.executeUpdate();
        }
        
        return rowsAffected + " filas afectadas";
    }

    /**
     * Obtiene la lista de todos los continentes
     */
    public List<Continente> listarContinentes() throws SQLException {
        List<Continente> lista = new ArrayList<>();
        
        String sql = "SELECT c.*, p.nombre as planetaNombre FROM continente c JOIN planeta p ON c.PlanetaID = p.id";
        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Continente continente = mapRowToContinente(rs);
                lista.add(continente);
            }
        }
        
        return lista;
    }

    /**
     * Obtiene un continente específico por su ID
     */
    public Continente obtenerContinentePorId(Integer id) throws SQLException {
        Continente continente = null;
        
        String sql = "SELECT c.*, p.nombre as planetaNombre FROM continente c JOIN planeta p ON c.PlanetaID = p.id WHERE c.id = ?";
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    continente = mapRowToContinente(rs);
                }
            }
        }
        
        return continente;
    }

    /**
     * Filtra continentes según varios criterios opcionales
     */
    public List<Continente> filtrarContinentes(Integer planetaId, String hemisferio, String clima, Boolean habitable) throws SQLException {
        List<Continente> lista = new ArrayList<>();
        
        StringBuilder query = new StringBuilder("SELECT c.*, p.nombre as planetaNombre FROM continente c JOIN planeta p ON c.PlanetaID = p.id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (planetaId != null) {
            query.append(" AND c.PlanetaID = ?");
            params.add(planetaId);
        }

        if (hemisferio != null && !hemisferio.isEmpty()) {
            query.append(" AND c.hemisferio = ?");
            params.add(hemisferio);
        }

        if (clima != null && !clima.isEmpty()) {
            query.append(" AND c.clima = ?");
            params.add(clima);
        }

        if (habitable != null) {
            query.append(" AND c.habitable = ?");
            params.add(habitable);
        }

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(query.toString())) {

            // Configurar parámetros dinámicamente
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Continente continente = mapRowToContinente(rs);
                    lista.add(continente);
                }
            }
        }
        
        return lista;
    }

    /**
     * Método auxiliar para mapear una fila de ResultSet a un objeto Continente
     */
    private Continente mapRowToContinente(ResultSet rs) throws SQLException {
        Continente continente = new Continente();
        continente.setId(rs.getInt("id"));
        continente.setNombre(rs.getString("nombre"));
        continente.setPlanetaId(rs.getInt("PlanetaID"));
        continente.setPlanetaNombre(rs.getString("planetaNombre"));
        continente.setHemisferio(rs.getString("hemisferio"));
        continente.setClima(rs.getString("clima"));
        continente.setTamanio(rs.getFloat("Tamanio"));
        continente.setHabitable(rs.getBoolean("habitable"));
        continente.setDescripcion(rs.getString("descripcion"));
        return continente;
    }

    /* ----- Métodos para gestión de Biomas ----- */

    /**
     * Busca biomas que coincidan con el nombre proporcionado
     */
    public List<Bioma> buscarBiomas(String nombre) throws SQLException {
        List<Bioma> lista = new ArrayList<>();
        
        String sql = "SELECT b.*, c.nombre as continenteNombre FROM bioma b JOIN continente c ON b.ContinenteID = c.id WHERE b.nombre LIKE ?";
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bioma bioma = mapRowToBioma(rs);
                    lista.add(bioma);
                }
            }
        }
        
        return lista;
    }

    /**
     * Añade un nuevo bioma a la base de datos
     */
    public String aniadirBioma(String nombre, Integer continenteId, String clima, Float porcentajeHumedad, 
                            String precipitaciones, Float temperaturaMedia) throws SQLException {
        int rowsAffected = 0;
        
        String sql = "INSERT INTO bioma (nombre, ContinenteID, clima, PorcentajeHumedad, Precipitaciones, TemperaturaMedia) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, continenteId);
            ps.setString(3, clima);
            
            if (porcentajeHumedad != null) {
                ps.setFloat(4, porcentajeHumedad);
            } else {
                ps.setNull(4, java.sql.Types.FLOAT);
            }
            
            ps.setString(5, precipitaciones);
            
            if (temperaturaMedia != null) {
                ps.setFloat(6, temperaturaMedia);
            } else {
                ps.setNull(6, java.sql.Types.FLOAT);
            }
            
            rowsAffected = ps.executeUpdate();
        }
        
        return rowsAffected + " filas afectadas";
    }

    /**
     * Elimina un bioma por su ID
     */
    public String eliminarBioma(Integer id) throws SQLException {
        int rowsAffected = 0;
        
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement("DELETE FROM bioma WHERE id = ?")) {
            ps.setInt(1, id);
            rowsAffected = ps.executeUpdate();
        }
        
        return rowsAffected + " filas afectadas";
    }

    /**
     * Actualiza los datos de un bioma existente
     */
    public String actualizarBioma(Integer id, String nombre, Integer continenteId, String clima, 
                              Float porcentajeHumedad, String precipitaciones, Float temperaturaMedia) throws SQLException {
        int rowsAffected = 0;
        
        String sql = "UPDATE bioma SET nombre = ?, ContinenteID = ?, clima = ?, PorcentajeHumedad = ?, " +
                    "Precipitaciones = ?, TemperaturaMedia = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, continenteId);
            ps.setString(3, clima);
            
            if (porcentajeHumedad != null) {
                ps.setFloat(4, porcentajeHumedad);
            } else {
                ps.setNull(4, java.sql.Types.FLOAT);
            }
            
            ps.setString(5, precipitaciones);
            
            if (temperaturaMedia != null) {
                ps.setFloat(6, temperaturaMedia);
            } else {
                ps.setNull(6, java.sql.Types.FLOAT);
            }
            
            ps.setInt(7, id);
            rowsAffected = ps.executeUpdate();
        }
        
        return rowsAffected + " filas afectadas";
    }

    /**
     * Obtiene la lista de todos los biomas
     */
    public List<Bioma> listarBiomas() throws SQLException {
        List<Bioma> lista = new ArrayList<>();
        
        String sql = "SELECT b.*, c.nombre as continenteNombre FROM bioma b JOIN continente c ON b.ContinenteID = c.id";
        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Bioma bioma = mapRowToBioma(rs);
                lista.add(bioma);
            }
        }
        
        return lista;
    }

    /**
     * Obtiene un bioma específico por su ID
     */
    public Bioma obtenerBiomaPorId(Integer id) throws SQLException {
        Bioma bioma = null;
        
        String sql = "SELECT b.*, c.nombre as continenteNombre FROM bioma b JOIN continente c ON b.ContinenteID = c.id WHERE b.id = ?";
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    bioma = mapRowToBioma(rs);
                }
            }
        }
        
        return bioma;
    }

    /**
     * Filtra biomas según varios criterios opcionales
     */
    public List<Bioma> filtrarBiomas(Integer continenteId, String clima, Float humedadMinima, 
                                    Float tempMin, Float tempMax) throws SQLException {
        List<Bioma> lista = new ArrayList<>();
        
        StringBuilder query = new StringBuilder(
            "SELECT b.*, c.nombre as continenteNombre FROM bioma b " +
            "JOIN continente c ON b.ContinenteID = c.id WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (continenteId != null) {
            query.append(" AND b.ContinenteID = ?");
            params.add(continenteId);
        }

        if (clima != null && !clima.isEmpty()) {
            query.append(" AND b.clima = ?");
            params.add(clima);
        }

        if (humedadMinima != null) {
            query.append(" AND b.PorcentajeHumedad >= ?");
            params.add(humedadMinima);
        }

        if (tempMin != null) {
            query.append(" AND b.TemperaturaMedia >= ?");
            params.add(tempMin);
        }

        if (tempMax != null) {
            query.append(" AND b.TemperaturaMedia <= ?");
            params.add(tempMax);
        }

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(query.toString())) {

            // Configurar parámetros dinámicamente
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bioma bioma = mapRowToBioma(rs);
                    lista.add(bioma);
                }
            }
        }
        
        return lista;
    }

    /**
     * Método auxiliar para mapear una fila de ResultSet a un objeto Bioma
     */
    private Bioma mapRowToBioma(ResultSet rs) throws SQLException {
        Bioma bioma = new Bioma();
        bioma.setId(rs.getInt("id"));
        bioma.setNombre(rs.getString("nombre"));
        bioma.setContinenteId(rs.getInt("ContinenteID"));
        bioma.setContinenteNombre(rs.getString("continenteNombre"));
        bioma.setClima(rs.getString("clima"));
        
        // Manejar valores nulos
        float porcentajeHumedad = rs.getFloat("PorcentajeHumedad");
        if (!rs.wasNull()) {
            bioma.setPorcentajeHumedad(porcentajeHumedad);
        }
        
        bioma.setPrecipitaciones(rs.getString("Precipitaciones"));
        
        float temperaturaMedia = rs.getFloat("TemperaturaMedia");
        if (!rs.wasNull()) {
            bioma.setTemperaturaMedia(temperaturaMedia);
        }
        
        return bioma;
    }

    /* ----- Métodos para gestión de Razas ----- */
    
    /**
     * Obtiene la lista de todas las razas
     */
    public List<Raza> listarRazas() throws SQLException {
        List<Raza> lista = new ArrayList<>();
        
        String sql = "SELECT r.*, e.* FROM raza r LEFT JOIN estadisticas e ON r.EstadisticasBaseID = e.ID";
        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Raza raza = mapRowToRaza(rs);
                lista.add(raza);
            }
        }
        
        return lista;
    }

    /**
     * Busca razas que coincidan con el nombre proporcionado
     */
    public List<Raza> buscarRazas(String nombre) throws SQLException {
        List<Raza> lista = new ArrayList<>();
        
        String sql = "SELECT r.*, e.* FROM raza r LEFT JOIN estadisticas e ON r.EstadisticasBaseID = e.ID WHERE r.Nombre LIKE ?";
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Raza raza = mapRowToRaza(rs);
                    lista.add(raza);
                }
            }
        }
        
        return lista;
    }

    /**
     * Obtiene una raza específica por su ID
     */
    public Raza obtenerRazaPorId(Integer id) throws SQLException {
        Raza raza = null;
        
        String sql = "SELECT r.*, e.* FROM raza r LEFT JOIN estadisticas e ON r.EstadisticasBaseID = e.ID WHERE r.ID = ?";
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    raza = mapRowToRaza(rs);
                }
            }
        }
        
        return raza;
    }

    /**
     * Añade una nueva raza a la base de datos
     */
    public String aniadirRaza(String nombre, String descripcionFisica, Date fechaConcepcion, Float alturaPromedia, Float anchoPromedio, 
                            Integer atk, Integer def, Integer hp, Integer spe, Integer mat, Integer mdf) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);
            
            try {
                // Primero creamos las estadísticas base
                Integer estadisticasId = null;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO estadisticas (Tipo, ATK, DEF, HP, SPE, MAT, MDF, XP, LVL) VALUES (?, ?, ?, ?, ?, ?, ?, 0, 1)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, "Base");
                    ps.setInt(2, atk);
                    ps.setInt(3, def);
                    ps.setInt(4, hp);
                    ps.setInt(5, spe);
                    ps.setInt(6, mat);
                    ps.setInt(7, mdf);
                    ps.executeUpdate();
                    
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            estadisticasId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("Fallo al obtener ID de estadísticas.");
                        }
                    }
                }
                
                // Luego creamos la raza con el ID de estadísticas
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO raza (Nombre, DescripcionFisica, FechaConcepcion, AlturaPromedia, AnchoPromedio, EstadisticasBaseID) VALUES (?, ?, ?, ?, ?, ?)")) {
                    ps.setString(1, nombre);
                    ps.setString(2, descripcionFisica);
                    ps.setDate(3, fechaConcepcion);
                    
                    if (alturaPromedia != null) {
                        ps.setFloat(4, alturaPromedia);
                    } else {
                        ps.setNull(4, java.sql.Types.FLOAT);
                    }
                    
                    if (anchoPromedio != null) {
                        ps.setFloat(5, anchoPromedio);
                    } else {
                        ps.setNull(5, java.sql.Types.FLOAT);
                    }
                    
                    ps.setInt(6, estadisticasId);
                    ps.executeUpdate();
                }
                
                con.commit();
                return "Raza añadida correctamente.";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Actualiza los datos de una raza existente
     */
    public String actualizarRaza(Integer id, String nombre, String descripcionFisica, Date fechaConcepcion, Float alturaPromedia, Float anchoPromedio, 
                               Integer atk, Integer def, Integer hp, Integer spe, Integer mat, Integer mdf) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);
            
            try {
                // Primero obtenemos el ID de las estadísticas
                Integer estadisticasId = null;
                try (PreparedStatement ps = con.prepareStatement("SELECT EstadisticasBaseID FROM raza WHERE ID = ?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            estadisticasId = rs.getInt("EstadisticasBaseID");
                        } else {
                            throw new SQLException("Raza no encontrada.");
                        }
                    }
                }
                
                // Actualizamos las estadísticas
                if (estadisticasId != null) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE estadisticas SET ATK = ?, DEF = ?, HP = ?, SPE = ?, MAT = ?, MDF = ? WHERE ID = ?")) {
                        ps.setInt(1, atk);
                        ps.setInt(2, def);
                        ps.setInt(3, hp);
                        ps.setInt(4, spe);
                        ps.setInt(5, mat);
                        ps.setInt(6, mdf);
                        ps.setInt(7, estadisticasId);
                        ps.executeUpdate();
                    }
                }
                
                // Actualizamos la raza
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE raza SET Nombre = ?, DescripcionFisica = ?, FechaConcepcion = ?, AlturaPromedia = ?, AnchoPromedio = ? WHERE ID = ?")) {
                    ps.setString(1, nombre);
                    ps.setString(2, descripcionFisica);
                    ps.setDate(3, fechaConcepcion);
                    
                    if (alturaPromedia != null) {
                        ps.setFloat(4, alturaPromedia);
                    } else {
                        ps.setNull(4, java.sql.Types.FLOAT);
                    }
                    
                    if (anchoPromedio != null) {
                        ps.setFloat(5, anchoPromedio);
                    } else {
                        ps.setNull(5, java.sql.Types.FLOAT);
                    }
                    
                    ps.setInt(6, id);
                    int rowsAffected = ps.executeUpdate();
                    
                    if (rowsAffected == 0) {
                        throw new SQLException("No se pudo actualizar la raza, ID no encontrado.");
                    }
                }
                
                con.commit();
                return "Raza actualizada correctamente.";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Elimina una raza por su ID
     */
    public String eliminarRaza(Integer id) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);
            
            try {
                // Primero obtenemos el ID de las estadísticas
                Integer estadisticasId = null;
                try (PreparedStatement ps = con.prepareStatement("SELECT EstadisticasBaseID FROM raza WHERE ID = ?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            estadisticasId = rs.getInt("EstadisticasBaseID");
                        }
                    }
                }
                
                // Eliminamos la raza
                int rowsAffected = 0;
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM raza WHERE ID = ?")) {
                    ps.setInt(1, id);
                    rowsAffected = ps.executeUpdate();
                }
                
                // Si existe el ID de estadísticas, las eliminamos
                if (estadisticasId != null) {
                    try (PreparedStatement ps = con.prepareStatement("DELETE FROM estadisticas WHERE ID = ?")) {
                        ps.setInt(1, estadisticasId);
                        ps.executeUpdate();
                    }
                }
                
                con.commit();
                return rowsAffected + " filas afectadas";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Filtra razas según varios criterios opcionales
     */
    public List<Raza> filtrarRazas(Float alturaMinima, Float anchoMinimo) throws SQLException {
        List<Raza> lista = new ArrayList<>();
        
        StringBuilder query = new StringBuilder("SELECT r.*, e.* FROM raza r LEFT JOIN estadisticas e ON r.EstadisticasBaseID = e.ID WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (alturaMinima != null) {
            query.append(" AND r.AlturaPromedia >= ?");
            params.add(alturaMinima);
        }

        if (anchoMinimo != null) {
            query.append(" AND r.AnchoPromedio >= ?");
            params.add(anchoMinimo);
        }

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(query.toString())) {

            // Configurar parámetros dinámicamente
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Raza raza = mapRowToRaza(rs);
                    lista.add(raza);
                }
            }
        }
        
        return lista;
    }

    /**
     * Método auxiliar para mapear una fila de ResultSet a un objeto Raza
     */
    private Raza mapRowToRaza(ResultSet rs) throws SQLException {
        Raza raza = new Raza();
        raza.setId(rs.getInt("ID"));
        raza.setNombre(rs.getString("Nombre"));
        raza.setDescripcionFisica(rs.getString("DescripcionFisica"));
        raza.setFechaConcepcion(rs.getDate("FechaConcepcion"));
        
        float alturaPromedia = rs.getFloat("AlturaPromedia");
        if (!rs.wasNull()) {
            raza.setAlturaPromedia(alturaPromedia);
        }
        
        float anchoPromedio = rs.getFloat("AnchoPromedio");
        if (!rs.wasNull()) {
            raza.setAnchoPromedio(anchoPromedio);
        }
        
        // Mapear estadísticas si existen
        Integer estadisticasId = rs.getInt("EstadisticasBaseID");
        if (!rs.wasNull()) {
            Estadisticas estadisticas = new Estadisticas();
            estadisticas.setId(estadisticasId);
            estadisticas.setTipo(rs.getString("Tipo"));
            estadisticas.setAtk(rs.getInt("ATK"));
            estadisticas.setDef(rs.getInt("DEF"));
            estadisticas.setHp(rs.getInt("HP"));
            estadisticas.setSpe(rs.getInt("SPE"));
            estadisticas.setMat(rs.getInt("MAT"));
            estadisticas.setMdf(rs.getInt("MDF"));
            estadisticas.setXp(rs.getInt("XP"));
            estadisticas.setLvl(rs.getInt("LVL"));
            raza.setEstadisticasBase(estadisticas);
        }
        
        return raza;
    }

    /* ----- Métodos para gestión de Imperios ----- */

/**
 * Obtiene la lista de todos los imperios
 */
public List<Imperio> listarImperios() throws SQLException {
    List<Imperio> lista = new ArrayList<>();
    
    try (Connection con = DriverManager.getConnection(url);
         Statement st = con.createStatement();
         ResultSet rs = st.executeQuery("SELECT * FROM imperio")) {
        while (rs.next()) {
            Imperio imperio = mapRowToImperio(rs);
            lista.add(imperio);
        }
    }
    
    return lista;
}

/**
 * Busca imperios que coincidan con el nombre proporcionado
 */
public List<Imperio> buscarImperios(String nombre) throws SQLException {
    List<Imperio> lista = new ArrayList<>();
    
    try (Connection con = DriverManager.getConnection(url);
         PreparedStatement ps = con.prepareStatement("SELECT * FROM imperio WHERE Nombre LIKE ?")) {
        ps.setString(1, "%" + nombre + "%");
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Imperio imperio = mapRowToImperio(rs);
                lista.add(imperio);
            }
        }
    }
    
    return lista;
}

/**
 * Obtiene un imperio específico por su ID
 */
public Imperio obtenerImperioPorId(Integer id) throws SQLException {
    Imperio imperio = null;
    
    try (Connection con = DriverManager.getConnection(url);
         PreparedStatement ps = con.prepareStatement("SELECT * FROM imperio WHERE ID = ?")) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                imperio = mapRowToImperio(rs);
            }
        }
    }
    
    return imperio;
}

/**
 * Añade un nuevo imperio a la base de datos
 */
public String aniadirImperio(String nombre, Integer poblacion, String descripcion, Date fechaCreacion, String lider, 
                        String ideologia, Float gdp, Float tamanio, Boolean enGuerra) throws SQLException {
    int rowsAffected = 0;
    
    try (Connection con = DriverManager.getConnection(url);
         PreparedStatement ps = con.prepareStatement(
             "INSERT INTO imperio (Nombre, Poblacion, Descripcion, FechaCreacion, Lider, Ideologia, GDP, Tamanio, EnGuerra) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
        ps.setString(1, nombre);
        
        if (poblacion != null) {
            ps.setInt(2, poblacion);
        } else {
            ps.setNull(2, java.sql.Types.INTEGER);
        }
        
        ps.setString(3, descripcion);
        ps.setDate(4, fechaCreacion);
        ps.setString(5, lider);
        ps.setString(6, ideologia);
        
        if (gdp != null) {
            ps.setFloat(7, gdp);
        } else {
            ps.setNull(7, java.sql.Types.FLOAT);
        }
        
        if (tamanio != null) {
            ps.setFloat(8, tamanio);
        } else {
            ps.setNull(8, java.sql.Types.FLOAT);
        }
        
        ps.setBoolean(9, enGuerra);
        rowsAffected = ps.executeUpdate();
    }
    
    return rowsAffected + " filas afectadas";
}

/**
 * Actualiza los datos de un imperio existente
 */
public String actualizarImperio(Integer id, String nombre, Integer poblacion, String descripcion, Date fechaCreacion, 
                             String lider, String ideologia, Float gdp, Float tamanio, Boolean enGuerra) throws SQLException {
    int rowsAffected = 0;
    
    try (Connection con = DriverManager.getConnection(url);
         PreparedStatement ps = con.prepareStatement(
             "UPDATE imperio SET Nombre = ?, Poblacion = ?, Descripcion = ?, FechaCreacion = ?, Lider = ?, Ideologia = ?, GDP = ?, Tamanio = ?, EnGuerra = ? WHERE ID = ?")) {
        ps.setString(1, nombre);
        
        if (poblacion != null) {
            ps.setInt(2, poblacion);
        } else {
            ps.setNull(2, java.sql.Types.INTEGER);
        }
        
        ps.setString(3, descripcion);
        ps.setDate(4, fechaCreacion);
        ps.setString(5, lider);
        ps.setString(6, ideologia);
        
        if (gdp != null) {
            ps.setFloat(7, gdp);
        } else {
            ps.setNull(7, java.sql.Types.FLOAT);
        }
        
        if (tamanio != null) {
            ps.setFloat(8, tamanio);
        } else {
            ps.setNull(8, java.sql.Types.FLOAT);
        }
        
        ps.setBoolean(9, enGuerra);
        ps.setInt(10, id);
        rowsAffected = ps.executeUpdate();
    }
    
    return rowsAffected + " filas afectadas";
}

/**
 * Elimina un imperio por su ID
 */
public String eliminarImperio(Integer id) throws SQLException {
    int rowsAffected = 0;
    
    try (Connection con = DriverManager.getConnection(url);
         PreparedStatement ps = con.prepareStatement("DELETE FROM imperio WHERE ID = ?")) {
        ps.setInt(1, id);
        rowsAffected = ps.executeUpdate();
    }
    
    return rowsAffected + " filas afectadas";
}

/**
 * Filtra imperios según varios criterios opcionales
 */
public List<Imperio> filtrarImperios(Integer poblacionMinima, String ideologia, Boolean enGuerra) throws SQLException {
    List<Imperio> lista = new ArrayList<>();
    
    StringBuilder query = new StringBuilder("SELECT * FROM imperio WHERE 1=1");
    List<Object> params = new ArrayList<>();

    if (poblacionMinima != null) {
        query.append(" AND Poblacion >= ?");
        params.add(poblacionMinima);
    }

    if (ideologia != null && !ideologia.isEmpty()) {
        query.append(" AND Ideologia = ?");
        params.add(ideologia);
    }

    if (enGuerra != null) {
        query.append(" AND EnGuerra = ?");
        params.add(enGuerra);
    }

    try (Connection con = DriverManager.getConnection(url);
         PreparedStatement ps = con.prepareStatement(query.toString())) {

        // Configurar parámetros dinámicamente
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Imperio imperio = mapRowToImperio(rs);
                lista.add(imperio);
            }
        }
    }
    
    return lista;
}

/**
 * Método auxiliar para mapear una fila de ResultSet a un objeto Imperio
 */
private Imperio mapRowToImperio(ResultSet rs) throws SQLException {
    Imperio imperio = new Imperio();
    imperio.setId(rs.getInt("ID"));
    imperio.setNombre(rs.getString("Nombre"));
    
    int poblacion = rs.getInt("Poblacion");
    if (!rs.wasNull()) {
        imperio.setPoblacion(poblacion);
    }
    
    imperio.setDescripcion(rs.getString("Descripcion"));
    imperio.setFechaCreacion(rs.getDate("FechaCreacion"));
    imperio.setLider(rs.getString("Lider"));
    imperio.setIdeologia(rs.getString("Ideologia"));
    
    float gdp = rs.getFloat("GDP");
    if (!rs.wasNull()) {
        imperio.setGdp(gdp);
    }
    
    float tamanio = rs.getFloat("Tamanio");
    if (!rs.wasNull()) {
        imperio.setTamanio(tamanio);
    }
    
    imperio.setEnGuerra(rs.getBoolean("EnGuerra"));
    
    return imperio;
}

/* ----- Métodos para gestión de Guerras ----- */

/**
 * Obtiene la lista de todas las guerras
 */
public List<Guerra> listarGuerras() throws SQLException {
    List<Guerra> lista = new ArrayList<>();
    Map<Integer, Guerra> guerrasMap = new HashMap<>();
    
    String sql = "SELECT g.*, ig.ImperioID, ig.Rol, ig.Ganador, i.Nombre as ImperioNombre " + 
                "FROM guerra g " + 
                "LEFT JOIN imperio_guerra ig ON g.ID = ig.GuerraID " + 
                "LEFT JOIN imperio i ON ig.ImperioID = i.ID " + 
                "ORDER BY g.ID";
    
    try (Connection con = DriverManager.getConnection(url);
         Statement st = con.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
        
        while (rs.next()) {
            int guerraId = rs.getInt("g.ID");
            
            // Si es la primera vez que vemos esta guerra, la creamos
            if (!guerrasMap.containsKey(guerraId)) {
                Guerra guerra = new Guerra();
                guerra.setId(guerraId);
                guerra.setNombre(rs.getString("g.Nombre"));
                guerra.setFechaInicio(rs.getDate("g.FechaInicio"));
                guerra.setFechaFin(rs.getDate("g.FechaFin"));
                guerra.setDescripcion(rs.getString("g.Descripcion"));
                guerra.setImperiosParticipantes(new ArrayList<>());
                
                guerrasMap.put(guerraId, guerra);
                lista.add(guerra);
            }
            
            // Añadir participante si existe
            Guerra guerra = guerrasMap.get(guerraId);
            int imperioId = rs.getInt("ImperioID");
            if (!rs.wasNull()) {
                ParticipanteGuerra participante = new ParticipanteGuerra();
                participante.setImperioId(imperioId);
                participante.setImperioNombre(rs.getString("ImperioNombre"));
                participante.setRol(rs.getString("Rol"));
                
                boolean ganador = rs.getBoolean("Ganador");
                if (!rs.wasNull()) {
                    participante.setGanador(ganador);
                } else {
                    participante.setGanador(null);
                }
                
                guerra.getImperiosParticipantes().add(participante);
            }
        }
    }
    
    return lista;
}

/**
 * Busca guerras que coincidan con el nombre proporcionado
 */
public List<Guerra> buscarGuerras(String nombre) throws SQLException {
    List<Guerra> lista = new ArrayList<>();
    Map<Integer, Guerra> guerrasMap = new HashMap<>();
    
    String sql = "SELECT g.*, ig.ImperioID, ig.Rol, ig.Ganador, i.Nombre as ImperioNombre " + 
                "FROM guerra g " + 
                "LEFT JOIN imperio_guerra ig ON g.ID = ig.GuerraID " + 
                "LEFT JOIN imperio i ON ig.ImperioID = i.ID " + 
                "WHERE g.Nombre LIKE ? " + 
                "ORDER BY g.ID";
    
    try (Connection con = DriverManager.getConnection(url);
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, "%" + nombre + "%");
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int guerraId = rs.getInt("g.ID");
                
                // Si es la primera vez que vemos esta guerra, la creamos
                if (!guerrasMap.containsKey(guerraId)) {
                    Guerra guerra = new Guerra();
                    guerra.setId(guerraId);
                    guerra.setNombre(rs.getString("g.Nombre"));
                    guerra.setFechaInicio(rs.getDate("g.FechaInicio"));
                    guerra.setFechaFin(rs.getDate("g.FechaFin"));
                    guerra.setDescripcion(rs.getString("g.Descripcion"));
                    guerra.setImperiosParticipantes(new ArrayList<>());
                    
                    guerrasMap.put(guerraId, guerra);
                    lista.add(guerra);
                }
                
                // Añadir participante si existe
                Guerra guerra = guerrasMap.get(guerraId);
                int imperioId = rs.getInt("ImperioID");
                if (!rs.wasNull()) {
                    ParticipanteGuerra participante = new ParticipanteGuerra();
                    participante.setImperioId(imperioId);
                    participante.setImperioNombre(rs.getString("ImperioNombre"));
                    participante.setRol(rs.getString("Rol"));
                    
                    boolean ganador = rs.getBoolean("Ganador");
                    if (!rs.wasNull()) {
                        participante.setGanador(ganador);
                    } else {
                        participante.setGanador(null);
                    }
                    
                    guerra.getImperiosParticipantes().add(participante);
                }
            }
        }
    }
    
    return lista;
}

/**
 * Obtiene una guerra específica por su ID
 */
public Guerra obtenerGuerraPorId(Integer id) throws SQLException {
    Guerra guerra = null;
    
    String sql = "SELECT g.*, ig.ImperioID, ig.Rol, ig.Ganador, i.Nombre as ImperioNombre " + 
                "FROM guerra g " + 
                "LEFT JOIN imperio_guerra ig ON g.ID = ig.GuerraID " + 
                "LEFT JOIN imperio i ON ig.ImperioID = i.ID " + 
                "WHERE g.ID = ?";
    
    try (Connection con = DriverManager.getConnection(url);
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, id);
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Si es la primera vez, creamos la guerra
                if (guerra == null) {
                    guerra = new Guerra();
                    guerra.setId(rs.getInt("g.ID"));
                    guerra.setNombre(rs.getString("g.Nombre"));
                    guerra.setFechaInicio(rs.getDate("g.FechaInicio"));
                    guerra.setFechaFin(rs.getDate("g.FechaFin"));
                    guerra.setDescripcion(rs.getString("g.Descripcion"));
                    guerra.setImperiosParticipantes(new ArrayList<>());
                }
                
                // Añadir participante si existe
                int imperioId = rs.getInt("ImperioID");
                if (!rs.wasNull()) {
                    ParticipanteGuerra participante = new ParticipanteGuerra();
                    participante.setImperioId(imperioId);
                    participante.setImperioNombre(rs.getString("ImperioNombre"));
                    participante.setRol(rs.getString("Rol"));
                    
                    boolean ganador = rs.getBoolean("Ganador");
                    if (!rs.wasNull()) {
                        participante.setGanador(ganador);
                    } else {
                        participante.setGanador(null);
                    }
                    
                    guerra.getImperiosParticipantes().add(participante);
                }
            }
        }
    }
    
    return guerra;
}

/**
 * Añade una nueva guerra a la base de datos
 */
public String aniadirGuerra(Guerra guerra) throws SQLException {
    try (Connection con = DriverManager.getConnection(url)) {
        con.setAutoCommit(false);
        
        try {
            // Primero insertamos la guerra
            Integer guerraId = null;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO guerra (Nombre, FechaInicio, FechaFin, Descripcion) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, guerra.getNombre());
                ps.setDate(2, guerra.getFechaInicio());
                
                if (guerra.getFechaFin() != null) {
                    ps.setDate(3, guerra.getFechaFin());
                } else {
                    ps.setNull(3, java.sql.Types.DATE);
                }
                
                ps.setString(4, guerra.getDescripcion());
                ps.executeUpdate();
                
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        guerraId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Fallo al obtener ID de guerra.");
                    }
                }
            }
            
            // Luego insertamos los participantes
            if (guerra.getImperiosParticipantes() != null && !guerra.getImperiosParticipantes().isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO imperio_guerra (ImperioID, GuerraID, Rol, Ganador) VALUES (?, ?, ?, ?)")) {
                    for (ParticipanteGuerra participante : guerra.getImperiosParticipantes()) {
                        ps.setInt(1, participante.getImperioId());
                        ps.setInt(2, guerraId);
                        ps.setString(3, participante.getRol());
                        
                        if (participante.getGanador() != null) {
                            ps.setBoolean(4, participante.getGanador());
                        } else {
                            ps.setNull(4, java.sql.Types.BOOLEAN);
                        }
                        
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                
                // Actualizar el estado EnGuerra de los imperios participantes
                try (PreparedStatement ps = con.prepareStatement("UPDATE imperio SET EnGuerra = TRUE WHERE ID = ?")) {
                    for (ParticipanteGuerra participante : guerra.getImperiosParticipantes()) {
                        ps.setInt(1, participante.getImperioId());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            
            con.commit();
            return "Guerra añadida correctamente.";
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }
}

/**
 * Actualiza los datos de una guerra existente
 */
public String actualizarGuerra(Guerra guerra) throws SQLException {
    try (Connection con = DriverManager.getConnection(url)) {
        con.setAutoCommit(false);
        
        try {
            // Primero actualizamos la guerra
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE guerra SET Nombre = ?, FechaInicio = ?, FechaFin = ?, Descripcion = ? WHERE ID = ?")) {
                ps.setString(1, guerra.getNombre());
                ps.setDate(2, guerra.getFechaInicio());
                
                if (guerra.getFechaFin() != null) {
                    ps.setDate(3, guerra.getFechaFin());
                } else {
                    ps.setNull(3, java.sql.Types.DATE);
                }
                
                ps.setString(4, guerra.getDescripcion());
                ps.setInt(5, guerra.getId());
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected == 0) {
                    throw new SQLException("No se pudo actualizar la guerra, ID no encontrado.");
                }
            }
            
            // Obtenemos los participantes actuales para compararlos
            List<Integer> imperiosActuales = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT ImperioID FROM imperio_guerra WHERE GuerraID = ?")) {
                ps.setInt(1, guerra.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        imperiosActuales.add(rs.getInt("ImperioID"));
                    }
                }
            }
            
            // Eliminamos todos los participantes actuales
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM imperio_guerra WHERE GuerraID = ?")) {
                ps.setInt(1, guerra.getId());
                ps.executeUpdate();
            }
            
            // Insertamos los nuevos participantes
            if (guerra.getImperiosParticipantes() != null && !guerra.getImperiosParticipantes().isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO imperio_guerra (ImperioID, GuerraID, Rol, Ganador) VALUES (?, ?, ?, ?)")) {
                    
                    List<Integer> nuevosImperios = new ArrayList<>();
                    for (ParticipanteGuerra participante : guerra.getImperiosParticipantes()) {
                        ps.setInt(1, participante.getImperioId());
                        ps.setInt(2, guerra.getId());
                        ps.setString(3, participante.getRol());
                        
                        if (participante.getGanador() != null) {
                            ps.setBoolean(4, participante.getGanador());
                        } else {
                            ps.setNull(4, java.sql.Types.BOOLEAN);
                        }
                        
                        ps.addBatch();
                        nuevosImperios.add(participante.getImperioId());
                    }
                    ps.executeBatch();
                    
                    // Actualizar el estado EnGuerra de los imperios
                    // Primero, para los nuevos
                    if (!nuevosImperios.isEmpty()) {
                        StringBuilder queryBuilder = new StringBuilder("UPDATE imperio SET EnGuerra = TRUE WHERE ID IN (");
                        for (int i = 0; i < nuevosImperios.size(); i++) {
                            if (i > 0) queryBuilder.append(",");
                            queryBuilder.append("?");
                        }
                        queryBuilder.append(")");
                        
                        try (PreparedStatement ps2 = con.prepareStatement(queryBuilder.toString())) {
                            for (int i = 0; i < nuevosImperios.size(); i++) {
                                ps2.setInt(i + 1, nuevosImperios.get(i));
                            }
                            ps2.executeUpdate();
                        }
                    }
                    
                    // Luego, para los que ya no participan
                    imperiosActuales.removeAll(nuevosImperios);
                    if (!imperiosActuales.isEmpty()) {
                        // Comprobamos si estos imperios participan en otras guerras
                        for (Integer imperioId : imperiosActuales) {
                            boolean participaEnOtraGuerra = false;
                            try (PreparedStatement ps2 = con.prepareStatement(
                                    "SELECT COUNT(*) FROM imperio_guerra WHERE ImperioID = ?")) {
                                ps2.setInt(1, imperioId);
                                try (ResultSet rs = ps2.executeQuery()) {
                                    if (rs.next() && rs.getInt(1) > 0) {
                                        participaEnOtraGuerra = true;
                                    }
                                }
                            }
                            
                            // Si no participa en otra guerra, actualizamos su estado
                            if (!participaEnOtraGuerra) {
                                try (PreparedStatement ps2 = con.prepareStatement(
                                        "UPDATE imperio SET EnGuerra = FALSE WHERE ID = ?")) {
                                    ps2.setInt(1, imperioId);
                                    ps2.executeUpdate();
                                }
                            }
                        }
                    }
                }
            } else {
                // Si no hay participantes nuevos, actualizamos el estado de los antiguos
                for (Integer imperioId : imperiosActuales) {
                    boolean participaEnOtraGuerra = false;
                    try (PreparedStatement ps = con.prepareStatement(
                            "SELECT COUNT(*) FROM imperio_guerra WHERE ImperioID = ?")) {
                        ps.setInt(1, imperioId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                participaEnOtraGuerra = true;
                            }
                        }
                    }
                    
                    if (!participaEnOtraGuerra) {
                        try (PreparedStatement ps = con.prepareStatement(
                                "UPDATE imperio SET EnGuerra = FALSE WHERE ID = ?")) {
                            ps.setInt(1, imperioId);
                            ps.executeUpdate();
                        }
                    }
                }
            }
            
            con.commit();
            return "Guerra actualizada correctamente.";
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }
}

/**
 * Elimina una guerra por su ID
 */
public String eliminarGuerra(Integer id) throws SQLException {
    try (Connection con = DriverManager.getConnection(url)) {
        con.setAutoCommit(false);
        
        try {
            // Primero obtenemos los imperios participantes
            List<Integer> imperiosParticipantes = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT ImperioID FROM imperio_guerra WHERE GuerraID = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        imperiosParticipantes.add(rs.getInt("ImperioID"));
                    }
                }
            }
            
            // Eliminamos las relaciones en la tabla imperio_guerra
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM imperio_guerra WHERE GuerraID = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            
            // Eliminamos la guerra
            int rowsAffected = 0;
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM guerra WHERE ID = ?")) {
                ps.setInt(1, id);
                rowsAffected = ps.executeUpdate();
            }
            
            // Actualizamos el estado de guerra de los imperios
            for (Integer imperioId : imperiosParticipantes) {
                boolean participaEnOtraGuerra = false;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT COUNT(*) FROM imperio_guerra WHERE ImperioID = ?")) {
                    ps.setInt(1, imperioId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            participaEnOtraGuerra = true;
                        }
                    }
                }
                
                if (!participaEnOtraGuerra) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE imperio SET EnGuerra = FALSE WHERE ID = ?")) {
                        ps.setInt(1, imperioId);
                        ps.executeUpdate();
                    }
                }
            }
            
            con.commit();
            return rowsAffected + " filas afectadas";
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }
}

/**
 * Filtra guerras según varios criterios opcionales
 */
public List<Guerra> filtrarGuerras(String estado, Integer imperioId) throws SQLException {
    List<Guerra> lista = new ArrayList<>();
    Map<Integer, Guerra> guerrasMap = new HashMap<>();
    
    StringBuilder query = new StringBuilder(
        "SELECT g.*, ig.ImperioID, ig.Rol, ig.Ganador, i.Nombre as ImperioNombre " +
        "FROM guerra g " +
        "LEFT JOIN imperio_guerra ig ON g.ID = ig.GuerraID " +
        "LEFT JOIN imperio i ON ig.ImperioID = i.ID " +
        "WHERE 1=1"
    );
    List<Object> params = new ArrayList<>();

    if (estado != null && !estado.isEmpty()) {
        if ("activo".equalsIgnoreCase(estado)) {
            query.append(" AND g.FechaFin IS NULL");
        } else if ("finalizado".equalsIgnoreCase(estado)) {
            query.append(" AND g.FechaFin IS NOT NULL");
        }
    }

    if (imperioId != null) {
        query.append(" AND ig.ImperioID = ?");
        params.add(imperioId);
    }
    
    query.append(" ORDER BY g.ID");

    try (Connection con = DriverManager.getConnection(url);
         PreparedStatement ps = con.prepareStatement(query.toString())) {

        // Configurar parámetros dinámicamente
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int guerraId = rs.getInt("g.ID");
                
                // Si es la primera vez que vemos esta guerra, la creamos
                if (!guerrasMap.containsKey(guerraId)) {
                    Guerra guerra = new Guerra();
                    guerra.setId(guerraId);
                    guerra.setNombre(rs.getString("g.Nombre"));
                    guerra.setFechaInicio(rs.getDate("g.FechaInicio"));
                    guerra.setFechaFin(rs.getDate("g.FechaFin"));
                    guerra.setDescripcion(rs.getString("g.Descripcion"));
                    guerra.setImperiosParticipantes(new ArrayList<>());
                    
                    guerrasMap.put(guerraId, guerra);
                    lista.add(guerra);
                }
                
                // Añadir participante si existe
                Guerra guerra = guerrasMap.get(guerraId);
                int imperioId2 = rs.getInt("ImperioID");
                if (!rs.wasNull()) {
                    ParticipanteGuerra participante = new ParticipanteGuerra();
                    participante.setImperioId(imperioId2);
                    participante.setImperioNombre(rs.getString("ImperioNombre"));
                    participante.setRol(rs.getString("Rol"));
                    
                    boolean ganador = rs.getBoolean("Ganador");
                    if (!rs.wasNull()) {
                        participante.setGanador(ganador);
                    } else {
                        participante.setGanador(null);
                    }
                    
                    guerra.getImperiosParticipantes().add(participante);
                }
            }
        }
    }
    
    return lista;
    
}
}