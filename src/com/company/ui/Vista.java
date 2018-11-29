package com.company.ui;

import com.company.base.Pelicula;

import javax.swing.*;
import java.awt.*;

public class Vista extends JFrame {

    // LISTA
    public DefaultListModel<Pelicula> modeloPeliculas;
    public JList<Pelicula> listaPeliculas;

    // BOTONES
    public JButton btNuevo, btGuardar, btModificar, btCancelar, btEliminar, btDeshacer, btEliminarTodo;

    // LABELS CAJAS E IMAGEN
    private JLabel lTitulo, lSinopsis, lValoracion, lRecaudacion;
    public JTextField tfTitulo, tfValoracion, tfRecaudacion, tfBusqueda;
    public JTextArea taSinopsis;
    public JLabel lImagen;

    public Vista() {
        inicializarComponentes();
        darTamsYBordes();
        colocarComponentes();
        crearPanelBotones();
        prepararVentana();
    }

    private void inicializarComponentes() {
        // LABELS
        lTitulo = new JLabel("Título", SwingConstants.CENTER);
        lSinopsis = new JLabel("Sinopsis", SwingConstants.CENTER);
        lValoracion = new JLabel("Valoración", SwingConstants.CENTER);
        lRecaudacion = new JLabel("Recaudación", SwingConstants.CENTER);

        // CAJAS
        tfTitulo = new JTextField();
        taSinopsis = new JTextArea();
        tfValoracion = new JTextField();
        tfRecaudacion = new JTextField();
        tfBusqueda = new JTextField();

        // IMAGEN
        lImagen = new JLabel();

        // BOTONES
        btNuevo = new JButton("Nuevo");
        btGuardar = new JButton("Guardar");
        btModificar = new JButton("Modificar");
        btCancelar = new JButton("Cancelar");
        btEliminar = new JButton("Eliminar");
        btDeshacer = new JButton("Deshacer");
        btEliminarTodo = new JButton("Eliminar Todo");
    }

    private void darTamsYBordes() {
        // LABELS
        for (JLabel label: new JLabel[]{lTitulo, lSinopsis, lValoracion, lRecaudacion}){
            label.setPreferredSize(new Dimension(80, 30));
        }

        // CAJAS
        for (JComponent entrada: new JComponent[]{tfTitulo, tfValoracion, tfRecaudacion, tfBusqueda}) {
            entrada.setPreferredSize(new Dimension(150, 25));
            entrada.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
        taSinopsis.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // IMAGEN
        lImagen.setMinimumSize(new Dimension(200, 100));
    }

    private void colocarComponentes(){
        JPanel contenedor = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // IMAGEN
        lImagen.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 2;
        int posicionPostImagen = gbc.gridheight + 1;
        gbc.fill = GridBagConstraints.BOTH;
        contenedor.add(lImagen, gbc);
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;

        // ETIQUETAS
        gbc.gridx = 0;
        gbc.gridy = posicionPostImagen;
        contenedor.add(lTitulo, gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTH;
        contenedor.add(lSinopsis, gbc);
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridy++;
        contenedor.add(lValoracion, gbc);

        gbc.gridy++;
        contenedor.add(lRecaudacion, gbc);

        // ENTRADAS
        gbc.gridx = 1;
        gbc.gridy = posicionPostImagen;
        contenedor.add(tfTitulo, gbc);

        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy++;
        contenedor.add(taSinopsis, gbc);
        gbc.weighty = 0;

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy++;
        contenedor.add(tfValoracion, gbc);

        gbc.gridy++;
        contenedor.add(tfRecaudacion, gbc);

        // CAJAS DE ERROR

        // CAJA DE BUSQUEDA
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        contenedor.add(tfBusqueda, gbc);

        // LISTA
        JScrollPane panelPeliculas = crearScrollPeliculas();
        gbc.weighty = 1;
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 9;
        gbc.fill = GridBagConstraints.BOTH;
        contenedor.add(panelPeliculas, gbc);

        // Colocar el panel
        add(contenedor);
    }

    private void prepararVentana() {
        Dimension dimensiones = new Dimension(800, 500);
        setSize(dimensiones);
        setMinimumSize(dimensiones);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void crearPanelBotones() {

        JPanel botonera = new JPanel();
        JButton[] botones = {btNuevo, btGuardar, btModificar, btCancelar,
                btEliminar, btDeshacer, btEliminarTodo};

        botonera.setLayout(new GridLayout(1, botones.length));
        for (JButton boton: botones)
            botonera.add(boton);

        add(botonera, BorderLayout.SOUTH);
    }

    private JScrollPane crearScrollPeliculas() {
        modeloPeliculas = new DefaultListModel<>();
        listaPeliculas = new JList<>(modeloPeliculas);
        return new JScrollPane(listaPeliculas);
    }
}
