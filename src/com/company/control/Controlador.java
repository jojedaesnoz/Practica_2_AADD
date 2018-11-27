package com.company.control;

import com.company.base.Pelicula;
import com.company.datos.Modelo;
import com.company.ui.Vista;
import com.company.util.Util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.company.control.Controlador.Origen.MODIFICAR;
import static com.company.control.Controlador.Origen.NUEVO;
import static javax.swing.JOptionPane.OK_OPTION;

public class Controlador implements ActionListener, MouseListener, DocumentListener {
    private Modelo modelo;
    private Vista vista;
    private int idPeliculaSeleccionada;
    private File imagenSeleccionada;

    public enum Origen {
        NUEVO, MODIFICAR
    }
    private Origen origen;

    public Controlador(Modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;

        try {
            modelo.conectar();
            iniciarSesion();

            // Establecer el estado por defecto de la aplicacion
            modoEdicion(false);
            addListeners();
            colocarImagen(modelo.getDefaultImage());
            vista.btDeshacer.setEnabled(false);
            refrescarLista(modelo.getPeliculas());
        } catch (ClassNotFoundException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }



    private void configurarModo(String actionCommand) {
        switch (actionCommand) {
            case "Nuevo":
            case "Modificar":
                modoEdicion(true);
                break;
            case "Guardar":
            case "Guardar Como":
            case "Cancelar":
            case "Eliminar":
            case "Eliminar Todo":
                modoEdicion(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Configura el modo edicion segun que boton ha sido pulsado
        configurarModo(e.getActionCommand());

        // Realiza la accion correspondiente a cada boton
        switch (e.getActionCommand()) {
            case "Nuevo":

                origen = NUEVO;
                vaciarCajas();
                break;

            case "Modificar":

                origen = MODIFICAR;
                cargarPelicula(vista.listaPeliculas.getSelectedValue());
                break;

            case "Guardar":

                // Recoger la pelicula y si no hay errores, guardarla
                Pelicula pelicula;
                if (null != (pelicula = cogerDatosPelicula())) {
                    guardarPelicula(pelicula);
                }
                break;

            case "Guardar Como":
                // todo adaptar a base de datos
//                // Coger el directorio de salida
//                JFileChooser jfc = new JFileChooser();
//                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//                if (jfc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
//                    return;
//                }
//
//                // Construir la ruta de destino con el directorio seleccionado
//                String rutaSeleccionada = jfc.getSelectedFile().getPath()
//                        + File.separator + "peliculas.dat";
//                if (null != (pelicula = cogerDatosPelicula())) {
//                    guardarPelicula(pelicula, rutaSeleccionada);
//                }

            case "Cancelar":

                vaciarCajas();
                break;

            case "Eliminar":
                eliminarPelicula();
                vista.btDeshacer.setEnabled(true);
                break;
            case "Deshacer":
                deshacerUltimoBorrado();
                vista.btDeshacer.setEnabled(false);
                break;
            case "Eliminar Todo":
                String warning = "¿Está seguro de que desea borrarlo todo?";
                if(JOptionPane.showConfirmDialog(null, warning) == OK_OPTION){
                    modelo.borrarTodo();
                    if (origen != NUEVO) {
                        vaciarCajas();
                    }
                }
                break;
            default:
                break;
        }

        // Haga lo que haga, refresca la lista
        refrescarLista(modelo.getPeliculas());
    }


    /*
    Metodo para recoger los datos de las cajas de texto e imagen
    Comprueba que la pelicula tenga un titulo, porque lo hemos definido como necesario
    El resto de campos reciben valores por defecto si no estan completos
     */
    private Pelicula cogerDatosPelicula(){
        String titulo, sinopsis;
        int valoracion;
        float recaudacion;
        File imagen;

        if (vista.tfTitulo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El título es necesario", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        titulo = vista.tfTitulo.getText();
        sinopsis = vista.taSinopsis.getText();
        valoracion = vista.tfValoracion.getText().isEmpty() ?
                0 : Integer.parseInt(vista.tfValoracion.getText());
        recaudacion = vista.tfRecaudacion.getText().isEmpty() ?
                0 : Float.parseFloat(vista.tfRecaudacion.getText());

        imagen = imagenSeleccionada != null? imagenSeleccionada : modelo.getDefaultImage();

        // Construir la pelicula y devolverla
        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(titulo);
        pelicula.setSinopsis(sinopsis);
        pelicula.setValoracion(valoracion);
        pelicula.setRecaudacion(recaudacion);
        pelicula.setImagen(imagen);

        // Si se trata de una modificacion, conserva el ID
        if (origen.equals(MODIFICAR)) {
            pelicula.setId(idPeliculaSeleccionada);
        }
        return pelicula;
    }


    /****************************
     *                           *
     *   METODOS DEL MODELO      *
     *                           *
     ****************************/

    private void iniciarSesion () {
        boolean autenticado = false;
        Login login = new Login();
        int intentos = 0;

        do {
            login.tfUsuario.requestFocus();
            login.mostrarDialogo();

            String usuario = login.getUsuario();
            String contrasena = login.getContrasena();

            try {
                autenticado = modelo.iniciarSesion(usuario, contrasena);

                if (!autenticado) {
                    if (intentos > 2) {
                        Util.mensajeError("Número de intentos excedido.");
                    }

                    login.mostrarMensaje("Error en el usuario y/o contraseña.");
                    intentos++;
//                    continue;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } while (!autenticado);
    }

    private void guardarPelicula(Pelicula pelicula) {
        modelo.guardarPelicula(pelicula);
        origen = null;
    }

    private void guardarPelicula(Pelicula pelicula, String destino) {
        modelo.guardarPelicula(pelicula, destino);
        origen = null;
    }

    private void eliminarPelicula() {
        Pelicula peliculaSeleccionada = vista.listaPeliculas.getSelectedValue();
        modelo.eliminarPelicula(peliculaSeleccionada);
    }

    private void deshacerUltimoBorrado() {
        Pelicula borrada = modelo.getUltimaBorrada();
        modelo.guardarPelicula(borrada);
        cargarPelicula(borrada);
    }

    private void addListeners() {
        // BOTONES: ActionListener
        JButton[] botones = {vista.btNuevo, vista.btModificar, vista.btGuardar, vista.btGuardarComo,
                vista.btCancelar, vista.btEliminar, vista.btDeshacer, vista.btEliminarTodo};
        for (JButton boton: botones) {
            boton.addActionListener(this);
        }

        // TEXTO EN BUSQUEDA: DocumentListener
        vista.tfBusqueda.getDocument().addDocumentListener(this);

        // IMAGEN: MouseListener
        vista.lImagen.addMouseListener(this);

        // CLICK EN LISTA: MouseListener
        vista.listaPeliculas.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getComponent().equals(vista.listaPeliculas)) {
            // Click en la lista
            cargarPelicula(vista.listaPeliculas.getSelectedValue());
            idPeliculaSeleccionada = vista.listaPeliculas.getSelectedValue().getId();

        } else if (e.getComponent().equals(vista.lImagen)) {
            // Click en la imagen
            JFileChooser jfc = new JFileChooser();
            if (jfc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
                return;

            imagenSeleccionada = jfc.getSelectedFile();
            colocarImagen(imagenSeleccionada);
        }
    }


    /****************************
     *                           *
     *   METODOS DE LA VISTA     *
     *                           *
     ****************************/

    private void modoEdicion(boolean modo){
        // Activar/desactivar botones
        vista.btGuardar.setEnabled(modo);
        vista.btCancelar.setEnabled(modo);

        // Activar/desactivar campos
        vista.tfTitulo.setEnabled(modo);
        vista.taSinopsis.setEnabled(modo);
        vista.tfValoracion.setEnabled(modo);
        vista.tfRecaudacion.setEnabled(modo);
    }

    private void refrescarLista(List<Pelicula> nuevaLista) {
        vista.modeloPeliculas.removeAllElements();
        for (Pelicula pelicula: nuevaLista) {
            vista.modeloPeliculas.addElement(pelicula);
        }
    }

    private void vaciarCajas() {
        JTextField[] cajas = {vista.tfTitulo,  vista.tfValoracion, vista.tfRecaudacion};
        for (JTextField textField: cajas) {
            textField.setText("");
        }
        vista.taSinopsis.setText("");
        colocarImagen(modelo.getDefaultImage());
    }

    private void cargarPelicula(Pelicula pelicula) {
        idPeliculaSeleccionada = pelicula.getId();

        vista.tfTitulo.setText(pelicula.getTitulo());
        vista.tfValoracion.setText(String.valueOf(pelicula.getValoracion()));
        vista.tfRecaudacion.setText(String.valueOf(pelicula.getRecaudacion()));
        vista.taSinopsis.setText(pelicula.getSinopsis());

        // Colocar la imagen de la pelicula si tiene y si no, la imagen por defecto
        colocarImagen(pelicula.getImagen().exists() ?
                pelicula.getImagen() : modelo.getDefaultImage());
    }

    private void colocarImagen(File imagen) {
        ImageIcon imageIcon = new ImageIcon(imagen.getPath());
        Image image = imageIcon.getImage().getScaledInstance(240, -1, Image.SCALE_DEFAULT);
        vista.lImagen.setIcon(new ImageIcon(image));
        vista.pack();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        // Cargar la lista que devuelve lo que haya en la caja de busqueda
        refrescarLista(modelo.getPeliculas(vista.tfBusqueda.getText()));
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        // Cargar la lista que devuelve lo que haya en la caja de busqueda
        refrescarLista(modelo.getPeliculas(vista.tfBusqueda.getText()));
    }



    /****************************
     *                           *
     *   LISTENERS VACIOS        *
     *                           *
     ****************************/

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
