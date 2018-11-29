package com.company.datos;

import com.company.base.Pelicula;
import com.company.util.Util;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static com.company.util.Constantes.*;

public class Modelo {
    private Connection conexion;
    private Pelicula ultimaBorrada;

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

    public void desconectar() throws SQLException {
        limpiarImagenesSobrantes();

        conexion.close();
        conexion = null;
    }

    // Borra las imagenes que no esten siendo usadas por ninguna pelicula


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

    public String getDefaultImage() {
        return DEFAULT_IMAGE;
    }



    public void guardarPelicula(Pelicula nueva) throws SQLException, IOException {
        nueva.setRutaImagen(copiarImagen(nueva.getRutaImagen()));

        // Si ya existe, la modifica
        if (nueva.getId() != 0) {
            modificarPelicula(nueva);
            return;
        }

        // Si no, la crea de cero
        String sql = "insert into " + TABLA_PELICULAS + " (" +
                TITULO + ", " + SINOPSIS + ", " + VALORACION + ", " +
                RECAUDACION + ", " + IMAGEN + ") values (?, ?, ?, ?, ?)";

        PreparedStatement sentencia = conexion.prepareStatement(sql);

        sentencia.setString(1, nueva.getTitulo());
        sentencia.setString(2, nueva.getSinopsis());
        sentencia.setInt(3, nueva.getValoracion());
        sentencia.setFloat(4, nueva.getRecaudacion());
        sentencia.setString(5, nueva.getRutaImagen());

        sentencia.executeUpdate();
        sentencia.close();
    }

    private void modificarPelicula (Pelicula pelicula) throws SQLException {
        String sql = "update " + TABLA_PELICULAS + " set " +
                TITULO + " = ?, " + SINOPSIS + " = ?, " + VALORACION + " = ?, " +
                RECAUDACION + " = ?, " + IMAGEN + " = ? where " + ID + " = ?";

        PreparedStatement sentencia = conexion.prepareStatement(sql);

        sentencia.setString(1, pelicula.getTitulo());
        sentencia.setString(2, pelicula.getSinopsis());
        sentencia.setInt(3, pelicula.getValoracion());
        sentencia.setFloat(4, pelicula.getRecaudacion());
        sentencia.setString(5, pelicula.getRutaImagen());
        sentencia.setInt(6, pelicula.getId());

        sentencia.executeUpdate();
        sentencia.close();
    }

    private Pelicula buscarPeliculaPorID(int id) {
        return null;
    }

    public List<Pelicula> getPeliculas() throws SQLException {
        ArrayList<Pelicula> peliculasADevolver = new ArrayList<>();

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
            pelicula.setRutaImagen(resultado.getString(IMAGEN));

            peliculasADevolver.add(pelicula);
        }

        return peliculasADevolver;
    }

    public List<Pelicula> getPeliculas(String busqueda) throws SQLException {
        ArrayList<Pelicula> peliculasADevolver = new ArrayList<>();

        for (Pelicula pelicula: getPeliculas())
            if (pelicula.getTitulo().toLowerCase().contains(busqueda.toLowerCase()))
                peliculasADevolver.add(pelicula);

        return peliculasADevolver;
    }

    public void eliminarPelicula(Pelicula peliculaABorrar) throws SQLException {
        String sql = "delete from " + TABLA_PELICULAS + " where id = ?";
        PreparedStatement sentencia = conexion.prepareStatement(sql);
        sentencia.setInt(1, peliculaABorrar.getId());
        sentencia.executeUpdate();
        ultimaBorrada = peliculaABorrar;
    }


    public Pelicula getUltimaBorrada() {
        return ultimaBorrada;
    }

    public boolean borrarTodo() throws SQLException {
        String sql = "delete from " + TABLA_PELICULAS;
        PreparedStatement sentencia = conexion.prepareStatement(sql);
        return sentencia.executeUpdate() != 0;
    }

    private void limpiarImagenesSobrantes() throws SQLException {
        ArrayList<File> imagenesUsadas = new ArrayList<>();
        for (Pelicula pelicula: getPeliculas()) {
            imagenesUsadas.add(new File(pelicula.getRutaImagen()));
        }

        File carpetaImagenes = new File(RUTA_IMAGENES);
        for (File imagen:  carpetaImagenes.listFiles()) {
            if (!imagenesUsadas.contains(imagen))
                imagen.delete();
        }
    }

    private String copiarImagen (String rutaImagen) throws IOException {
        // Copiar la imagen a un directorio de la aplicacion, manteniendo su extension
        String[] partes =  new File(rutaImagen).getName().split("[.]");
        String extension = partes.length == 2 ? "." + partes[1] : "";
        String rutaSalida = RUTA_IMAGENES + File.separator
                + UUID.randomUUID().toString() + extension;
        Util.copiarFichero(rutaImagen, rutaSalida);
        return rutaSalida;
    }

}
