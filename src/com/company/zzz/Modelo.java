package com.company.zzz;

import com.company.base.Pokemon;
import com.company.base.Pokemon.Tipo;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static com.company.Constantes.*;

public class Modelo {

    // Todo: ponerlo en un fichero properties
    private final String RUTA_PROPERTIES = "Config.PROPERTIES";
    private Connection conexion;


    public void conectar() throws ClassNotFoundException, SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(RUTA_PROPERTIES));

        String ip = properties.getProperty("DB.IP");
        String nombreBase = properties.getProperty("DB.NAME");
        String usuario = properties.getProperty("DB.USER");
        String contrasena = properties.getProperty("DB.PASS");

        Class.forName("com.mysql.cj.jdbc.Driver");
        conexion = DriverManager.getConnection(
                "jdbc:mysql://" + ip + ":3306/" + nombreBase, usuario, contrasena);
    }

    public boolean iniciarSesion(String usuario, String contrasena) throws SQLException {
        String sql = "select id from usuarios where usuario = ? " +
                "AND contrasena = SHA1(?);";

        // Parametrizar la consulta para evitar inyeccion de codigo
        PreparedStatement sentencia = conexion.prepareStatement(sql);
        sentencia.setString(1, usuario);
        sentencia.setString(2, contrasena);

        ResultSet resultado = sentencia.executeQuery();

        boolean encontrado = resultado.next();
        resultado.close();
        return encontrado;
    }

    public void desconectar() throws SQLException {
        conexion.close();
        conexion = null;
    }


    private HashMap<String, Pokemon> pokemones;

    public Modelo() {
    }

    public void guardarPokemon(Pokemon pokemon) throws SQLException {
        String sql = String.format("insert into %s(%s, %s, %s, %s, %s, %s) values(?, ?, ?, ?, ?, ?);",
                POKEMONES, ID, NOMBRE, TIPO, NIVEL, PESO, IMAGEN);
        PreparedStatement sentencia = conexion.prepareStatement(sql);

        // Guardar el pokemon
        sentencia.setInt(1, pokemon.getId());
        sentencia.setString(2, pokemon.getNombre());
        sentencia.setString(3, pokemon.getTipo().toString());
        sentencia.setInt(4, pokemon.getNivel());
        sentencia.setFloat(5, pokemon.getPeso());
        sentencia.setString(6, pokemon.getNombreImagen());
        sentencia.executeUpdate();
    }

    public void eliminarPokemon(Pokemon pokemon) throws SQLException {
        String sql = String.format("delete from %s where %s = %d", POKEMONES, ID, pokemon.getId());
        conexion.prepareStatement(sql).executeUpdate();
    }

    public List<Pokemon> getPokemones() throws SQLException {
        String sql = "select * from " + POKEMONES;
        PreparedStatement sentencia = conexion.prepareStatement(sql);
        ResultSet resultado = sentencia.executeQuery();

        ArrayList<Pokemon> pokemones = new ArrayList<>();
        while (resultado.next()) {
            int id = resultado.getInt(ID);
            String nombre = resultado.getString(NOMBRE);
            Tipo tipo = Tipo.valueOf(resultado.getString(TIPO));
            int nivel = resultado.getInt(NIVEL);
            float peso = resultado.getFloat(PESO);
            String nombreImagen = resultado.getString(IMAGEN);

            pokemones.add(new Pokemon(id, nombre, tipo, nivel, peso, nombreImagen));
        }
        return  pokemones;
    }
}
