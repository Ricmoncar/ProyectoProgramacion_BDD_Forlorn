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

@Service
public class Servicio {
	
	static final String url = "jdbc:mysql://localhost:3306/mundo_fantasia?user=usuario&password=usuario";	
	
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<Planeta> buscarPlanetas(String nombre) throws SQLException {
		List<Planeta> lista = new ArrayList<>();
		Connection con = DriverManager.getConnection(url);
		PreparedStatement ps = con.prepareStatement("SELECT * FROM planeta WHERE nombre LIKE ?");
		ps.setString(1, "%" + nombre + "%");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Planeta planeta = new Planeta();
			planeta.setId(rs.getInt("id"));
			planeta.setNombre(rs.getString("nombre"));
			planeta.setUbicacion(rs.getString("ubicacion"));
			planeta.setHabitable(rs.getBoolean("habitable"));
			planeta.setNivelAgua(rs.getFloat("nivelAgua"));
			planeta.setFechaCreacion(rs.getDate("fechacreacion"));
			planeta.setTamanio(rs.getFloat("tamanio"));
			planeta.setDensidad(rs.getFloat("densidad"));
			planeta.setDescripcion(rs.getString("descripcion"));
			lista.add(planeta);
		}
		rs.close();
		ps.close();
		con.close();
		return lista;
	}

	public String aniadirPlaneta(String nombre, String ubicacion, Boolean habitable, Float nivelAgua, Date fechaCreacion, Float tamanio, Float densidad, String descripcion) throws SQLException {
		Connection con = DriverManager.getConnection(url);
		PreparedStatement ps = con.prepareStatement("INSERT INTO planeta (nombre, ubicacion, habitable, nivelagua, fechacreacion, tamanio, densidad, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		ps.setString(1, nombre);
		ps.setString(2, ubicacion);
		ps.setBoolean(3, habitable);
		ps.setFloat(4, nivelAgua);
		ps.setDate(5, fechaCreacion);
		ps.setFloat(6, tamanio);
		ps.setFloat(7, densidad);
		ps.setString(8, descripcion);
		int rowsAffected = ps.executeUpdate();
		ps.close();
		con.close();
		return rowsAffected + " filas afectadas";
	}

	public String eliminarPlaneta(Integer id) throws SQLException {
		Connection con = DriverManager.getConnection(url);
		PreparedStatement ps = con.prepareStatement("DELETE FROM planeta WHERE id = ?");
		ps.setInt(1, id);
		int rowsAffected = ps.executeUpdate();
		ps.close();
		con.close();
		return rowsAffected + " filas afectadas";
	}

	public String actualizarPlaneta(Integer id, String nombre, String ubicacion, Boolean habitable, Float nivelAgua, Date fechaCreacion, Float tamanio, Float densidad, String descripcion) throws SQLException {
		Connection con = DriverManager.getConnection(url);
		PreparedStatement ps = con.prepareStatement("UPDATE planeta SET nombre = ?, ubicacion = ?, habitable = ?, nivelagua = ?, fechacreacion = ?, tamanio = ?, densidad = ?, descripcion = ? WHERE id = ?");
		ps.setString(1, nombre);
		ps.setString(2, ubicacion);
		ps.setBoolean(3, habitable);
		ps.setFloat(4, nivelAgua);
		ps.setDate(5, fechaCreacion);
		ps.setFloat(6, tamanio);
		ps.setFloat(7, densidad);
		ps.setString(8, descripcion);
		ps.setInt(9, id);
		int rowsAffected = ps.executeUpdate();
		ps.close();
		con.close();
		return rowsAffected + " filas afectadas";
	}

	public List<Planeta> listarPlanetas() throws SQLException {
		List<Planeta> lista = new ArrayList<>();
		Connection con = DriverManager.getConnection(url);
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM planeta");
		while (rs.next()) {
			Planeta planeta = new Planeta();
			planeta.setId(rs.getInt("id"));
			planeta.setNombre(rs.getString("nombre"));
			planeta.setUbicacion(rs.getString("ubicacion"));
			planeta.setHabitable(rs.getBoolean("habitable"));
			planeta.setNivelAgua(rs.getFloat("nivelagua"));
			planeta.setFechaCreacion(rs.getDate("fechacreacion"));
			planeta.setTamanio(rs.getFloat("tamanio"));
			planeta.setDensidad(rs.getFloat("densidad"));
			planeta.setDescripcion(rs.getString("descripcion"));
			lista.add(planeta);
		}
		rs.close();
		st.close();
		con.close();
		return lista;
	}

}