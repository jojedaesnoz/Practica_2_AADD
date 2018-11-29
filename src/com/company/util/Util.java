package com.company.util;


import javax.swing.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Util<T> {

    public static void copiarFichero(String rutaOrigen, String rutaDestino) throws IOException {
        Path origen = FileSystems.getDefault().getPath(rutaOrigen);
        FileOutputStream destino = new FileOutputStream(new File(rutaDestino));
        Files.copy(origen, destino);
    }

    public static void mensajeError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.ERROR_MESSAGE);
    }
}
