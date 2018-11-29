package com.company.control;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Login extends JDialog implements ActionListener, KeyListener, FocusListener {
    public JLabel lUser, lPass, mensaje;
    public JTextField tfUsuario;
    public JPasswordField pfContrasena;
    public JButton btEntrar, btSalir;
    public JComponent[] componentes;
    private String usuario, contrasena;

    public Login() {
        prepararComponentes();
        prepararVentana();
    }

    private void prepararComponentes() {
        lUser = new JLabel("Usuario:", SwingConstants.CENTER);
        lPass = new JLabel("Contraseña:", SwingConstants.CENTER);
        tfUsuario = new JTextField();
        pfContrasena = new JPasswordField();
        btEntrar = new JButton("Entrar");
        btSalir = new JButton("Salir");
        mensaje = new JLabel("", SwingConstants.CENTER);
        mensaje.setForeground(Color.red);
        mensaje.setVisible(false);

        JPanel panelEntradas = new JPanel(new GridLayout(3, 2));
        componentes = new JComponent[]{lUser, tfUsuario, lPass, pfContrasena, btEntrar, btSalir};
        for (JComponent componente: componentes){
            componente.setPreferredSize(new Dimension(160, 35));
            componente.addKeyListener(this);
            componente.addFocusListener(this);
            panelEntradas.add(componente);
        }
        add(panelEntradas, BorderLayout.CENTER);
        add(mensaje, BorderLayout.SOUTH);

        btEntrar.addActionListener(this);
        btSalir.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case "Entrar":
                usuario = tfUsuario.getText();
                contrasena = String.valueOf(pfContrasena.getPassword());
                setVisible(false);
                break;
            case "Salir":
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            btEntrar.doClick();
        } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
            // Lleva el focus al siguiente componente
            JComponent componenteConFocus = (JComponent) getFocusOwner();
            for (int i = 0; i < componentes.length; i++) {
                if(componentes[i].equals(componenteConFocus)) {
                    if (i >= componentes.length - 1) {
                        componentes[i].requestFocus();
                    } else {
                        componentes[i + 1].requestFocus();
                    }
                }
            }
        }
    }

    private void prepararVentana() {

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        //setModal(true);
        setTitle("Mi aplicación");
        pack();
        setLocationRelativeTo(null);
        setModal(true);
    }

    public void mostrarDialogo() {
        setVisible(true);
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void mostrarMensaje(String mensaje){
        this.mensaje.setText(mensaje);
        this.mensaje.setVisible(true);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getComponent().equals(tfUsuario)) {
            tfUsuario.selectAll();
        } else if (e.getComponent().equals(pfContrasena)) {
            pfContrasena.selectAll();
        }
    }


    // LISTENERS VACIOS

    @Override
    public void focusLost(FocusEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

}
