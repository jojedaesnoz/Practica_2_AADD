package com.company.zzz;

import com.company.base.Pelicula;
import com.company.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.company.util.Constantes.RUTA_IMAGENES;
import static com.company.util.Constantes.RUTA_PELICULAS;

public class ModeloAntiguo {
    private ArrayList<Pelicula> peliculas;
    private int ultimoID;
    private Pelicula ultimaBorrada;

    public ModeloAntiguo() throws IOException, ClassNotFoundException {
        if (new File(RUTA_PELICULAS).exists()) {
            // Si existe el fichero, lo carga del disco
            cargarDeDisco();
            ultimoID = peliculas.get(peliculas.size() - 1).getId();
        } else {
            // Si no existe, crea un modelo de cero
            peliculas = new ArrayList<>();
            ultimoID = 0;
        }
        limpiarImagenesSobrantes();
    }


    public void guardarPelicula(Pelicula nueva) throws IOException {
        guardarPelicula(nueva, RUTA_PELICULAS);
    }

    public void guardarPelicula(Pelicula nueva, String destino) throws IOException {
        // Construir la imagen de destino
        File imagenFuente = nueva.getImagen();
        String[] partes = imagenFuente.getName().split("[.]");
        String extension = partes.length == 2 ? "." + partes[1] : "";
        File imagenSalida = new File(RUTA_IMAGENES + File.separator
                + UUID.randomUUID().toString() + extension);

        // Guardar la imagen
        nueva.setImagen(imagenSalida);
        Util.copiarFichero(imagenFuente.getPath(), imagenSalida.getPath());

        // Guardar o modificar la pelicula, segun si ya existe en el modelo
        Pelicula pelicula = buscarPeliculaPorID(nueva.getId());
        if (pelicula != null) {
            peliculas.remove(pelicula);
        } else {
            ultimoID++;
            nueva.setId(ultimoID);
        }
        peliculas.add(nueva);
        guardarADisco(destino);
    }

    private Pelicula buscarPeliculaPorID(int id) {
        for (Pelicula pelicula: peliculas) {
            if (pelicula.getId() == id) {
                return pelicula;
            }
        }
        return null;
    }

    private void cargarDeDisco() throws IOException, ClassNotFoundException {
        peliculas = Util.deserialize(RUTA_PELICULAS);
    }

    private void guardarADisco(String destino) throws IOException {
        Util.serialize(destino, peliculas);
    }

    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public List<Pelicula> getPeliculas(String busqueda) {
        ArrayList<Pelicula> peliculasADevolver = new ArrayList<>();

        // Buscar las peliculas que cumplan el criterio
        for (Pelicula pelicula: peliculas) {
            String titulo = pelicula.getTitulo().toLowerCase();
            if (titulo.contains(busqueda.toLowerCase())) {
                peliculasADevolver.add(pelicula);
            }
        }

        // Devolver el resultado
        return peliculasADevolver;
    }

    public void eliminarPelicula(Pelicula peliculaABorrar) throws IOException {
        // Comprobar que existe antes de intentar borrar
        Pelicula pelicula = buscarPeliculaPorID(peliculaABorrar.getId());
        if (pelicula == null)
            return;

        ultimaBorrada = pelicula;
        peliculas.remove(pelicula);
        guardarADisco(RUTA_PELICULAS);
    }

    // Borra las imagenes que no esten siendo usadas por ninguna pelicula
    private void limpiarImagenesSobrantes(){
        ArrayList<File> imagenesUsadas = new ArrayList<>();
        for (Pelicula pelicula: peliculas) {
            imagenesUsadas.add(pelicula.getImagen());
        }

        for (File imagen:  new File(RUTA_IMAGENES).listFiles()) {
            if (!imagenesUsadas.contains(imagen))
                imagen.delete();
        }
    }

    public Pelicula getUltimaBorrada() {
        return ultimaBorrada;
    }

    public boolean borrarTodo() {
        File ficheroAlmacenamiento = new File(RUTA_PELICULAS);
        if (ficheroAlmacenamiento.exists()) {
            peliculas.clear();
            return ficheroAlmacenamiento.delete();
        }
        return false;
    }

}
