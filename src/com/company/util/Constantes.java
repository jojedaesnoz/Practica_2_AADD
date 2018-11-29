package com.company.util;

import java.io.File;

public class Constantes {

    // TABLA PELICULAS BBDD
    public static final String TABLA_PELICULAS = "peliculas";
    public static final String ID = "id";
    public static final String TITULO = "titulo";
    public static final String SINOPSIS = "sinopsis";
    public static final String VALORACION = "valoracion";
    public static final String RECAUDACION = "recaudacion";
    public static final String IMAGEN = "imagen";

    // RECURSOS LOCALES
    private static final String RECURSOS = "res";
    public static final String RUTA_PROPERTIES = RECURSOS + File.separator + "config.properties";
    public static final String RUTA_IMAGENES = RECURSOS + File.separator + "images";
    public static final String DEFAULT_IMAGE = RECURSOS + File.separator + "default_image.jpg";
}
