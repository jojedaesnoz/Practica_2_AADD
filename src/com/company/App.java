package com.company;

import com.company.control.Controlador;
import com.company.datos.Modelo;
import com.company.ui.Vista;

public class App {

    public static void main (String[] args) {
        Modelo modelo = new Modelo();
        Vista vista = new Vista();
        Controlador controlador = new Controlador(modelo, vista);
    }
}
