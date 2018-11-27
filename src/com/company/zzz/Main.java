package com.company.zzz;

import com.company.control.Controlador;
import com.company.datos.Modelo;
import com.company.ui.Vista;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
		Modelo modelo = null;
		try {
			modelo = new Modelo();
			Vista vista = new Vista();
			Controlador controlador = new Controlador(modelo, vista);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
}
