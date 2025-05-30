package com.daw.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.daw.controllers.Arcana;
import com.daw.controllers.Arma;
import com.daw.controllers.Armadura;
import com.daw.controllers.Bioma;
import com.daw.controllers.Continente;
import com.daw.controllers.Estadisticas;
import com.daw.controllers.Guerra;
import com.daw.controllers.Herramienta;
import com.daw.controllers.Imperio;
import com.daw.controllers.ParticipanteGuerra;
import com.daw.controllers.Persona;
import com.daw.controllers.Planeta;
import com.daw.controllers.Raza;

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
    public String aniadirPlaneta(String nombre, String ubicacion, Boolean habitable, Float nivelAgua,
            Date fechaCreacion, Float tamanio, Float densidad, String descripcion) throws SQLException {
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
    public String actualizarPlaneta(Integer id, String nombre, String ubicacion, Boolean habitable, Float nivelAgua,
            Date fechaCreacion, Float tamanio, Float densidad, String descripcion) throws SQLException {
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
    public String aniadirContinente(String nombre, Integer planetaId, String hemisferio, String clima, Float tamanio,
            Boolean habitable, String descripcion) throws SQLException {
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
    public String actualizarContinente(Integer id, String nombre, Integer planetaId, String hemisferio, String clima,
            Float tamanio, Boolean habitable, String descripcion) throws SQLException {
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
    public List<Continente> filtrarContinentes(Integer planetaId, String hemisferio, String clima, Boolean habitable)
            throws SQLException {
        List<Continente> lista = new ArrayList<>();

        StringBuilder query = new StringBuilder(
                "SELECT c.*, p.nombre as planetaNombre FROM continente c JOIN planeta p ON c.PlanetaID = p.id WHERE 1=1");
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
                        "JOIN continente c ON b.ContinenteID = c.id WHERE 1=1");
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
    public String aniadirRaza(String nombre, String descripcionFisica, Date fechaConcepcion, Float alturaPromedia,
            Float anchoPromedio,
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
    public String actualizarRaza(Integer id, String nombre, String descripcionFisica, Date fechaConcepcion,
            Float alturaPromedia, Float anchoPromedio,
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

        StringBuilder query = new StringBuilder(
                "SELECT r.*, e.* FROM raza r LEFT JOIN estadisticas e ON r.EstadisticasBaseID = e.ID WHERE 1=1");
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
    public String actualizarImperio(Integer id, String nombre, Integer poblacion, String descripcion,
            Date fechaCreacion,
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
    public List<Imperio> filtrarImperios(Integer poblacionMinima, String ideologia, Boolean enGuerra)
            throws SQLException {
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
                    try (PreparedStatement ps = con
                            .prepareStatement("UPDATE imperio SET EnGuerra = TRUE WHERE ID = ?")) {
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
                try (PreparedStatement ps = con
                        .prepareStatement("SELECT ImperioID FROM imperio_guerra WHERE GuerraID = ?")) {
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
                            StringBuilder queryBuilder = new StringBuilder(
                                    "UPDATE imperio SET EnGuerra = TRUE WHERE ID IN (");
                            for (int i = 0; i < nuevosImperios.size(); i++) {
                                if (i > 0)
                                    queryBuilder.append(",");
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
                try (PreparedStatement ps = con
                        .prepareStatement("SELECT ImperioID FROM imperio_guerra WHERE GuerraID = ?")) {
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
                        "WHERE 1=1");
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

    /* ----- Métodos para gestión de Personas ----- */

    /**
     * Busca personas que coincidan con el nombre o apellido proporcionado
     */
    public List<Persona> buscarPersonas(String nombre) throws SQLException {
        List<Persona> lista = new ArrayList<>();

        String sql = "SELECT p.*, r.nombre as raza_nombre, i.nombre as imperio_nombre " +
                "FROM persona p " +
                "LEFT JOIN raza r ON p.RazaID = r.ID " +
                "LEFT JOIN imperio i ON p.ImperioID = i.ID " +
                "WHERE p.Nombre LIKE ? OR p.Apellido LIKE ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ps.setString(2, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Persona persona = mapRowToPersonaBasica(rs);
                    cargarRelacionesPersona(con, persona);
                    lista.add(persona);
                }
            }
        }

        return lista;
    }

    /**
     * Añade una nueva persona a la base de datos
     */
    public String aniadirPersona(Persona persona) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);

            try {
                // 1. Primero creamos las estadísticas
                Integer estadisticasId = insertarEstadisticas(con, persona.getEstadisticas());

                // 2. Luego creamos la persona
                Integer personaId = insertarPersona(con, persona, estadisticasId);

                // 3. Ahora manejamos las relaciones muchos a muchos
                if (persona.getArmas() != null && !persona.getArmas().isEmpty()) {
                    insertarArmasPersona(con, personaId, persona.getArmas());
                }

                if (persona.getArmaduras() != null && !persona.getArmaduras().isEmpty()) {
                    insertarArmadurasPersona(con, personaId, persona.getArmaduras());
                }

                if (persona.getHerramientas() != null && !persona.getHerramientas().isEmpty()) {
                    insertarHerramientasPersona(con, personaId, persona.getHerramientas());
                }

                if (persona.getArcanas() != null && !persona.getArcanas().isEmpty()) {
                    insertarArcanasPersona(con, personaId, persona.getArcanas());
                }

                con.commit();
                return "Persona añadida correctamente con ID: " + personaId;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Actualiza una persona existente
     */
    public String actualizarPersona(Persona persona) throws SQLException {
        if (persona.getId() == null) {
            throw new SQLException("ID de persona no proporcionado para actualización");
        }

        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);

            try {
                // 1. Verificamos si la persona existe
                boolean personaExiste = false;
                Integer estadisticasId = null;

                try (PreparedStatement ps = con.prepareStatement("SELECT EstadisticasID FROM persona WHERE ID = ?")) {
                    ps.setInt(1, persona.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            personaExiste = true;
                            estadisticasId = rs.getInt("EstadisticasID");
                            if (rs.wasNull()) {
                                estadisticasId = null;
                            }
                        }
                    }
                }

                if (!personaExiste) {
                    throw new SQLException("Persona con ID " + persona.getId() + " no existe");
                }

                // 2. Actualizamos las estadísticas o creamos nuevas
                if (estadisticasId != null) {
                    actualizarEstadisticas(con, estadisticasId, persona.getEstadisticas());
                } else {
                    estadisticasId = insertarEstadisticas(con, persona.getEstadisticas());
                }

                // 3. Actualizamos la persona
                actualizarPersona(con, persona, estadisticasId);

                // 4. Manejamos relaciones muchos a muchos
                // Primero borramos todas las relaciones existentes
                eliminarRelacionesPersona(con, persona.getId());

                // Luego insertamos las nuevas relaciones
                if (persona.getArmas() != null && !persona.getArmas().isEmpty()) {
                    insertarArmasPersona(con, persona.getId(), persona.getArmas());
                }

                if (persona.getArmaduras() != null && !persona.getArmaduras().isEmpty()) {
                    insertarArmadurasPersona(con, persona.getId(), persona.getArmaduras());
                }

                if (persona.getHerramientas() != null && !persona.getHerramientas().isEmpty()) {
                    insertarHerramientasPersona(con, persona.getId(), persona.getHerramientas());
                }

                if (persona.getArcanas() != null && !persona.getArcanas().isEmpty()) {
                    insertarArcanasPersona(con, persona.getId(), persona.getArcanas());
                }

                con.commit();
                return "Persona actualizada correctamente";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Elimina una persona por su ID
     */
    public String eliminarPersona(Integer id) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);

            try {
                // 1. Obtenemos el ID de estadísticas
                Integer estadisticasId = null;
                try (PreparedStatement ps = con.prepareStatement("SELECT EstadisticasID FROM persona WHERE ID = ?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            estadisticasId = rs.getInt("EstadisticasID");
                            if (rs.wasNull()) {
                                estadisticasId = null;
                            }
                        }
                    }
                }

                // 2. Eliminamos todas las relaciones
                eliminarRelacionesPersona(con, id);

                // 3. Eliminamos la persona
                int rowsAffected = 0;
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM persona WHERE ID = ?")) {
                    ps.setInt(1, id);
                    rowsAffected = ps.executeUpdate();
                }

                // 4. Si existe, eliminamos las estadísticas
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
     * Lista todas las personas
     */
    public List<Persona> listarPersonas() throws SQLException {
        List<Persona> lista = new ArrayList<>();

        String sql = "SELECT p.*, r.nombre as raza_nombre, i.nombre as imperio_nombre " +
                "FROM persona p " +
                "LEFT JOIN raza r ON p.RazaID = r.ID " +
                "LEFT JOIN imperio i ON p.ImperioID = i.ID";

        try (Connection con = DriverManager.getConnection(url);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Persona persona = mapRowToPersonaBasica(rs);
                cargarRelacionesPersona(con, persona);
                lista.add(persona);
            }
        }

        return lista;
    }

    /**
     * Obtiene una persona específica por su ID
     */
    public Persona obtenerPersonaPorId(Integer id) throws SQLException {
        Persona persona = null;

        String sql = "SELECT p.*, r.nombre as raza_nombre, i.nombre as imperio_nombre " +
                "FROM persona p " +
                "LEFT JOIN raza r ON p.RazaID = r.ID " +
                "LEFT JOIN imperio i ON p.ImperioID = i.ID " +
                "WHERE p.ID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    persona = mapRowToPersonaBasica(rs);
                    cargarRelacionesPersona(con, persona);
                }
            }
        }

        return persona;
    }

    /**
     * Filtra personas según varios criterios opcionales
     */
    public List<Persona> filtrarPersonas(Integer razaId, Integer imperioId, String profesion, Integer nivelMin)
            throws SQLException {
        List<Persona> lista = new ArrayList<>();

        StringBuilder query = new StringBuilder(
                "SELECT p.*, r.nombre as raza_nombre, i.nombre as imperio_nombre " +
                        "FROM persona p " +
                        "LEFT JOIN raza r ON p.RazaID = r.ID " +
                        "LEFT JOIN imperio i ON p.ImperioID = i.ID " +
                        "LEFT JOIN estadisticas e ON p.EstadisticasID = e.ID " +
                        "WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (razaId != null) {
            query.append(" AND p.RazaID = ?");
            params.add(razaId);
        }

        if (imperioId != null) {
            query.append(" AND p.ImperioID = ?");
            params.add(imperioId);
        }

        if (profesion != null && !profesion.isEmpty()) {
            query.append(" AND p.Profesion = ?");
            params.add(profesion);
        }

        if (nivelMin != null) {
            query.append(" AND e.LVL >= ?");
            params.add(nivelMin);
        }

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(query.toString())) {

            // Configurar parámetros dinámicamente
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Persona persona = mapRowToPersonaBasica(rs);
                    cargarRelacionesPersona(con, persona);
                    lista.add(persona);
                }
            }
        }

        return lista;
    }

    /**
     * Lista todas las armas disponibles
     */

    public List<Arma> listarArmas() throws SQLException {
        List<Arma> lista = new ArrayList<>();
        String sql = "SELECT a.*, i.nombre as imperio_nombre FROM arma a " +
                "LEFT JOIN imperio i ON a.OrigenImperioID = i.ID";

        try (Connection con = DriverManager.getConnection(url);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Arma arma = mapRowToArma(rs);
                lista.add(arma);
            }
        }
        return lista;
    }

    public List<Arma> listarArmasPersona(Integer personaId) throws SQLException {
        List<Arma> armas = new ArrayList<>();
        String sql = "SELECT a.*, i.nombre as imperio_nombre, pa.Equipada " +
                "FROM arma a " +
                "JOIN persona_arma pa ON a.ID = pa.ArmaID " +
                "LEFT JOIN imperio i ON a.OrigenImperioID = i.ID " +
                "WHERE pa.PersonaID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Arma arma = mapRowToArma(rs); //
                    arma.setEquipada(rs.getBoolean("Equipada"));
                    armas.add(arma);
                }
            }
        }
        return armas;
    }

    /**
     * Lista todas las armaduras disponibles
     */
    public List<Armadura> listarArmaduras() throws SQLException {
        List<Armadura> lista = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM armadura")) {
            while (rs.next()) {
                Armadura armadura = new Armadura();
                armadura.setId(rs.getInt("ID"));
                armadura.setNombre(rs.getString("Nombre"));
                armadura.setBufoEstadisticas(rs.getString("BufoEstadisticas"));
                armadura.setMaterial(rs.getString("Material"));
                armadura.setDescripcion(rs.getString("Descripcion"));
                armadura.setPeso(rs.getFloat("Peso"));
                armadura.setPvp(rs.getFloat("PVP"));
                armadura.setOrigen(rs.getString("Origen"));
                armadura.setFechaCreacion(rs.getDate("FechaCreacion"));
                lista.add(armadura);
            }
        }

        return lista;
    }

    /**
     * Lista las armaduras de una persona específica.
     * Este método es público y maneja su propia conexión.
     */
    public List<Armadura> listarArmadurasPersona(Integer personaId) throws SQLException {
        List<Armadura> armaduras = new ArrayList<>();
        String sql = "SELECT adm.*, i.nombre as imperio_nombre, pa.Equipada " +
                "FROM armadura adm " +
                "JOIN persona_armadura pa ON adm.ID = pa.ArmaduraID " +
                "LEFT JOIN imperio i ON adm.OrigenImperioID = i.ID " +
                "WHERE pa.PersonaID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Armadura armadura = mapRowToArmadura(rs);
                    armadura.setEquipada(rs.getBoolean("Equipada"));
                    armaduras.add(armadura);
                }
            }
        }
        return armaduras;
    }

    /**
     * Lista todas las herramientas disponibles
     */
    public List<Herramienta> listarHerramientas() throws SQLException {
        List<Herramienta> lista = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM herramienta")) {
            while (rs.next()) {
                Herramienta herramienta = new Herramienta();
                herramienta.setId(rs.getInt("ID"));
                herramienta.setNombre(rs.getString("Nombre"));
                herramienta.setBufoEstadisticas(rs.getString("BufoEstadisticas"));
                herramienta.setMaterial(rs.getString("Material"));
                herramienta.setDescripcion(rs.getString("Descripcion"));
                herramienta.setPeso(rs.getFloat("Peso"));
                herramienta.setPvp(rs.getFloat("PVP"));
                herramienta.setUso(rs.getString("Uso"));
                herramienta.setOrigen(rs.getString("Origen"));
                herramienta.setFechaCreacion(rs.getDate("FechaCreacion"));
                lista.add(herramienta);
            }
        }

        return lista;
    }

    /**
     * Lista las herramientas de una persona específica.
     * Este método es público y maneja su propia conexión.
     */
    public List<Herramienta> listarHerramientasPersona(Integer personaId) throws SQLException {
        List<Herramienta> herramientas = new ArrayList<>();
        String sql = "SELECT h.*, i.nombre as imperio_nombre, ph.Equipada " +
                "FROM herramienta h " +
                "JOIN persona_herramienta ph ON h.ID = ph.HerramientaID " +
                "LEFT JOIN imperio i ON h.OrigenImperioID = i.ID " +
                "WHERE ph.PersonaID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Herramienta herramienta = mapRowToHerramienta(rs);
                    herramienta.setEquipada(rs.getBoolean("Equipada"));
                    herramientas.add(herramienta);
                }
            }
        }
        return herramientas;
    }

    /**
     * Lista todas las arcanas disponibles
     */
    public List<Arcana> listarArcanas() throws SQLException {
        List<Arcana> lista = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM arcana")) {
            while (rs.next()) {
                Arcana arcana = new Arcana();
                arcana.setId(rs.getInt("ID"));
                arcana.setTipo(rs.getString("Tipo"));
                arcana.setMaestria(rs.getString("Maestria"));
                arcana.setDificultad(rs.getString("Dificultad"));
                arcana.setFecha(rs.getDate("Fecha"));
                lista.add(arcana);
            }
        }

        return lista;
    }

    /**
     * Lista las arcanas de una persona específica, incluyendo su maestría personal.
     */
    public List<Arcana> listarArcanasPersona(Integer personaId) throws SQLException {
        List<Arcana> arcanas = new ArrayList<>();

        String sql = "SELECT a.ID AS ArcanaID, a.Tipo AS ArcanaTipo, " +
                "a.Dificultad AS ArcanaDificultad, a.Fecha AS ArcanaFecha, " +
                "pa.Maestria AS PersonaMaestria " +
                "FROM arcana a " +
                "JOIN persona_arcana pa ON a.ID = pa.ArcanaID " +
                "WHERE pa.PersonaID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, personaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Arcana arcana = new Arcana();
                    arcana.setId(rs.getInt("ArcanaID"));
                    arcana.setTipo(rs.getString("ArcanaTipo"));
                    arcana.setDificultad(rs.getString("ArcanaDificultad"));
                    arcana.setFecha(rs.getDate("ArcanaFecha"));
                    arcana.setMaestria(rs.getString("PersonaMaestria"));

                    arcanas.add(arcana);
                }
            }
        }
        return arcanas;
    }

    /* ----- Métodos auxiliares para personas ----- */

    /**
     * Mapea una fila de ResultSet a un objeto Persona básico
     */
    private Persona mapRowToPersonaBasica(ResultSet rs) throws SQLException {
        Persona persona = new Persona();
        persona.setId(rs.getInt("ID"));
        persona.setNombre(rs.getString("Nombre"));

        String apellido = rs.getString("Apellido");
        if (!rs.wasNull()) {
            persona.setApellido(apellido);
        }

        float ancho = rs.getFloat("Ancho");
        if (!rs.wasNull()) {
            persona.setAncho(ancho);
        }

        float alto = rs.getFloat("Alto");
        if (!rs.wasNull()) {
            persona.setAlto(alto);
        }

        persona.setDescripcionFisica(rs.getString("DescripcionFisica"));

        float grasaCorporal = rs.getFloat("PorcentajeGrasaCorporal");
        if (!rs.wasNull()) {
            persona.setPorcentajeGrasaCorporal(grasaCorporal);
        }

        persona.setPersonalidad(rs.getString("Personalidad"));
        persona.setOro(rs.getInt("Oro"));
        persona.setFechaNacimiento(rs.getDate("FechaNacimiento"));
        persona.setProfesion(rs.getString("Profesion"));
        persona.setDireccion(rs.getString("Direccion"));

        // Relaciones one-to-many
        int razaId = rs.getInt("RazaID");
        if (!rs.wasNull()) {
            persona.setRazaId(razaId);
            // Si hay un join con raza, obtenemos el nombre
            String razaNombre = rs.getString("raza_nombre");
            if (razaNombre != null) {
                Raza raza = new Raza();
                raza.setId(razaId);
                raza.setNombre(razaNombre);
                persona.setRaza(raza);
            }
        }

        int imperioId = rs.getInt("ImperioID");
        if (!rs.wasNull()) {
            persona.setImperioId(imperioId);
            // Si hay un join con imperio, obtenemos el nombre
            String imperioNombre = rs.getString("imperio_nombre");
            if (imperioNombre != null) {
                Imperio imperio = new Imperio();
                imperio.setId(imperioId);
                imperio.setNombre(imperioNombre);
                persona.setImperio(imperio);
            }
        }

        int estadisticasId = rs.getInt("EstadisticasID");
        if (!rs.wasNull()) {
            persona.setEstadisticasId(estadisticasId);
        }

        return persona;
    }

    /**
     * Carga todas las relaciones de una persona
     */
    private void cargarRelacionesPersona(Connection con, Persona persona) throws SQLException {
        if (persona == null || persona.getId() == null) {
            return;
        }

        // Cargar estadísticas base
        if (persona.getEstadisticasId() != null) {
            persona.setEstadisticas(obtenerEstadisticasPorId(con, persona.getEstadisticasId()));
        }

        // Cargar equipamiento
        persona.setArmas(obtenerArmasDePersona(con, persona.getId()));
        persona.setArmaduras(obtenerArmadurasDePersona(con, persona.getId()));
        persona.setHerramientas(obtenerHerramientasDePersona(con, persona.getId()));
        persona.setArcanas(obtenerArcanasDePersona(con, persona.getId()));

        // Calcular estadísticas finales aplicando buffs del equipamiento
        if (persona.getEstadisticas() != null) {
            persona.setEstadisticas(calcularEstadisticasFinales(persona));
        }
    }

    /**
     * Calcula las estadísticas finales de una persona aplicando los buffs del
     * equipamiento
     */
    private Estadisticas calcularEstadisticasFinales(Persona persona) {
        if (persona.getEstadisticas() == null) {
            return null;
        }

        // Crear una copia de las estadísticas base
        Estadisticas estadisticasFinales = persona.getEstadisticas().copiar();

        // Aplicar buffs de armas equipadas
        if (persona.getArmas() != null) {
            for (Arma arma : persona.getArmas()) {
                if (arma.getEquipada() != null && arma.getEquipada() && arma.getBufoEstadisticas() != null) {
                    estadisticasFinales.aplicarBuff(arma.getBufoEstadisticas());
                }
            }
        }

        // Aplicar buffs de armaduras equipadas
        if (persona.getArmaduras() != null) {
            for (Armadura armadura : persona.getArmaduras()) {
                if (armadura.getEquipada() != null && armadura.getEquipada()
                        && armadura.getBufoEstadisticas() != null) {
                    estadisticasFinales.aplicarBuff(armadura.getBufoEstadisticas());
                }
            }
        }

        // Aplicar buffs de herramientas equipadas
        if (persona.getHerramientas() != null) {
            for (Herramienta herramienta : persona.getHerramientas()) {
                if (herramienta.getEquipada() != null && herramienta.getEquipada()
                        && herramienta.getBufoEstadisticas() != null) {
                    estadisticasFinales.aplicarBuff(herramienta.getBufoEstadisticas());
                }
            }
        }

        return estadisticasFinales;
    }

    /**
     * Obtiene las estadísticas por su ID
     */
    private Estadisticas obtenerEstadisticasPorId(Connection con, Integer id) throws SQLException {
        Estadisticas estadisticas = null;

        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM estadisticas WHERE ID = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    estadisticas = new Estadisticas();
                    estadisticas.setId(rs.getInt("ID"));
                    estadisticas.setTipo(rs.getString("Tipo"));
                    estadisticas.setAtk(rs.getInt("ATK"));
                    estadisticas.setDef(rs.getInt("DEF"));
                    estadisticas.setHp(rs.getInt("HP"));
                    estadisticas.setSpe(rs.getInt("SPE"));
                    estadisticas.setMat(rs.getInt("MAT"));
                    estadisticas.setMdf(rs.getInt("MDF"));
                    estadisticas.setXp(rs.getInt("XP"));
                    estadisticas.setLvl(rs.getInt("LVL"));
                }
            }
        }

        return estadisticas;
    }

    /**
     * Obtiene las armas equipadas por una persona
     */
    private List<Arma> obtenerArmasDePersona(Connection con, Integer personaId) throws SQLException {
        List<Arma> armas = new ArrayList<>();

        String sql = "SELECT a.*, pa.Equipada FROM arma a " +
                "JOIN persona_arma pa ON a.ID = pa.ArmaID " +
                "WHERE pa.PersonaID = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Arma arma = new Arma();
                    arma.setId(rs.getInt("ID"));
                    arma.setNombre(rs.getString("Nombre"));
                    arma.setBufoEstadisticas(rs.getString("BufoEstadisticas"));
                    arma.setMaterial(rs.getString("Material"));
                    arma.setDescripcion(rs.getString("Descripcion"));
                    arma.setPeso(rs.getFloat("Peso"));
                    arma.setPvp(rs.getFloat("PVP"));
                    arma.setOrigen(rs.getString("Origen"));
                    arma.setFechaCreacion(rs.getDate("FechaCreacion"));
                    arma.setEquipada(rs.getBoolean("Equipada"));
                    armas.add(arma);
                }
            }
        }

        return armas;
    }

    /**
     * Obtiene las armaduras equipadas por una persona
     */
    private List<Armadura> obtenerArmadurasDePersona(Connection con, Integer personaId) throws SQLException {
        List<Armadura> armaduras = new ArrayList<>();

        String sql = "SELECT a.*, pa.Equipada FROM armadura a " +
                "JOIN persona_armadura pa ON a.ID = pa.ArmaduraID " +
                "WHERE pa.PersonaID = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Armadura armadura = new Armadura();
                    armadura.setId(rs.getInt("ID"));
                    armadura.setNombre(rs.getString("Nombre"));
                    armadura.setBufoEstadisticas(rs.getString("BufoEstadisticas"));
                    armadura.setMaterial(rs.getString("Material"));
                    armadura.setDescripcion(rs.getString("Descripcion"));
                    armadura.setPeso(rs.getFloat("Peso"));
                    armadura.setPvp(rs.getFloat("PVP"));
                    armadura.setOrigen(rs.getString("Origen"));
                    armadura.setFechaCreacion(rs.getDate("FechaCreacion"));
                    armadura.setEquipada(rs.getBoolean("Equipada"));
                    armaduras.add(armadura);
                }
            }
        }

        return armaduras;
    }

    /**
     * Obtiene las herramientas equipadas por una persona
     */
    private List<Herramienta> obtenerHerramientasDePersona(Connection con, Integer personaId) throws SQLException {
        List<Herramienta> herramientas = new ArrayList<>();

        String sql = "SELECT h.*, ph.Equipada FROM herramienta h " +
                "JOIN persona_herramienta ph ON h.ID = ph.HerramientaID " +
                "WHERE ph.PersonaID = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Herramienta herramienta = new Herramienta();
                    herramienta.setId(rs.getInt("ID"));
                    herramienta.setNombre(rs.getString("Nombre"));
                    herramienta.setBufoEstadisticas(rs.getString("BufoEstadisticas"));
                    herramienta.setMaterial(rs.getString("Material"));
                    herramienta.setDescripcion(rs.getString("Descripcion"));
                    herramienta.setPeso(rs.getFloat("Peso"));
                    herramienta.setPvp(rs.getFloat("PVP"));
                    herramienta.setUso(rs.getString("Uso"));
                    herramienta.setOrigen(rs.getString("Origen"));
                    herramienta.setFechaCreacion(rs.getDate("FechaCreacion"));
                    herramienta.setEquipada(rs.getBoolean("Equipada"));
                    herramientas.add(herramienta);
                }
            }
        }

        return herramientas;
    }

    /**
     * Obtiene las arcanas dominadas por una persona
     */
    private List<Arcana> obtenerArcanasDePersona(Connection con, Integer personaId) throws SQLException {
        List<Arcana> arcanas = new ArrayList<>();

        // This query should be using "Maestria", not "NivelMaestria"
        String sql = "SELECT a.*, pa.Maestria FROM arcana a " +
                "JOIN persona_arcana pa ON a.ID = pa.ArcanaID " +
                "WHERE pa.PersonaID = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Arcana arcana = new Arcana();
                    arcana.setId(rs.getInt("ID"));
                    arcana.setTipo(rs.getString("Tipo"));
                    arcana.setDificultad(rs.getString("Dificultad"));
                    arcana.setFecha(rs.getDate("Fecha"));
                    // This should be getting "Maestria"
                    arcana.setMaestria(rs.getString("Maestria"));
                    arcanas.add(arcana);
                }
            }
        }

        return arcanas;
    }

    /**
     * Inserta estadísticas y devuelve el ID generado
     */
    private Integer insertarEstadisticas(Connection con, Estadisticas estadisticas) throws SQLException {
        if (estadisticas == null) {
            // Crear estadísticas por defecto
            estadisticas = new Estadisticas();
            estadisticas.setTipo("Personal");
            estadisticas.setAtk(10);
            estadisticas.setDef(10);
            estadisticas.setHp(10);
            estadisticas.setSpe(10);
            estadisticas.setMat(10);
            estadisticas.setMdf(10);
            estadisticas.setXp(0);
            estadisticas.setLvl(1);
        }

        String sql = "INSERT INTO estadisticas (Tipo, ATK, DEF, HP, SPE, MAT, MDF, XP, LVL) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, estadisticas.getTipo() != null ? estadisticas.getTipo() : "Personal");
            ps.setInt(2, estadisticas.getAtk() != null ? estadisticas.getAtk() : 10);
            ps.setInt(3, estadisticas.getDef() != null ? estadisticas.getDef() : 10);
            ps.setInt(4, estadisticas.getHp() != null ? estadisticas.getHp() : 10);
            ps.setInt(5, estadisticas.getSpe() != null ? estadisticas.getSpe() : 10);
            ps.setInt(6, estadisticas.getMat() != null ? estadisticas.getMat() : 10);
            ps.setInt(7, estadisticas.getMdf() != null ? estadisticas.getMdf() : 10);
            ps.setInt(8, estadisticas.getXp() != null ? estadisticas.getXp() : 0);
            ps.setInt(9, estadisticas.getLvl() != null ? estadisticas.getLvl() : 1);
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se generó ID para las estadísticas");
                }
            }
        }
    }

    /**
     * Actualiza estadísticas existentes
     */
    private void actualizarEstadisticas(Connection con, Integer id, Estadisticas estadisticas) throws SQLException {
        if (estadisticas == null) {
            return;
        }

        String sql = "UPDATE estadisticas SET Tipo = ?, ATK = ?, DEF = ?, HP = ?, SPE = ?, MAT = ?, MDF = ?, XP = ?, LVL = ? WHERE ID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estadisticas.getTipo() != null ? estadisticas.getTipo() : "Personal");
            ps.setInt(2, estadisticas.getAtk() != null ? estadisticas.getAtk() : 10);
            ps.setInt(3, estadisticas.getDef() != null ? estadisticas.getDef() : 10);
            ps.setInt(4, estadisticas.getHp() != null ? estadisticas.getHp() : 10);
            ps.setInt(5, estadisticas.getSpe() != null ? estadisticas.getSpe() : 10);
            ps.setInt(6, estadisticas.getMat() != null ? estadisticas.getMat() : 10);
            ps.setInt(7, estadisticas.getMdf() != null ? estadisticas.getMdf() : 10);
            ps.setInt(8, estadisticas.getXp() != null ? estadisticas.getXp() : 0);
            ps.setInt(9, estadisticas.getLvl() != null ? estadisticas.getLvl() : 1);
            ps.setInt(10, id);
            ps.executeUpdate();
        }
    }

    /**
     * Inserta una persona y devuelve el ID generado
     */
    private Integer insertarPersona(Connection con, Persona persona, Integer estadisticasId) throws SQLException {
        String sql = "INSERT INTO persona (Nombre, Apellido, Ancho, Alto, DescripcionFisica, PorcentajeGrasaCorporal, "
                +
                "Personalidad, Oro, FechaNacimiento, Profesion, Direccion, RazaID, ImperioID, EstadisticasID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, persona.getNombre());

            if (persona.getApellido() != null) {
                ps.setString(2, persona.getApellido());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }

            if (persona.getAncho() != null) {
                ps.setFloat(3, persona.getAncho());
            } else {
                ps.setNull(3, java.sql.Types.FLOAT);
            }

            if (persona.getAlto() != null) {
                ps.setFloat(4, persona.getAlto());
            } else {
                ps.setNull(4, java.sql.Types.FLOAT);
            }

            ps.setString(5, persona.getDescripcionFisica());

            if (persona.getPorcentajeGrasaCorporal() != null) {
                ps.setFloat(6, persona.getPorcentajeGrasaCorporal());
            } else {
                ps.setNull(6, java.sql.Types.FLOAT);
            }

            ps.setString(7, persona.getPersonalidad());
            ps.setInt(8, persona.getOro() != null ? persona.getOro() : 0);

            if (persona.getFechaNacimiento() != null) {
                ps.setDate(9, persona.getFechaNacimiento());
            } else {
                ps.setNull(9, java.sql.Types.DATE);
            }

            ps.setString(10, persona.getProfesion());
            ps.setString(11, persona.getDireccion());

            if (persona.getRazaId() != null) {
                ps.setInt(12, persona.getRazaId());
            } else {
                ps.setNull(12, java.sql.Types.INTEGER);
            }

            if (persona.getImperioId() != null) {
                ps.setInt(13, persona.getImperioId());
            } else {
                ps.setNull(13, java.sql.Types.INTEGER);
            }

            ps.setInt(14, estadisticasId);

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se generó ID para la persona");
                }
            }
        }
    }

    /**
     * Actualiza una persona existente
     */
    private void actualizarPersona(Connection con, Persona persona, Integer estadisticasId) throws SQLException {
        String sql = "UPDATE persona SET Nombre = ?, Apellido = ?, Ancho = ?, Alto = ?, DescripcionFisica = ?, " +
                "PorcentajeGrasaCorporal = ?, Personalidad = ?, Oro = ?, FechaNacimiento = ?, Profesion = ?, " +
                "Direccion = ?, RazaID = ?, ImperioID = ?, EstadisticasID = ? WHERE ID = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, persona.getNombre());

            if (persona.getApellido() != null) {
                ps.setString(2, persona.getApellido());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }

            if (persona.getAncho() != null) {
                ps.setFloat(3, persona.getAncho());
            } else {
                ps.setNull(3, java.sql.Types.FLOAT);
            }

            if (persona.getAlto() != null) {
                ps.setFloat(4, persona.getAlto());
            } else {
                ps.setNull(4, java.sql.Types.FLOAT);
            }

            ps.setString(5, persona.getDescripcionFisica());

            if (persona.getPorcentajeGrasaCorporal() != null) {
                ps.setFloat(6, persona.getPorcentajeGrasaCorporal());
            } else {
                ps.setNull(6, java.sql.Types.FLOAT);
            }

            ps.setString(7, persona.getPersonalidad());
            ps.setInt(8, persona.getOro() != null ? persona.getOro() : 0);

            if (persona.getFechaNacimiento() != null) {
                ps.setDate(9, persona.getFechaNacimiento());
            } else {
                ps.setNull(9, java.sql.Types.DATE);
            }

            ps.setString(10, persona.getProfesion());
            ps.setString(11, persona.getDireccion());

            if (persona.getRazaId() != null) {
                ps.setInt(12, persona.getRazaId());
            } else {
                ps.setNull(12, java.sql.Types.INTEGER);
            }

            if (persona.getImperioId() != null) {
                ps.setInt(13, persona.getImperioId());
            } else {
                ps.setNull(13, java.sql.Types.INTEGER);
            }

            ps.setInt(14, estadisticasId);
            ps.setInt(15, persona.getId());

            ps.executeUpdate();
        }
    }

    /**
     * Elimina todas las relaciones de una persona
     */
    private void eliminarRelacionesPersona(Connection con, Integer personaId) throws SQLException {
        // Eliminar relaciones con armas
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM persona_arma WHERE PersonaID = ?")) {
            ps.setInt(1, personaId);
            ps.executeUpdate();
        }

        // Eliminar relaciones con armaduras
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM persona_armadura WHERE PersonaID = ?")) {
            ps.setInt(1, personaId);
            ps.executeUpdate();
        }

        // Eliminar relaciones con herramientas
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM persona_herramienta WHERE PersonaID = ?")) {
            ps.setInt(1, personaId);
            ps.executeUpdate();
        }

        // Eliminar relaciones con arcanas
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM persona_arcana WHERE PersonaID = ?")) {
            ps.setInt(1, personaId);
            ps.executeUpdate();
        }
    }

    /**
     * Inserta relaciones entre una persona y sus armas
     */
    private void insertarArmasPersona(Connection con, Integer personaId, List<Arma> armas) throws SQLException {
        String sql = "INSERT INTO persona_arma (PersonaID, ArmaID, Equipada) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Arma arma : armas) {
                ps.setInt(1, personaId);
                ps.setInt(2, arma.getId());
                ps.setBoolean(3, arma.getEquipada() != null ? arma.getEquipada() : false);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Inserta relaciones entre una persona y sus armaduras
     */
    private void insertarArmadurasPersona(Connection con, Integer personaId, List<Armadura> armaduras)
            throws SQLException {
        String sql = "INSERT INTO persona_armadura (PersonaID, ArmaduraID, Equipada) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Armadura armadura : armaduras) {
                ps.setInt(1, personaId);
                ps.setInt(2, armadura.getId());
                ps.setBoolean(3, armadura.getEquipada() != null ? armadura.getEquipada() : false);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Inserta relaciones entre una persona y sus herramientas
     */
    private void insertarHerramientasPersona(Connection con, Integer personaId, List<Herramienta> herramientas)
            throws SQLException {
        String sql = "INSERT INTO persona_herramienta (PersonaID, HerramientaID, Equipada) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Herramienta herramienta : herramientas) {
                ps.setInt(1, personaId);
                ps.setInt(2, herramienta.getId());
                ps.setBoolean(3, herramienta.getEquipada() != null ? herramienta.getEquipada() : false);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // This is the completion of the insertarArcanasPersona method
    private void insertarArcanasPersona(Connection con, Integer personaId, List<Arcana> arcanas) throws SQLException {
        String sql = "INSERT INTO persona_arcana (PersonaID, ArcanaID, Maestria) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Arcana arcana : arcanas) {
                ps.setInt(1, personaId);
                ps.setInt(2, arcana.getId());
                ps.setString(3, arcana.getMaestria());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Equipa o desequipa un arma para una persona
     */
    public String equiparArma(Integer personaId, Integer armaId, Boolean equipada) throws SQLException {
        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE persona_arma SET Equipada = ? WHERE PersonaID = ? AND ArmaID = ?")) {
            ps.setBoolean(1, equipada);
            ps.setInt(2, personaId);
            ps.setInt(3, armaId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                return "No se encontró la relación entre la persona y el arma.";
            }
            return "Arma " + (equipada ? "equipada" : "desequipada") + " correctamente.";
        }
    }

    /**
     * Equipa o desequipa una armadura para una persona
     */
    public String equiparArmadura(Integer personaId, Integer armaduraId, Boolean equipada) throws SQLException {
        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE persona_armadura SET Equipada = ? WHERE PersonaID = ? AND ArmaduraID = ?")) {
            ps.setBoolean(1, equipada);
            ps.setInt(2, personaId);
            ps.setInt(3, armaduraId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                return "No se encontró la relación entre la persona y la armadura.";
            }
            return "Armadura " + (equipada ? "equipada" : "desequipada") + " correctamente.";
        }
    }

    /**
     * Equipa o desequipa una herramienta para una persona
     */
    public String equiparHerramienta(Integer personaId, Integer herramientaId, Boolean equipada) throws SQLException {
        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE persona_herramienta SET Equipada = ? WHERE PersonaID = ? AND HerramientaID = ?")) {
            ps.setBoolean(1, equipada);
            ps.setInt(2, personaId);
            ps.setInt(3, herramientaId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                return "No se encontró la relación entre la persona y la herramienta.";
            }
            return "Herramienta " + (equipada ? "equipada" : "desequipada") + " correctamente.";
        }
    }

    /**
     * Actualiza el nivel de maestría de una arcana para una persona
     */
    public String actualizarMaestriaArcana(Integer personaId, Integer arcanaId, String maestria) throws SQLException {
        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE persona_arcana SET Maestria = ? WHERE PersonaID = ? AND ArcanaID = ?")) {
            ps.setString(1, maestria);
            ps.setInt(2, personaId);
            ps.setInt(3, arcanaId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                return "No se encontró la relación entre la persona y la arcana.";
            }
            return "Nivel de maestría actualizado correctamente.";
        }
    }

    /**
     * Obtiene personas con habilidades arcanas específicas
     */
    public List<Persona> buscarPersonasPorArcana(String tipoArcana) throws SQLException {
        List<Persona> lista = new ArrayList<>();

        String sql = "SELECT p.* FROM persona p " +
                "JOIN persona_arcana pa ON p.ID = pa.PersonaID " +
                "JOIN arcana a ON pa.ArcanaID = a.ID " +
                "WHERE a.Tipo LIKE ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + tipoArcana + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Persona persona = mapRowToPersonaBasica(rs);
                    cargarRelacionesPersona(con, persona);
                    lista.add(persona);
                }
            }
        }

        return lista;
    }

    /**
     * Actualiza las estadísticas de una persona (al ganar experiencia, subir de
     * nivel, etc.)
     */
    public String actualizarEstadisticasPersona(Integer personaId, Estadisticas estadisticas) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            // Obtener el ID de estadísticas de la persona
            Integer estadisticasId = null;
            try (PreparedStatement ps = con.prepareStatement("SELECT EstadisticasID FROM persona WHERE ID = ?")) {
                ps.setInt(1, personaId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        estadisticasId = rs.getInt("EstadisticasID");
                    } else {
                        return "Persona no encontrada";
                    }
                }
            }

            // Actualizar estadísticas
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE estadisticas SET ATK = ?, DEF = ?, HP = ?, SPE = ?, MAT = ?, MDF = ?, XP = ?, LVL = ? WHERE ID = ?")) {
                ps.setInt(1, estadisticas.getAtk());
                ps.setInt(2, estadisticas.getDef());
                ps.setInt(3, estadisticas.getHp());
                ps.setInt(4, estadisticas.getSpe());
                ps.setInt(5, estadisticas.getMat());
                ps.setInt(6, estadisticas.getMdf());
                ps.setInt(7, estadisticas.getXp());
                ps.setInt(8, estadisticas.getLvl());
                ps.setInt(9, estadisticasId);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    return "No se pudieron actualizar las estadísticas";
                }
            }

            return "Estadísticas actualizadas correctamente";
        }
    }

    /**
     * Obtiene todas las personas de un imperio específico
     */
    public List<Persona> obtenerPersonasPorImperio(Integer imperioId) throws SQLException {
        List<Persona> lista = new ArrayList<>();

        String sql = "SELECT p.* FROM persona p WHERE p.ImperioID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, imperioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Persona persona = mapRowToPersonaBasica(rs);
                    cargarRelacionesPersona(con, persona);
                    lista.add(persona);
                }
            }
        }

        return lista;
    }

    /**
     * Obtiene todas las personas de una raza específica
     */
    public List<Persona> obtenerPersonasPorRaza(Integer razaId) throws SQLException {
        List<Persona> lista = new ArrayList<>();

        String sql = "SELECT p.* FROM persona p WHERE p.RazaID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, razaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Persona persona = mapRowToPersonaBasica(rs);
                    cargarRelacionesPersona(con, persona);
                    lista.add(persona);
                }
            }
        }

        return lista;
    }
    /* ----- Métodos para gestión de Armas ----- */

    /**
     * Busca armas que coincidan con el nombre proporcionado
     */
    public List<Arma> buscarArmas(String nombre) throws SQLException {
        List<Arma> lista = new ArrayList<>();

        String sql = "SELECT a.*, i.nombre as imperio_nombre FROM arma a " +
                "LEFT JOIN imperio i ON a.OrigenImperioID = i.ID " +
                "WHERE a.Nombre LIKE ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Arma arma = mapRowToArma(rs);
                    lista.add(arma);
                }
            }
        }

        return lista;
    }

    /**
     * Obtiene un arma específica por su ID
     */
    public Arma obtenerArmaPorId(Integer id) throws SQLException {
        Arma arma = null;

        String sql = "SELECT a.*, i.nombre as imperio_nombre FROM arma a " +
                "LEFT JOIN imperio i ON a.OrigenImperioID = i.ID " +
                "WHERE a.ID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    arma = mapRowToArma(rs);
                }
            }
        }

        return arma;
    }

    /**
     * Añade una nueva arma a la base de datos
     */
    public String aniadirArma(String nombre, String material, String descripcion, Float peso, Float pvp,
            Integer imperioOrigenId, Date fechaCreacion, Integer bufAtk, Integer bufDef,
            Integer bufHp, Integer bufSpe, Integer bufMat, Integer bufMdf) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);

            try {
                // Validar que el imperio existe
                if (imperioOrigenId != null && !imperioExiste(con, imperioOrigenId)) {
                    throw new SQLException("El imperio especificado como origen no existe");
                }

                // Crear las estadísticas de buff
                Estadisticas estadisticasBuf = new Estadisticas();
                estadisticasBuf.setTipo("BuffArma");
                estadisticasBuf.setAtk(bufAtk);
                estadisticasBuf.setDef(bufDef);
                estadisticasBuf.setHp(bufHp);
                estadisticasBuf.setSpe(bufSpe);
                estadisticasBuf.setMat(bufMat);
                estadisticasBuf.setMdf(bufMdf);
                estadisticasBuf.setXp(0);
                estadisticasBuf.setLvl(1);

                Integer estadisticasId = insertarEstadisticas(con, estadisticasBuf);

                // Convertir estadísticas a string
                String bufoString = formatearEstadisticas(bufAtk, bufDef, bufHp, bufSpe, bufMat, bufMdf);

                String sql = "INSERT INTO arma (Nombre, BufoEstadisticas, Material, Descripcion, Peso, PVP, OrigenImperioID, FechaCreacion) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, bufoString);
                    ps.setString(3, material);
                    ps.setString(4, descripcion);

                    if (peso != null) {
                        ps.setFloat(5, peso);
                    } else {
                        ps.setNull(5, java.sql.Types.FLOAT);
                    }

                    if (pvp != null) {
                        ps.setFloat(6, pvp);
                    } else {
                        ps.setNull(6, java.sql.Types.FLOAT);
                    }

                    if (imperioOrigenId != null) {
                        ps.setInt(7, imperioOrigenId);
                    } else {
                        ps.setNull(7, java.sql.Types.INTEGER);
                    }

                    ps.setDate(8, fechaCreacion);

                    ps.executeUpdate();
                }

                con.commit();
                return "Arma añadida correctamente";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Actualiza un arma existente
     */
    public String actualizarArma(Integer id, String nombre, String material, String descripcion, Float peso, Float pvp,
            Integer imperioOrigenId, Date fechaCreacion, Integer bufAtk, Integer bufDef,
            Integer bufHp, Integer bufSpe, Integer bufMat, Integer bufMdf) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);

            try {
                // Validar que el imperio existe
                if (imperioOrigenId != null && !imperioExiste(con, imperioOrigenId)) {
                    throw new SQLException("El imperio especificado como origen no existe");
                }

                // Convertir estadísticas a string
                String bufoString = formatearEstadisticas(bufAtk, bufDef, bufHp, bufSpe, bufMat, bufMdf);

                String sql = "UPDATE arma SET Nombre = ?, BufoEstadisticas = ?, Material = ?, Descripcion = ?, " +
                        "Peso = ?, PVP = ?, OrigenImperioID = ?, FechaCreacion = ? WHERE ID = ?";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, bufoString);
                    ps.setString(3, material);
                    ps.setString(4, descripcion);

                    if (peso != null) {
                        ps.setFloat(5, peso);
                    } else {
                        ps.setNull(5, java.sql.Types.FLOAT);
                    }

                    if (pvp != null) {
                        ps.setFloat(6, pvp);
                    } else {
                        ps.setNull(6, java.sql.Types.FLOAT);
                    }

                    if (imperioOrigenId != null) {
                        ps.setInt(7, imperioOrigenId);
                    } else {
                        ps.setNull(7, java.sql.Types.INTEGER);
                    }

                    ps.setDate(8, fechaCreacion);
                    ps.setInt(9, id);

                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Arma no encontrada con ID: " + id);
                    }
                }

                con.commit();
                return "Arma actualizada correctamente";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Elimina un arma por su ID
     */
    public String eliminarArma(Integer id) throws SQLException {
        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement("DELETE FROM arma WHERE ID = ?")) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Arma no encontrada con ID: " + id);
            }

            return rowsAffected + " arma eliminada";
        }
    }

    /**
     * Mapea una fila de ResultSet a un objeto Arma
     */
    private Arma mapRowToArma(ResultSet rs) throws SQLException {
        Arma arma = new Arma();
        arma.setId(rs.getInt("ID"));
        arma.setNombre(rs.getString("Nombre"));
        arma.setBufoEstadisticas(rs.getString("BufoEstadisticas"));
        arma.setMaterial(rs.getString("Material"));
        arma.setDescripcion(rs.getString("Descripcion"));

        float peso = rs.getFloat("Peso");
        if (!rs.wasNull()) {
            arma.setPeso(peso);
        }

        float pvp = rs.getFloat("PVP");
        if (!rs.wasNull()) {
            arma.setPvp(pvp);
        }

        String origen = rs.getString("imperio_nombre");
        if (origen != null) {
            arma.setOrigen(origen);
        }

        arma.setFechaCreacion(rs.getDate("FechaCreacion"));
        return arma;
    }

    /* ----- Métodos para gestión de Armaduras ----- */

    /**
     * Busca armaduras que coincidan con el nombre proporcionado
     */
    public List<Armadura> buscarArmaduras(String nombre) throws SQLException {
        List<Armadura> lista = new ArrayList<>();

        String sql = "SELECT a.*, i.nombre as imperio_nombre FROM armadura a " +
                "LEFT JOIN imperio i ON a.OrigenImperioID = i.ID " +
                "WHERE a.Nombre LIKE ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Armadura armadura = mapRowToArmadura(rs);
                    lista.add(armadura);
                }
            }
        }

        return lista;
    }

    /**
     * Obtiene una armadura específica por su ID
     */
    public Armadura obtenerArmaduraPorId(Integer id) throws SQLException {
        Armadura armadura = null;

        String sql = "SELECT a.*, i.nombre as imperio_nombre FROM armadura a " +
                "LEFT JOIN imperio i ON a.OrigenImperioID = i.ID " +
                "WHERE a.ID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    armadura = mapRowToArmadura(rs);
                }
            }
        }

        return armadura;
    }

    /**
     * Añade una nueva armadura a la base de datos
     */
    public String aniadirArmadura(String nombre, String material, String descripcion, Float peso, Float pvp,
            Integer imperioOrigenId, Date fechaCreacion, Integer bufAtk, Integer bufDef,
            Integer bufHp, Integer bufSpe, Integer bufMat, Integer bufMdf) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);

            try {
                // Validar que el imperio existe
                if (imperioOrigenId != null && !imperioExiste(con, imperioOrigenId)) {
                    throw new SQLException("El imperio especificado como origen no existe");
                }

                // Crear las estadísticas de buff
                Estadisticas estadisticasBuf = new Estadisticas();
                estadisticasBuf.setTipo("BuffArmadura");
                estadisticasBuf.setAtk(bufAtk);
                estadisticasBuf.setDef(bufDef);
                estadisticasBuf.setHp(bufHp);
                estadisticasBuf.setSpe(bufSpe);
                estadisticasBuf.setMat(bufMat);
                estadisticasBuf.setMdf(bufMdf);
                estadisticasBuf.setXp(0);
                estadisticasBuf.setLvl(1);

                Integer estadisticasId = insertarEstadisticas(con, estadisticasBuf);

                // Convertir estadísticas a string
                String bufoString = formatearEstadisticas(bufAtk, bufDef, bufHp, bufSpe, bufMat, bufMdf);

                String sql = "INSERT INTO armadura (Nombre, BufoEstadisticas, Material, Descripcion, Peso, PVP, OrigenImperioID, FechaCreacion) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, bufoString);
                    ps.setString(3, material);
                    ps.setString(4, descripcion);

                    if (peso != null) {
                        ps.setFloat(5, peso);
                    } else {
                        ps.setNull(5, java.sql.Types.FLOAT);
                    }

                    if (pvp != null) {
                        ps.setFloat(6, pvp);
                    } else {
                        ps.setNull(6, java.sql.Types.FLOAT);
                    }

                    if (imperioOrigenId != null) {
                        ps.setInt(7, imperioOrigenId);
                    } else {
                        ps.setNull(7, java.sql.Types.INTEGER);
                    }

                    ps.setDate(8, fechaCreacion);

                    ps.executeUpdate();
                }

                con.commit();
                return "Armadura añadida correctamente";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Actualiza una armadura existente
     */
    public String actualizarArmadura(Integer id, String nombre, String material, String descripcion, Float peso,
            Float pvp,
            Integer imperioOrigenId, Date fechaCreacion, Integer bufAtk, Integer bufDef,
            Integer bufHp, Integer bufSpe, Integer bufMat, Integer bufMdf) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);

            try {
                // Validar que el imperio existe
                if (imperioOrigenId != null && !imperioExiste(con, imperioOrigenId)) {
                    throw new SQLException("El imperio especificado como origen no existe");
                }

                // Convertir estadísticas a string
                String bufoString = formatearEstadisticas(bufAtk, bufDef, bufHp, bufSpe, bufMat, bufMdf);

                String sql = "UPDATE armadura SET Nombre = ?, BufoEstadisticas = ?, Material = ?, Descripcion = ?, " +
                        "Peso = ?, PVP = ?, OrigenImperioID = ?, FechaCreacion = ? WHERE ID = ?";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, bufoString);
                    ps.setString(3, material);
                    ps.setString(4, descripcion);

                    if (peso != null) {
                        ps.setFloat(5, peso);
                    } else {
                        ps.setNull(5, java.sql.Types.FLOAT);
                    }

                    if (pvp != null) {
                        ps.setFloat(6, pvp);
                    } else {
                        ps.setNull(6, java.sql.Types.FLOAT);
                    }

                    if (imperioOrigenId != null) {
                        ps.setInt(7, imperioOrigenId);
                    } else {
                        ps.setNull(7, java.sql.Types.INTEGER);
                    }

                    ps.setDate(8, fechaCreacion);
                    ps.setInt(9, id);

                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Armadura no encontrada con ID: " + id);
                    }
                }

                con.commit();
                return "Armadura actualizada correctamente";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Elimina una armadura por su ID
     */
    public String eliminarArmadura(Integer id) throws SQLException {
        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement("DELETE FROM armadura WHERE ID = ?")) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Armadura no encontrada con ID: " + id);
            }

            return rowsAffected + " armadura eliminada";
        }
    }

    /**
     * Mapea una fila de ResultSet a un objeto Armadura
     */
    private Armadura mapRowToArmadura(ResultSet rs) throws SQLException {
        Armadura armadura = new Armadura();
        armadura.setId(rs.getInt("ID"));
        armadura.setNombre(rs.getString("Nombre"));
        armadura.setBufoEstadisticas(rs.getString("BufoEstadisticas"));
        armadura.setMaterial(rs.getString("Material"));
        armadura.setDescripcion(rs.getString("Descripcion"));

        float peso = rs.getFloat("Peso");
        if (!rs.wasNull()) {
            armadura.setPeso(peso);
        }

        float pvp = rs.getFloat("PVP");
        if (!rs.wasNull()) {
            armadura.setPvp(pvp);
        }

        String origen = rs.getString("imperio_nombre");
        if (origen != null) {
            armadura.setOrigen(origen);
        }

        armadura.setFechaCreacion(rs.getDate("FechaCreacion"));
        return armadura;
    }

    /* ----- Métodos para gestión de Herramientas ----- */

    /**
     * Busca herramientas que coincidan con el nombre proporcionado
     */
    public List<Herramienta> buscarHerramientas(String nombre) throws SQLException {
        List<Herramienta> lista = new ArrayList<>();

        String sql = "SELECT h.*, i.nombre as imperio_nombre FROM herramienta h " +
                "LEFT JOIN imperio i ON h.OrigenImperioID = i.ID " +
                "WHERE h.Nombre LIKE ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Herramienta herramienta = mapRowToHerramienta(rs);
                    lista.add(herramienta);
                }
            }
        }

        return lista;
    }

    /**
     * Obtiene una herramienta específica por su ID
     */
    public Herramienta obtenerHerramientaPorId(Integer id) throws SQLException {
        Herramienta herramienta = null;

        String sql = "SELECT h.*, i.nombre as imperio_nombre FROM herramienta h " +
                "LEFT JOIN imperio i ON h.OrigenImperioID = i.ID " +
                "WHERE h.ID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    herramienta = mapRowToHerramienta(rs);
                }
            }
        }

        return herramienta;
    }

    /**
     * Añade una nueva herramienta a la base de datos
     */
    public String aniadirHerramienta(String nombre, String material, String descripcion, String uso, Float peso,
            Float pvp,
            Integer imperioOrigenId, Date fechaCreacion, Integer bufAtk, Integer bufDef,
            Integer bufHp, Integer bufSpe, Integer bufMat, Integer bufMdf) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);

            try {
                // Validar que el imperio existe
                if (imperioOrigenId != null && !imperioExiste(con, imperioOrigenId)) {
                    throw new SQLException("El imperio especificado como origen no existe");
                }

                // Crear las estadísticas de buff
                Estadisticas estadisticasBuf = new Estadisticas();
                estadisticasBuf.setTipo("BuffHerramienta");
                estadisticasBuf.setAtk(bufAtk);
                estadisticasBuf.setDef(bufDef);
                estadisticasBuf.setHp(bufHp);
                estadisticasBuf.setSpe(bufSpe);
                estadisticasBuf.setMat(bufMat);
                estadisticasBuf.setMdf(bufMdf);
                estadisticasBuf.setXp(0);
                estadisticasBuf.setLvl(1);

                Integer estadisticasId = insertarEstadisticas(con, estadisticasBuf);

                // Convertir estadísticas a string
                String bufoString = formatearEstadisticas(bufAtk, bufDef, bufHp, bufSpe, bufMat, bufMdf);

                String sql = "INSERT INTO herramienta (Nombre, BufoEstadisticas, Material, Descripcion, Peso, PVP, Uso, OrigenImperioID, FechaCreacion) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, bufoString);
                    ps.setString(3, material);
                    ps.setString(4, descripcion);

                    if (peso != null) {
                        ps.setFloat(5, peso);
                    } else {
                        ps.setNull(5, java.sql.Types.FLOAT);
                    }

                    if (pvp != null) {
                        ps.setFloat(6, pvp);
                    } else {
                        ps.setNull(6, java.sql.Types.FLOAT);
                    }

                    ps.setString(7, uso);

                    if (imperioOrigenId != null) {
                        ps.setInt(8, imperioOrigenId);
                    } else {
                        ps.setNull(8, java.sql.Types.INTEGER);
                    }

                    ps.setDate(9, fechaCreacion);

                    ps.executeUpdate();
                }

                con.commit();
                return "Herramienta añadida correctamente";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Actualiza una herramienta existente
     */
    public String actualizarHerramienta(Integer id, String nombre, String material, String descripcion, String uso,
            Float peso, Float pvp,
            Integer imperioOrigenId, Date fechaCreacion, Integer bufAtk, Integer bufDef,
            Integer bufHp, Integer bufSpe, Integer bufMat, Integer bufMdf) throws SQLException {
        try (Connection con = DriverManager.getConnection(url)) {
            con.setAutoCommit(false);

            try {
                // Validar que el imperio existe
                if (imperioOrigenId != null && !imperioExiste(con, imperioOrigenId)) {
                    throw new SQLException("El imperio especificado como origen no existe");
                }

                // Convertir estadísticas a string
                String bufoString = formatearEstadisticas(bufAtk, bufDef, bufHp, bufSpe, bufMat, bufMdf);

                String sql = "UPDATE herramienta SET Nombre = ?, BufoEstadisticas = ?, Material = ?, Descripcion = ?, "
                        +
                        "Peso = ?, PVP = ?, Uso = ?, OrigenImperioID = ?, FechaCreacion = ? WHERE ID = ?";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, bufoString);
                    ps.setString(3, material);
                    ps.setString(4, descripcion);

                    if (peso != null) {
                        ps.setFloat(5, peso);
                    } else {
                        ps.setNull(5, java.sql.Types.FLOAT);
                    }

                    if (pvp != null) {
                        ps.setFloat(6, pvp);
                    } else {
                        ps.setNull(6, java.sql.Types.FLOAT);
                    }

                    ps.setString(7, uso);

                    if (imperioOrigenId != null) {
                        ps.setInt(8, imperioOrigenId);
                    } else {
                        ps.setNull(8, java.sql.Types.INTEGER);
                    }

                    ps.setDate(9, fechaCreacion);
                    ps.setInt(10, id);

                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Herramienta no encontrada con ID: " + id);
                    }
                }

                con.commit();
                return "Herramienta actualizada correctamente";
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /**
     * Elimina una herramienta por su ID
     */
    public String eliminarHerramienta(Integer id) throws SQLException {
        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement("DELETE FROM herramienta WHERE ID = ?")) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Herramienta no encontrada con ID: " + id);
            }

            return rowsAffected + " herramienta eliminada";
        }
    }

    /**
     * Mapea una fila de ResultSet a un objeto Herramienta
     */
    private Herramienta mapRowToHerramienta(ResultSet rs) throws SQLException {
        Herramienta herramienta = new Herramienta();
        herramienta.setId(rs.getInt("ID"));
        herramienta.setNombre(rs.getString("Nombre"));
        herramienta.setBufoEstadisticas(rs.getString("BufoEstadisticas"));
        herramienta.setMaterial(rs.getString("Material"));
        herramienta.setDescripcion(rs.getString("Descripcion"));

        float peso = rs.getFloat("Peso");
        if (!rs.wasNull()) {
            herramienta.setPeso(peso);
        }

        float pvp = rs.getFloat("PVP");
        if (!rs.wasNull()) {
            herramienta.setPvp(pvp);
        }

        herramienta.setUso(rs.getString("Uso"));

        String origen = rs.getString("imperio_nombre");
        if (origen != null) {
            herramienta.setOrigen(origen);
        }

        herramienta.setFechaCreacion(rs.getDate("FechaCreacion"));
        return herramienta;
    }

    /* ----- Métodos para gestión de Arcanas ----- */

    /**
     * Busca arcanas que coincidan con el tipo proporcionado
     */
    public List<Arcana> buscarArcanas(String tipo) throws SQLException {
        List<Arcana> lista = new ArrayList<>();

        String sql = "SELECT * FROM arcana WHERE Tipo LIKE ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + tipo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Arcana arcana = mapRowToArcana(rs);
                    lista.add(arcana);
                }
            }
        }

        return lista;
    }

    /**
     * Obtiene una arcana específica por su ID
     */
    public Arcana obtenerArcanaPorId(Integer id) throws SQLException {
        Arcana arcana = null;

        String sql = "SELECT * FROM arcana WHERE ID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    arcana = mapRowToArcana(rs);
                }
            }
        }

        return arcana;
    }

    /**
     * Añade una nueva arcana a la base de datos
     */
    public String aniadirArcana(String tipo, String maestria, String dificultad, Date fecha) throws SQLException {
        String sql = "INSERT INTO arcana (Tipo, Maestria, Dificultad, Fecha) VALUES (?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, maestria);
            ps.setString(3, dificultad);
            ps.setDate(4, fecha);

            ps.executeUpdate();
            return "Arcana añadida correctamente";
        }
    }

    /**
     * Actualiza una arcana existente
     */
    public String actualizarArcana(Integer id, String tipo, String maestria, String dificultad, Date fecha)
            throws SQLException {
        String sql = "UPDATE arcana SET Tipo = ?, Maestria = ?, Dificultad = ?, Fecha = ? WHERE ID = ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, maestria);
            ps.setString(3, dificultad);
            ps.setDate(4, fecha);
            ps.setInt(5, id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Arcana no encontrada con ID: " + id);
            }

            return "Arcana actualizada correctamente";
        }
    }

    /**
     * Elimina una arcana por su ID
     */
    public String eliminarArcana(Integer id) throws SQLException {
        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement("DELETE FROM arcana WHERE ID = ?")) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Arcana no encontrada con ID: " + id);
            }

            return rowsAffected + " arcana eliminada";
        }
    }

    /**
     * Mapea una fila de ResultSet a un objeto Arcana
     */
    private Arcana mapRowToArcana(ResultSet rs) throws SQLException {
        Arcana arcana = new Arcana();
        arcana.setId(rs.getInt("ID"));
        arcana.setTipo(rs.getString("Tipo"));
        arcana.setMaestria(rs.getString("Maestria"));
        arcana.setDificultad(rs.getString("Dificultad"));
        arcana.setFecha(rs.getDate("Fecha"));
        return arcana;
    }

    /* ----- Métodos auxiliares ----- */

    /**
     * Verifica si un imperio existe
     */
    private boolean imperioExiste(Connection con, Integer imperioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM imperio WHERE ID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, imperioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Formatea las estadísticas como un string legible
     */
    private String formatearEstadisticas(Integer atk, Integer def, Integer hp, Integer spe, Integer mat, Integer mdf) {
        StringBuilder sb = new StringBuilder();
        if (atk != null && atk != 0)
            sb.append(atk > 0 ? "+" : "").append(atk).append(" ATK, ");
        if (def != null && def != 0)
            sb.append(def > 0 ? "+" : "").append(def).append(" DEF, ");
        if (hp != null && hp != 0)
            sb.append(hp > 0 ? "+" : "").append(hp).append(" HP, ");
        if (spe != null && spe != 0)
            sb.append(spe > 0 ? "+" : "").append(spe).append(" SPE, ");
        if (mat != null && mat != 0)
            sb.append(mat > 0 ? "+" : "").append(mat).append(" MAT, ");
        if (mdf != null && mdf != 0)
            sb.append(mdf > 0 ? "+" : "").append(mdf).append(" MDF, ");

        if (sb.length() > 0) {
            // Eliminar la última coma y espacio
            return sb.substring(0, sb.length() - 2);
        }
        return "Sin modificadores";
    }

    // TABLA MAESTRA... (wow)

    public Map<String, Object> obtenerEstadisticasGlobales() throws SQLException {
        Map<String, Object> estadisticas = new HashMap<>();
        String sqlPlanetas = "SELECT COUNT(*) FROM planeta";
        String sqlContinentes = "SELECT COUNT(*) FROM continente";
        String sqlImperios = "SELECT COUNT(*) FROM imperio";
        String sqlRazas = "SELECT COUNT(*) FROM raza";
        String sqlGuerrasActivas = "SELECT COUNT(*) FROM guerra WHERE FechaFin IS NULL";
        String sqlPoblacionTotal = "SELECT SUM(Poblacion) FROM imperio WHERE Poblacion IS NOT NULL";

        try (Connection con = DriverManager.getConnection(url); Statement st = con.createStatement()) {

            try (ResultSet rs = st.executeQuery(sqlPlanetas)) {
                if (rs.next())
                    estadisticas.put("totalPlanetas", rs.getInt(1));
            }
            try (ResultSet rs = st.executeQuery(sqlContinentes)) {
                if (rs.next())
                    estadisticas.put("totalContinentes", rs.getInt(1));
            }
            try (ResultSet rs = st.executeQuery(sqlImperios)) {
                if (rs.next())
                    estadisticas.put("totalImperios", rs.getInt(1));
            }
            try (ResultSet rs = st.executeQuery(sqlRazas)) {
                if (rs.next())
                    estadisticas.put("totalRazas", rs.getInt(1));
            }
            try (ResultSet rs = st.executeQuery(sqlGuerrasActivas)) {
                if (rs.next())
                    estadisticas.put("guerrasActivas", rs.getInt(1));
            }
            try (ResultSet rs = st.executeQuery(sqlPoblacionTotal)) {
                if (rs.next())
                    estadisticas.put("poblacionTotalImperios", rs.getLong(1));
            }
        }
        return estadisticas;
    }

    /**
     * Obtiene los N planetas más recientes.
     */
    public List<Planeta> getPlanetasRecientes(int limite) throws SQLException {
        List<Planeta> lista = new ArrayList<>();

        String sql = "SELECT * FROM planeta ORDER BY FechaCreacion DESC, ID DESC LIMIT ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limite); // Establece el parámetro LIMITE

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Planeta planeta = new Planeta();

                    // Mapea cada columna del ResultSet a las propiedades del objeto Planeta

                    planeta.setId(rs.getInt("ID"));
                    planeta.setNombre(rs.getString("Nombre"));
                    planeta.setUbicacion(rs.getString("Ubicacion"));
                    planeta.setHabitable(rs.getBoolean("Habitable"));
                    // Para campos Float/Double/Integer que pueden ser NULL en la BD:
                    float nivelAgua = rs.getFloat("NivelAgua");
                    if (!rs.wasNull()) {
                        planeta.setNivelAgua(nivelAgua);
                    } else {
                        planeta.setNivelAgua(null);
                    }

                    planeta.setFechaCreacion(rs.getDate("FechaCreacion"));

                    float tamanio = rs.getFloat("Tamanio");
                    if (!rs.wasNull()) {
                        planeta.setTamanio(tamanio);
                    } else {
                        planeta.setTamanio(null);
                    }

                    float densidad = rs.getFloat("Densidad");
                    if (!rs.wasNull()) {
                        planeta.setDensidad(densidad);
                    } else {
                        planeta.setDensidad(null);
                    }

                    planeta.setDescripcion(rs.getString("Descripcion"));

                    lista.add(planeta);
                }
            }

        }
        return lista;
    }

    /**
     * Obtiene los N imperios más recientes.
     * Ordena por FechaCreacion descendente, y luego por ID descendente.
     *
     * 
     * @param limite El número máximo de imperios recientes a devolver.
     * @return Una lista de objetos Imperio.
     */
    public List<Imperio> getImperiosRecientes(int limite) throws SQLException {
        List<Imperio> lista = new ArrayList<>();
        String sql = "SELECT * FROM imperio ORDER BY FechaCreacion DESC, ID DESC LIMIT ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // mapRowToImperio debería
                    // manejar la lectura de todas las columnas de "SELECT *".
                    Imperio imperio = mapRowToImperio(rs);
                    lista.add(imperio);
                }
            }
        }
        return lista;
    }

    /**
     * Para este ejemplo, obtendremos las más recientes por ID descendente.
     * Incluye el nombre de la raza y el nivel de las estadísticas.
     *
     * @param limite El número máximo de personas recientes a devolver.
     * @return Una lista de objetos Persona.
     */
    public List<Persona> getPersonasRecientes(int limite) throws SQLException {
        List<Persona> lista = new ArrayList<>();

        String sql = "SELECT p.ID, p.Nombre, p.Apellido, p.Profesion, " +
                "r.Nombre as razaNombre, " +
                "s.LVL as nivelEstadisticas " +
                "FROM persona p " +
                "LEFT JOIN raza r ON p.RazaID = r.ID " +
                "LEFT JOIN estadisticas s ON p.EstadisticasID = s.ID " +
                "ORDER BY p.ID DESC LIMIT ?";

        try (Connection con = DriverManager.getConnection(url);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Persona persona = new Persona();
                    persona.setId(rs.getInt("ID"));
                    persona.setNombre(rs.getString("Nombre"));

                    // Apellido puede ser null
                    String apellido = rs.getString("Apellido");
                    if (!rs.wasNull()) {
                        persona.setApellido(apellido);
                    }

                    persona.setProfesion(rs.getString("Profesion"));

                    // Crear y setear el objeto Raza
                    Raza raza = new Raza();
                    String nombreRaza = rs.getString("razaNombre");
                    if (nombreRaza != null) {
                        raza.setNombre(nombreRaza);
                    } else {
                        raza.setNombre("Desconocida");
                    }
                    persona.setRaza(raza);

                    // Crear y setear el objeto Estadisticas
                    Estadisticas estadisticas = new Estadisticas();
                    int nivel = rs.getInt("nivelEstadisticas");
                    if (!rs.wasNull()) { // El LEFT JOIN puede resultar en NULL si no hay estadísticas
                        estadisticas.setLvl(nivel);
                    } else {
                        estadisticas.setLvl(1); // Valor por defecto si no hay estadísticas o nivel
                    }
                    persona.setEstadisticas(estadisticas);

                    lista.add(persona);
                }
            }
        }
        return lista;
    }

    /**
     * Obtiene las N guerras iniciadas más recientemente, incluyendo sus imperios
     * participantes.
     *
     * @param limite El número máximo de guerras recientes a devolver.
     * @return Una lista de objetos Guerra, cada uno con su lista de
     *         imperiosParticipantes poblada.
     * 
     */
    public List<Guerra> getGuerrasRecientes(int limite) throws SQLException {
        List<Guerra> guerrasRecientes = new ArrayList<>();

        // Obtiene las N guerras más recientes (solo los datos de la tabla 'guerra')
        String sqlGuerras = "SELECT * FROM guerra ORDER BY FechaInicio DESC, ID DESC LIMIT ?";

        try (Connection con = DriverManager.getConnection(url)) { // Una sola conexión para todas las operaciones
            try (PreparedStatement psGuerras = con.prepareStatement(sqlGuerras)) {
                psGuerras.setInt(1, limite);

                try (ResultSet rsGuerras = psGuerras.executeQuery()) {
                    while (rsGuerras.next()) {
                        Guerra guerra = new Guerra(); // El constructor inicializa imperiosParticipantes a new
                                                      // ArrayList<>()

                        guerra.setId(rsGuerras.getInt("ID"));
                        guerra.setNombre(rsGuerras.getString("Nombre"));

                        Date fechaInicio = rsGuerras.getDate("FechaInicio");
                        if (!rsGuerras.wasNull()) {
                            guerra.setFechaInicio(fechaInicio);
                        }

                        Date fechaFin = rsGuerras.getDate("FechaFin");
                        if (!rsGuerras.wasNull()) {
                            guerra.setFechaFin(fechaFin);
                        } else {
                            guerra.setFechaFin(null);
                        }

                        guerra.setDescripcion(rsGuerras.getString("Descripcion"));

                        // para esta guerra, obtener sus imperios participantes
                        List<ParticipanteGuerra> participantes = obtenerParticipantesDeGuerra(con, guerra.getId());
                        guerra.setImperiosParticipantes(participantes);

                        guerrasRecientes.add(guerra);
                    }
                }
            }
        } // La conexión se cierra aquí
        return guerrasRecientes;
    }

    /**
     * Método auxiliar para obtener los participantes de una guerra específica.
     * Este método reutiliza la conexión pasada como argumento.
     *
     * @param con      La conexión de base de datos activa.
     * @param guerraId El ID de la guerra para la cual obtener los participantes.
     * @return Una lista de objetos ParticipanteGuerra.
     */
    private List<ParticipanteGuerra> obtenerParticipantesDeGuerra(Connection con, Integer guerraId)
            throws SQLException {
        List<ParticipanteGuerra> participantes = new ArrayList<>();
        String sqlParticipantes = "SELECT ig.Rol, ig.Ganador, i.ID as ImperioID, i.Nombre as ImperioNombre " +
                "FROM imperio_guerra ig " +
                "JOIN imperio i ON ig.ImperioID = i.ID " +
                "WHERE ig.GuerraID = ?";

        try (PreparedStatement psParticipantes = con.prepareStatement(sqlParticipantes)) {
            psParticipantes.setInt(1, guerraId);
            try (ResultSet rsParticipantes = psParticipantes.executeQuery()) {
                while (rsParticipantes.next()) {
                    ParticipanteGuerra participante = new ParticipanteGuerra();
                    participante.setImperioId(rsParticipantes.getInt("ImperioID"));
                    participante.setImperioNombre(rsParticipantes.getString("ImperioNombre"));
                    participante.setRol(rsParticipantes.getString("Rol"));

                    // Manejar el booleano 'Ganador' que puede ser NULL
                    boolean ganador = rsParticipantes.getBoolean("Ganador");
                    if (!rsParticipantes.wasNull()) {
                        participante.setGanador(ganador);
                    } else {
                        participante.setGanador(null); // Explícitamente null si la columna es NULL
                    }
                    participantes.add(participante);
                }
            }
        }
        return participantes;
    }

    // Clase auxiliar interna estática para representar una fila de la Tabla Maestra

    public static class FilaTablaMaestra {
        private Integer id;
        private String tipoEntidad;
        private String nombrePrincipal;
        private Date fechaRelevante;
        private String infoClave1;
        private String infoClave2;
        private String urlDetalle; // URL para ver detalles (ej. "planet.html#1")

        public FilaTablaMaestra() {
        }

        public FilaTablaMaestra(Integer id, String tipoEntidad, String nombrePrincipal, Date fechaRelevante,
                String infoClave1, String infoClave2, String urlDetalle) {
            this.id = id;
            this.tipoEntidad = tipoEntidad;
            this.nombrePrincipal = nombrePrincipal;
            this.fechaRelevante = fechaRelevante;
            this.infoClave1 = infoClave1;
            this.infoClave2 = infoClave2;
            this.urlDetalle = urlDetalle;
        }

        // Getters
        public Integer getId() {
            return id;
        }

        public String getTipoEntidad() {
            return tipoEntidad;
        }

        public String getNombrePrincipal() {
            return nombrePrincipal;
        }

        public Date getFechaRelevante() {
            return fechaRelevante;
        }

        public String getInfoClave1() {
            return infoClave1;
        }

        public String getInfoClave2() {
            return infoClave2;
        }

        public String getUrlDetalle() {
            return urlDetalle;
        }

        // Setters
        public void setId(Integer id) {
            this.id = id;
        }

        public void setTipoEntidad(String tipoEntidad) {
            this.tipoEntidad = tipoEntidad;
        }

        public void setNombrePrincipal(String nombrePrincipal) {
            this.nombrePrincipal = nombrePrincipal;
        }

        public void setFechaRelevante(Date fechaRelevante) {
            this.fechaRelevante = fechaRelevante;
        }

        public void setInfoClave1(String infoClave1) {
            this.infoClave1 = infoClave1;
        }

        public void setInfoClave2(String infoClave2) {
            this.infoClave2 = infoClave2;
        }

        public void setUrlDetalle(String urlDetalle) {
            this.urlDetalle = urlDetalle;
        }
    }

    /**
     * Obtiene datos consolidados de varias entidades para una tabla maestra.
     * Inicialmente, solo carga planetas.
     *
     * @param limitePorEntidad El número máximo de ítems a cargar por cada tipo de
     *                         entidad.
     * @return Una lista de objetos FilaTablaMaestra.
     */
    public List<FilaTablaMaestra> getDatosTablaMaestra(int limitePorEntidad) throws SQLException {
        List<FilaTablaMaestra> datosCompletos = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(url)) {

            // ---- Sección para PLANETAS ----
            // Seleccionamos las columnas que necesitamos para FilaTablaMaestra
            String sqlPlanetas = "SELECT ID, Nombre, FechaCreacion, Habitable, Ubicacion " +
                    "FROM planeta " +
                    "ORDER BY FechaCreacion DESC, ID DESC LIMIT ?"; // Ordenar para obtener los más recientes

            try (PreparedStatement psPlanetas = con.prepareStatement(sqlPlanetas)) {
                psPlanetas.setInt(1, limitePorEntidad);
                try (ResultSet rsPlanetas = psPlanetas.executeQuery()) {
                    while (rsPlanetas.next()) {
                        Integer planetaId = rsPlanetas.getInt("ID");
                        String nombrePlaneta = rsPlanetas.getString("Nombre");
                        Date fechaCreacionPlaneta = rsPlanetas.getDate("FechaCreacion");
                        boolean esHabitable = rsPlanetas.getBoolean("Habitable");
                        String ubicacionPlaneta = rsPlanetas.getString("Ubicacion");

                        datosCompletos.add(new FilaTablaMaestra(
                                planetaId,
                                "Planeta", // tipoEntidad
                                nombrePlaneta, // nombrePrincipal
                                fechaCreacionPlaneta, // fechaRelevante
                                esHabitable ? "Habitable" : "No Habitable", // infoClave1
                                ubicacionPlaneta != null ? "Ubicación: " + ubicacionPlaneta : "Ubicación: N/A", // infoClave2
                                "planet.html#" + planetaId // urlDetalle (el frontend construirá el enlace completo)
                        ));
                    }
                }
            }
            // ---- Sección para IMPERIOS ----
            String sqlImperios = "SELECT ID, Nombre, FechaCreacion, Lider, Poblacion " +
                    "FROM imperio " +
                    "ORDER BY FechaCreacion DESC, ID DESC LIMIT ?";
            try (PreparedStatement psImperios = con.prepareStatement(sqlImperios)) {
                psImperios.setInt(1, limitePorEntidad);
                try (ResultSet rsImperios = psImperios.executeQuery()) {
                    while (rsImperios.next()) {
                        Integer imperioId = rsImperios.getInt("ID");
                        int poblacion = rsImperios.getInt("Poblacion"); // getInt devuelve 0 si es NULL, wasNull para
                                                                        // chequear
                        String poblacionStr = rsImperios.wasNull() ? "Población: N/A" : "Población: " + poblacion;

                        datosCompletos.add(new FilaTablaMaestra(
                                imperioId,
                                "Imperio",
                                rsImperios.getString("Nombre"),
                                rsImperios.getDate("FechaCreacion"),
                                "Líder: " + (rsImperios.getString("Lider") != null ? rsImperios.getString("Lider")
                                        : "N/A"),
                                poblacionStr,
                                "empire.html#" + imperioId));
                    }
                }
            }

            // ---- Sección para GUERRAS ----
            String sqlGuerras = "SELECT ID, Nombre, FechaInicio, FechaFin " +
                    "FROM guerra " +
                    "ORDER BY FechaInicio DESC, ID DESC LIMIT ?";
            try (PreparedStatement psGuerras = con.prepareStatement(sqlGuerras)) {
                psGuerras.setInt(1, limitePorEntidad);
                try (ResultSet rsGuerras = psGuerras.executeQuery()) {
                    while (rsGuerras.next()) {
                        Integer guerraId = rsGuerras.getInt("ID");
                        Date fechaFin = rsGuerras.getDate("FechaFin");
                        boolean activa = rsGuerras.wasNull() || fechaFin == null; // Considera NULL como activa

                        datosCompletos.add(new FilaTablaMaestra(
                                guerraId,
                                "Guerra",
                                rsGuerras.getString("Nombre"),
                                rsGuerras.getDate("FechaInicio"),
                                activa ? "Activa" : "Finalizada",
                                "",
                                "war.html#" + guerraId));
                    }
                }
            }

            // ---- Sección para PERSONAS ----
            String sqlPersonas = "SELECT p.ID, p.Nombre, p.Apellido, p.FechaNacimiento, r.Nombre as razaNombre, s.LVL as nivel "
                    +
                    "FROM persona p " +
                    "LEFT JOIN raza r ON p.RazaID = r.ID " +
                    "LEFT JOIN estadisticas s ON p.EstadisticasID = s.ID " +
                    "ORDER BY p.ID DESC LIMIT ?";
            try (PreparedStatement psPersonas = con.prepareStatement(sqlPersonas)) {
                psPersonas.setInt(1, limitePorEntidad);
                try (ResultSet rsPersonas = psPersonas.executeQuery()) {
                    while (rsPersonas.next()) {
                        Integer personaId = rsPersonas.getInt("ID");
                        String nombreCompleto = rsPersonas.getString("Nombre") +
                                (rsPersonas.getString("Apellido") != null ? " " + rsPersonas.getString("Apellido")
                                        : "");
                        String razaNombre = rsPersonas.getString("razaNombre");
                        int nivel = rsPersonas.getInt("nivel");
                        boolean nivelWasNull = rsPersonas.wasNull();

                        datosCompletos.add(new FilaTablaMaestra(
                                personaId,
                                "Persona",
                                nombreCompleto,
                                rsPersonas.getDate("FechaNacimiento"),
                                "Raza: " + (razaNombre != null ? razaNombre : "N/A"),
                                "Nivel: " + (nivelWasNull ? "N/A" : nivel),
                                "person.html#" + personaId));
                    }
                }
            }
            // ---- NUEVA Sección para CONTINENTES ----
            // Asumimos que Continente no tiene una fecha de creación propia, usaremos ID.
            // Hacemos JOIN con Planeta para mostrar a qué planeta pertenece.
            String sqlContinentes = "SELECT c.ID, c.Nombre, c.Clima, p.Nombre as PlanetaNombre " +
                    "FROM continente c " +
                    "LEFT JOIN planeta p ON c.PlanetaID = p.ID " +
                    "ORDER BY c.ID DESC LIMIT ?";
            try (PreparedStatement psContinentes = con.prepareStatement(sqlContinentes)) {
                psContinentes.setInt(1, limitePorEntidad);
                try (ResultSet rsContinentes = psContinentes.executeQuery()) {
                    while (rsContinentes.next()) {
                        Integer continenteId = rsContinentes.getInt("ID");
                        datosCompletos.add(new FilaTablaMaestra(
                                continenteId,
                                "Continente",
                                rsContinentes.getString("Nombre"),
                                null, // Sin fecha relevante específica para continente en este resumen
                                "Clima: " + (rsContinentes.getString("Clima") != null ? rsContinentes.getString("Clima")
                                        : "N/A"),
                                "Planeta: " + (rsContinentes.getString("PlanetaNombre") != null
                                        ? rsContinentes.getString("PlanetaNombre")
                                        : "N/A"),
                                "continent.html#" + continenteId));
                    }
                }
            }

            // ---- Sección para BIOMAS ----

            String sqlBiomas = "SELECT b.ID, b.Nombre, b.Clima, c.Nombre as ContinenteNombre " +
                    "FROM bioma b " +
                    "LEFT JOIN continente c ON b.ContinenteID = c.ID " +
                    "ORDER BY b.ID DESC LIMIT ?";
            try (PreparedStatement psBiomas = con.prepareStatement(sqlBiomas)) {
                psBiomas.setInt(1, limitePorEntidad);
                try (ResultSet rsBiomas = psBiomas.executeQuery()) {
                    while (rsBiomas.next()) {
                        Integer biomaId = rsBiomas.getInt("ID");
                        datosCompletos.add(new FilaTablaMaestra(
                                biomaId,
                                "Bioma",
                                rsBiomas.getString("Nombre"),
                                null, // Sin fecha relevante específica
                                "Clima: " + (rsBiomas.getString("Clima") != null ? rsBiomas.getString("Clima") : "N/A"),
                                "Continente: " + (rsBiomas.getString("ContinenteNombre") != null
                                        ? rsBiomas.getString("ContinenteNombre")
                                        : "N/A"),
                                "biome.html#" + biomaId));
                    }
                }
            }

            // ---- Sección para ARMAS ----

            String sqlArmas = "SELECT ID, Nombre, FechaCreacion, Material, PVP FROM arma ORDER BY FechaCreacion DESC, ID DESC LIMIT ?";
            try (PreparedStatement psArmas = con.prepareStatement(sqlArmas)) {
                psArmas.setInt(1, limitePorEntidad);
                try (ResultSet rsArmas = psArmas.executeQuery()) {
                    while (rsArmas.next()) {
                        Integer armaId = rsArmas.getInt("ID");
                        float pvp = rsArmas.getFloat("PVP");
                        String pvpStr = rsArmas.wasNull() ? "PVP: N/A" : "PVP: " + pvp + " oro";
                        datosCompletos.add(new FilaTablaMaestra(
                                armaId, "Arma", rsArmas.getString("Nombre"), rsArmas.getDate("FechaCreacion"),
                                "Material: " + (rsArmas.getString("Material") != null ? rsArmas.getString("Material")
                                        : "N/A"),
                                pvpStr, "equipment.html#armas"));
                    }
                }
            }

            // ---- Sección para ARMADURAS ----
            String sqlArmaduras = "SELECT ID, Nombre, FechaCreacion, Material, PVP FROM armadura ORDER BY FechaCreacion DESC, ID DESC LIMIT ?";
            try (PreparedStatement psArmaduras = con.prepareStatement(sqlArmaduras)) {
                psArmaduras.setInt(1, limitePorEntidad);
                try (ResultSet rsArmaduras = psArmaduras.executeQuery()) {
                    while (rsArmaduras.next()) {
                        Integer armaduraId = rsArmaduras.getInt("ID");
                        float pvp = rsArmaduras.getFloat("PVP");
                        String pvpStr = rsArmaduras.wasNull() ? "PVP: N/A" : "PVP: " + pvp + " oro";
                        datosCompletos.add(new FilaTablaMaestra(
                                armaduraId, "Armadura", rsArmaduras.getString("Nombre"),
                                rsArmaduras.getDate("FechaCreacion"),
                                "Material: "
                                        + (rsArmaduras.getString("Material") != null ? rsArmaduras.getString("Material")
                                                : "N/A"),
                                pvpStr, "equipment.html#armaduras"));
                    }
                }
            }

            // ---- Sección para HERRAMIENTAS ----
            String sqlHerramientas = "SELECT ID, Nombre, FechaCreacion, Uso, Material FROM herramienta ORDER BY FechaCreacion DESC, ID DESC LIMIT ?";
            try (PreparedStatement psHerramientas = con.prepareStatement(sqlHerramientas)) {
                psHerramientas.setInt(1, limitePorEntidad);
                try (ResultSet rsHerramientas = psHerramientas.executeQuery()) {
                    while (rsHerramientas.next()) {
                        Integer herramientaId = rsHerramientas.getInt("ID");
                        datosCompletos.add(new FilaTablaMaestra(
                                herramientaId, "Herramienta", rsHerramientas.getString("Nombre"),
                                rsHerramientas.getDate("FechaCreacion"),
                                "Uso: " + (rsHerramientas.getString("Uso") != null ? rsHerramientas.getString("Uso")
                                        : "N/A"),
                                "Material: " + (rsHerramientas.getString("Material") != null
                                        ? rsHerramientas.getString("Material")
                                        : "N/A"),
                                "equipment.html#herramientas"));
                    }
                }
            }

            // ---- Sección para ARCANAS ----

            String sqlArcanas = "SELECT ID, Tipo, Fecha, Maestria, Dificultad FROM arcana ORDER BY Fecha DESC, ID DESC LIMIT ?";
            try (PreparedStatement psArcanas = con.prepareStatement(sqlArcanas)) {
                psArcanas.setInt(1, limitePorEntidad);
                try (ResultSet rsArcanas = psArcanas.executeQuery()) {
                    while (rsArcanas.next()) {
                        Integer arcanaId = rsArcanas.getInt("ID");
                        datosCompletos.add(new FilaTablaMaestra(
                                arcanaId, "Arcana", rsArcanas.getString("Tipo"), rsArcanas.getDate("Fecha"),
                                "Maestría: "
                                        + (rsArcanas.getString("Maestria") != null ? rsArcanas.getString("Maestria")
                                                : "N/A"),
                                "Dificultad: "
                                        + (rsArcanas.getString("Dificultad") != null ? rsArcanas.getString("Dificultad")
                                                : "N/A"),
                                "equipment.html#arcanas"));
                    }
                }
            }

        } // La conexión se cierra aquí
        return datosCompletos;
    }
}
