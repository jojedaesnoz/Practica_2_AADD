package com.company.zzz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Login extends JDialog implements ActionListener, KeyListener {
    public JLabel lUser, lPass, mensaje;
    public JTextField tfUsuario;
    public JPasswordField pfContrasena;
    public JButton btEntrar, btSalir;
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
        for (JComponent component: new JComponent[]{lUser, tfUsuario, lPass, pfContrasena, btEntrar, btSalir}){
            component.setPreferredSize(new Dimension(160, 35));
            panelEntradas.add(component);
        }
        add(panelEntradas, BorderLayout.CENTER);
        add(mensaje, BorderLayout.SOUTH);

        btEntrar.addActionListener(this);
        btSalir.addActionListener(this);

        tfUsuario.addKeyListener(this);
        pfContrasena.addKeyListener(this);
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
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            btEntrar.doClick();
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

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public JLabel getMensaje() {
        return mensaje;
    }

    public void mostrarMensaje(String mensaje){
        this.mensaje.setText(mensaje);
        this.mensaje.setVisible(true);
    }
}
