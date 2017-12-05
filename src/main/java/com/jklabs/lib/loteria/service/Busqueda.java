package com.jklabs.lib.loteria.service;

/**
 * Created by juanky on 20/03/15.
 */
public class Busqueda extends Resultado {

    public Busqueda(String line) {
        super(line);
    }

    @Override
    public String toString() {
        return "El nº: " + String.format("%05d", Integer.parseInt(this.getValores().get(0)))
                + " está premiado con: " + this.getValores().get(1)
                + "€ al decimo. " + tratarEstado(this.getValores().get(3));
    }

    public Double getPremio() {
        double premio;
        try {
            premio = Double.parseDouble(this.getValores().get(1));
        } catch (Exception e) {
            premio = 0D;
        }
        return premio;
    }

    public String getEstado() {
        return tratarEstado(this.getValores().get(3));
    }
}
