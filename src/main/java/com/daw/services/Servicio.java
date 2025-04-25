package com.daw.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.daw.controllers.Bioma;
import com.daw.controllers.Continente;
import com.daw.controllers.Planeta;

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
}