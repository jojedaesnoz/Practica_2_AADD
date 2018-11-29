package com.company.control;

import com.company.base.Pelicula;
import com.company.datos.Modelo;
import com.company.ui.Vista;
import com.company.util.Util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static com.company.control.Controlador.Origen.MODIFICAR;
import static com.company.control.Controlador.Origen.NUEVO;
import static javax.swing.JOptionPane.OK_OPTION;

public class Controlador implements ActionListener, MouseListener, DocumentListener, WindowListener {
    private Modelo modelo;
    private Vista vista;
    private int idPeliculaSeleccionada;
    private String imagenSeleccionada;
    public enum Origen {
        NUEVO, MODIFICAR
    }
    private Origen origen;

    /*
    Puntos opcionales:
        Añadir una opción al usuario que permita recuperar el último elemento borrado
        Añadir una opción a la aplicación que permita eliminar todos los datos del programa
     */

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

    /****************************
     *                           *
     *      LISTENERS            *
     *                           *
     ****************************/

    private void addListeners() {
        // BOTONES: ActionListener
        JButton[] botones = {vista.btNuevo, vista.btModificar, vista.btGuardar,
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

        // CIERRE DE LA VENTANA: WindowListener
        vista.addWindowListener(this);
    }

    private void configurarModo(String actionCommand) {
        switch (actionCommand) {
            case "Nuevo":
            case "Modificar":
                modoEdicion(true);
                break;
            case "Guardar":
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
                eliminarTodo();
                break;
            default:
                break;
        }

        // Haga lo que haga, refresca la lista
        try {
            refrescarLista(modelo.getPeliculas());
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        // Cargar la lista que devuelve lo que haya en la caja de busqueda
        try {
            refrescarLista(modelo.getPeliculas(vista.tfBusqueda.getText()));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        // Cargar la lista que devuelve lo que haya en la caja de busqueda
        try {
            refrescarLista(modelo.getPeliculas(vista.tfBusqueda.getText()));
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            modelo.desconectar();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getComponent().equals(vista.listaPeliculas) && !vista.modeloPeliculas.isEmpty()) {
            // Click en la lista
            cargarPelicula(vista.listaPeliculas.getSelectedValue());
            idPeliculaSeleccionada = vista.listaPeliculas.getSelectedValue().getId();

        } else if (e.getComponent().equals(vista.lImagen)) {
            // Click en la imagen
            JFileChooser jfc = new JFileChooser();
            if (jfc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
                return;

            imagenSeleccionada = jfc.getSelectedFile().getAbsolutePath();
            colocarImagen(imagenSeleccionada);
        }
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
        String imagen;

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
        pelicula.setRutaImagen(imagen);

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
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } while (!autenticado);
    }

    private void guardarPelicula(Pelicula pelicula) {
        try {
            modelo.guardarPelicula(pelicula);
            origen = null;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void eliminarPelicula() {
        Pelicula peliculaSeleccionada = vista.listaPeliculas.getSelectedValue();
        if (peliculaSeleccionada != null) {
            try {
                modelo.eliminarPelicula(peliculaSeleccionada);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void eliminarTodo() {
        String warning = "¿Está seguro de que desea borrarlo todo?";
        if(JOptionPane.showConfirmDialog(null, warning) == OK_OPTION){
            try {
                modelo.borrarTodo();
                if (origen != NUEVO) {
                    vaciarCajas();
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
    }

    private void deshacerUltimoBorrado() {
        Pelicula borrada = modelo.getUltimaBorrada();
        try {
            borrada.setId(0);
            modelo.guardarPelicula(borrada);
            cargarPelicula(borrada);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
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
        colocarImagen(new File(pelicula.getRutaImagen()).exists() ?
                pelicula.getRutaImagen() : modelo.getDefaultImage());
    }

    private void colocarImagen(String imagen) {
        ImageIcon imageIcon = new ImageIcon(imagen);
        Image image = imageIcon.getImage().getScaledInstance(240, -1, Image.SCALE_DEFAULT);
        vista.lImagen.setIcon(new ImageIcon(image));
        vista.pack();
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

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

}
