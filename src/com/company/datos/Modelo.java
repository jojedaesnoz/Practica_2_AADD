package com.company.datos;

import com.company.base.Pelicula;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import static com.company.util.Constantes.*;

public class Modelo {
    private Connection conexion;
    private final String RUTA_PROPERTIES = "src" + File.separator
            + "res" + File.separator + "config.properties";

    public Modelo() {
    }

    public void conectar() throws ClassNotFoundException, IOException, SQLException {
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
        String sql = "select id from usuarios where usuario = ? and contrasena = SHA1(?);" ;
        PreparedStatement sentencia = conexion.prepareStatement(sql);

        sentencia.setString(1, usuario);
        sentencia.setString(2, contrasena);

        ResultSet resultado = sentencia.executeQuery();
        boolean encontrado = resultado.next();
        resultado.close();
        return encontrado;
    }

    public File getDefaultImage() {
        return new File("");
    }


    public void guardarPelicula(Pelicula nueva) throws SQLException, FileNotFoundException {
        String sql = "insert into " + TABLA_PELICULAS + " (" +
                TITULO + ", " + SINOPSIS + ", " + VALORACION + ", " +
                RECAUDACION + ", " + IMAGEN + ") values (?, ?, ?, ?, ?);";
        PreparedStatement sentencia = conexion.prepareStatement(sql);
        sentencia.setString(1, nueva.getTitulo());
        sentencia.setString(2, nueva.getSinopsis());
        sentencia.setInt(3, nueva.getValoracion());
        sentencia.setFloat(4, nueva.getRecaudacion());
        sentencia.setBlob(5, new FileInputStream(nueva.getImagen()));

        sentencia.executeUpdate();
        sentencia.close();

//
//        File imagen = nueva.getImagen();
//        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
//        byte[] datos = IOUtils
//
//        ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
//        BufferedImage image = ImageIO.read(bis);
//
//        File outputFile = new File("output.png");
//        ImageIO.write(image, "png", outputFile);
    }

    public void guardarPelicula(Pelicula nueva, String destino) {

    }

    private Pelicula buscarPeliculaPorID(int id) {
        return null;
    }

    public List<Pelicula> getPeliculas() throws SQLException {
        ArrayList<Pelicula> peliculas = new ArrayList<>();

        String sql = "select * from " + TABLA_PELICULAS;
        PreparedStatement sentencia = conexion.prepareStatement(sql);
        ResultSet resultado = sentencia.executeQuery();

        while (resultado.next()) {
            Pelicula pelicula = new Pelicula();
            pelicula.setId(resultado.getInt(ID));
            pelicula.setTitulo(resultado.getString(TITULO));
            pelicula.setSinopsis(resultado.getString(SINOPSIS));
            pelicula.setValoracion(resultado.getInt(VALORACION));
            pelicula.setRecaudacion(resultado.getFloat(RECAUDACION));
            Blob blob = resultado.getBlob(IMAGEN);
            byte[] imagen = blob.getBytes(1, (int) blob.length());
            pelicula.setImagen(imagen);
        }
        return peliculas;
    }

    public List<Pelicula> getPeliculas(String busqueda) {
        ArrayList<Pelicula> peliculasADevolver = new ArrayList<>();

        return peliculasADevolver;
    }

    public void eliminarPelicula(Pelicula peliculaABorrar) {
    }


    public Pelicula getUltimaBorrada() {
        Pelicula ultimaBorrada = new Pelicula();
        return ultimaBorrada;
    }

    public boolean borrarTodo() {
        return false;
    }

}
