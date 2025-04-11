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

import com.daw.controllers.Planeta;
import com.daw.controllers.Continente;

@Service
public class Servicio {

	// Consider externalizing connection details to application.properties
	static final String url = "jdbc:mysql://localhost:3306/mundo_fantasia?user=usuario&password=usuario";

	// Static block for driver loading (generally okay, but consider alternatives in modern Spring)
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// Consider throwing a RuntimeException or using a better logging framework
			e.printStackTrace();
            throw new RuntimeException("MySQL JDBC Driver not found!", e);
		}
	}

    // --- Planeta Methods (Assumed Correct based on previous context) ---
	public List<Planeta> buscarPlanetas(String nombre) throws SQLException {
		List<Planeta> lista = new ArrayList<>();
		// Try-with-resources for better resource management
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
                    planeta.setNivelAgua(rs.getFloat("nivelAgua")); // Ensure DB column name is correct
                    planeta.setFechaCreacion(rs.getDate("FechaCreacion")); // Ensure DB column name is correct
                    planeta.setTamanio(rs.getFloat("Tamanio")); // Ensure DB column name is correct
                    planeta.setDensidad(rs.getFloat("densidad"));
                    planeta.setDescripcion(rs.getString("descripcion"));
                    lista.add(planeta);
                }
            }
        } // Resources are automatically closed here
		return lista;
	}

	public String aniadirPlaneta(String nombre, String ubicacion, Boolean habitable, Float nivelAgua, Date fechaCreacion, Float tamanio, Float densidad, String descripcion) throws SQLException {
        int rowsAffected = 0;
         try (Connection con = DriverManager.getConnection(url);
              PreparedStatement ps = con.prepareStatement("INSERT INTO planeta (nombre, ubicacion, habitable, nivelAgua, FechaCreacion, Tamanio, densidad, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) { // Check case for DB cols
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

	public String eliminarPlaneta(Integer id) throws SQLException {
		int rowsAffected = 0;
        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement("DELETE FROM planeta WHERE id = ?")) {
            ps.setInt(1, id);
            rowsAffected = ps.executeUpdate();
        }
		return rowsAffected + " filas afectadas";
	}

	public String actualizarPlaneta(Integer id, String nombre, String ubicacion, Boolean habitable, Float nivelAgua, Date fechaCreacion, Float tamanio, Float densidad, String descripcion) throws SQLException {
        int rowsAffected = 0;
		 try (Connection con = DriverManager.getConnection(url);
              PreparedStatement ps = con.prepareStatement("UPDATE planeta SET nombre = ?, ubicacion = ?, habitable = ?, nivelAgua = ?, FechaCreacion = ?, Tamanio = ?, densidad = ?, descripcion = ? WHERE id = ?")) { // Check case for DB cols
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
                planeta.setNivelAgua(rs.getFloat("nivelAgua")); // Check DB col name
                planeta.setFechaCreacion(rs.getDate("FechaCreacion")); // Check DB col name
                planeta.setTamanio(rs.getFloat("Tamanio")); // Check DB col name
                planeta.setDensidad(rs.getFloat("densidad"));
                planeta.setDescripcion(rs.getString("descripcion"));
                lista.add(planeta);
            }
        }
		return lista;
	}

	// --- Métodos para Continentes CORREGIDOS ---

	public List<Continente> buscarContinentes(String nombre) throws SQLException {
		List<Continente> lista = new ArrayList<>();
		// *** FIX: Use PlanetaID in JOIN and SELECT ***
        // *** FIX: Use correct case for other columns if needed ***
		String sql = "SELECT c.*, p.nombre as planetaNombre FROM continente c JOIN planeta p ON c.PlanetaID = p.id WHERE c.nombre LIKE ?";
		try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
             try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Continente continente = mapRowToContinente(rs); // Use helper method
                    lista.add(continente);
                }
            }
        }
		return lista;
	}

	public String aniadirContinente(String nombre, Integer planetaId, String hemisferio, String clima, Float tamanio, Boolean habitable, String descripcion) throws SQLException {
        int rowsAffected = 0;
		// *** FIX: Use PlanetaID in INSERT ***
        // *** FIX: Use correct case for other columns if needed (e.g., Tamanio) ***
		String sql = "INSERT INTO continente (nombre, PlanetaID, hemisferio, clima, Tamanio, habitable, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, planetaId);
            ps.setString(3, hemisferio);
            ps.setString(4, clima);
             // Handle potential null tamanio
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

	public String eliminarContinente(Integer id) throws SQLException {
        int rowsAffected = 0;
		try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement("DELETE FROM continente WHERE id = ?")) {
            ps.setInt(1, id);
            rowsAffected = ps.executeUpdate();
        }
		return rowsAffected + " filas afectadas";
	}

	public String actualizarContinente(Integer id, String nombre, Integer planetaId, String hemisferio, String clima, Float tamanio, Boolean habitable, String descripcion) throws SQLException {
		int rowsAffected = 0;
        String sql = "UPDATE continente SET nombre = ?, PlanetaID = ?, hemisferio = ?, clima = ?, Tamanio = ?, habitable = ?, descripcion = ? WHERE id = ?";
		try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, planetaId);
            ps.setString(3, hemisferio);
            ps.setString(4, clima);
             // Handle potential null tamanio
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

	public List<Continente> listarContinentes() throws SQLException {
		List<Continente> lista = new ArrayList<>();
		// *** FIX: Use PlanetaID in JOIN ***
		String sql = "SELECT c.*, p.nombre as planetaNombre FROM continente c JOIN planeta p ON c.PlanetaID = p.id";
		try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                 Continente continente = mapRowToContinente(rs); // Use helper method
                 lista.add(continente);
            }
        }
		return lista;
	}

	public Continente obtenerContinentePorId(Integer id) throws SQLException {
		Continente continente = null;
        // *** FIX: Use PlanetaID in JOIN ***
        String sql = "SELECT c.*, p.nombre as planetaNombre FROM continente c JOIN planeta p ON c.PlanetaID = p.id WHERE c.id = ?";
		 try (Connection con = DriverManager.getConnection(url);
              PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
             try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    continente = mapRowToContinente(rs); // Use helper method
                }
            }
        }
		return continente;
	}

	public List<Continente> filtrarContinentes(Integer planetaId, String hemisferio, String clima, Boolean habitable) throws SQLException {
		List<Continente> lista = new ArrayList<>();
		// *** FIX: Use PlanetaID in JOIN and WHERE ***
        StringBuilder query = new StringBuilder("SELECT c.*, p.nombre as planetaNombre FROM continente c JOIN planeta p ON c.PlanetaID = p.id WHERE 1=1");

		List<Object> params = new ArrayList<>();

		if (planetaId != null) {
			query.append(" AND c.PlanetaID = ?"); // *** FIX ***
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

            // Set parameters dynamically
            for (int i = 0; i < params.size(); i++) {
                 ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                     Continente continente = mapRowToContinente(rs); // Use helper method
                     lista.add(continente);
                }
            }
        }
		return lista;
	}

    // Helper method to map ResultSet row to Continente object (reduces code duplication)
    private Continente mapRowToContinente(ResultSet rs) throws SQLException {
        Continente continente = new Continente();
        continente.setId(rs.getInt("id"));
        continente.setNombre(rs.getString("nombre"));
        // *** FIX: Read PlanetaID ***
        continente.setPlanetaId(rs.getInt("PlanetaID"));
        // This comes from the JOIN alias, so it's okay
        continente.setPlanetaNombre(rs.getString("planetaNombre"));
        continente.setHemisferio(rs.getString("hemisferio"));
        continente.setClima(rs.getString("clima"));
         // *** FIX: Use correct case for column name if needed ***
        continente.setTamanio(rs.getFloat("Tamanio")); // Check DB column case
        continente.setHabitable(rs.getBoolean("habitable"));
        continente.setDescripcion(rs.getString("descripcion"));
        return continente;
    }
}