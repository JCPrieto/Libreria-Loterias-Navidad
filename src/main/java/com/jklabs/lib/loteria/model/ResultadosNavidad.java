package com.jklabs.lib.loteria.model;

import com.jklabs.lib.loteria.service.Resultado;

import java.util.Date;

/**
 * Created by juanky on 20/03/15.
 */
public class ResultadosNavidad extends Resultado {

    public ResultadosNavidad(String consulta) {
        super(consulta);
    }

    public String getCuarto1() {
        return tratarNumero(this.valores.get(5));
    }

    public String getCuarto2() {
        return tratarNumero(this.valores.get(6));
    }

    public String getEstado() {
        return tratarEstado(this.valores.get(1));
    }

    public Date getFecha() {
        return tratarFecha(this.valores.get(0));
    }

    public String getGordo() {
        return tratarNumero(this.valores.get(2));
    }

    public String getPDF() {
        return this.valores.get(17);
    }

    public String getQuinto1() {
        return tratarNumero(this.valores.get(7));
    }

    public String getQuinto2() {
        return tratarNumero(this.valores.get(8));
    }

    public String getQuinto3() {
        return tratarNumero(this.valores.get(9));
    }

    public String getQuinto4() {
        return tratarNumero(this.valores.get(10));
    }

    public String getQuinto5() {
        return tratarNumero(this.valores.get(11));
    }

    public String getQuinto6() {
        return tratarNumero(this.valores.get(12));
    }

    public String getQuinto7() {
        return tratarNumero(this.valores.get(13));
    }

    public String getQuinto8() {
        return tratarNumero(this.valores.get(14));
    }

    public String getSegundo() {
        return tratarNumero(this.valores.get(3));
    }

    public String getTercero() {
        return tratarNumero(this.valores.get(4));
    }

}
