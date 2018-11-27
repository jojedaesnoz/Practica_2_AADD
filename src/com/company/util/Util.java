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
    public static <T> void operar(Iterable<T> datos, Predicate<T> condicion, Consumer<T> accion){
        for(T dato: datos)
            if (condicion.test(dato))
                accion.accept(dato);
    }

    /**
     * Carga datos desde una ruta introducida
     * @param ruta
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> T deserialize(String ruta) throws IOException, ClassNotFoundException {
        ObjectInputStream deserializador = new ObjectInputStream(new FileInputStream(ruta));
        T datos = (T) deserializador.readObject();
        deserializador.close();
        return datos;
    }

    public static <T> void serialize(String ruta, T objeto) throws IOException{
        ObjectOutputStream serializador = new ObjectOutputStream(new FileOutputStream(ruta));
        serializador.writeObject(objeto);
        serializador.close();
    }

    public static void copiarImagen(String rutaOrigen, String nombreDestino) throws IOException {
        String rutaDestino = System.getProperty("user.dir") + File.separator + "imagenes" + File.separator + nombreDestino;
        copiarFichero(rutaOrigen, rutaDestino);
    }

    public static void copiarFichero(String rutaOrigen, String rutaDestino) throws IOException {
        Path origen = FileSystems.getDefault().getPath(rutaOrigen);
        FileOutputStream destino = new FileOutputStream(new File(rutaDestino));
        Files.copy(origen, destino);
    }

    public static void mensajeInformacion(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mensajeInformacion(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mensajeError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    public static void mensajeError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.ERROR_MESSAGE);
    }

    public static Date parsearFecha(String fecha) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        return sdf.parse(fecha);
    }

    public static String formatearFecha(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        return sdf.format(fecha);
    }

    // todo getBytes, getBitmap (equivalente)
//    public static byte[] getBytes(Bitmap bitmap) {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
//        return bos.toByteArray();
//    }
//
//    public static Bitmap getBitmap(byte[] bytes) {
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//    }

//    public static void copiarImagen(String rutaOrigen, String nombreDestino) throws IOException {
//        Path origen = FileSystems.getDefault().getPath(rutaOrigen);
//
//        FileOutputStream destino = new FileOutputStream (
//                new File(System.getProperty("user.dir") +
//                        File.separator + "imagenes" + File.separator + nombreDestino));
//
//        Files.copy(origen, destino);
//    }
}
