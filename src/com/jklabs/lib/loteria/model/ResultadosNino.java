package com.jklabs.lib.loteria.model;

import com.jklabs.lib.loteria.service.Resultado;

/**
 * Created by juanky on 21/03/15.
 */
public class ResultadosNino extends Resultado {

    public ResultadosNino(String resultado) {
        super(resultado);
    }

    public String getEstado() {
        return tratarEstado(this.valores.get(1));
    }

    /**
     * @return Las extracciones de 2 cifras
     */
    public String[] getExtraccionDos() {
        return tratarArray(this.valores.get(10));
    }

    /**
     * @return Las extracciones de 3 cifras
     */
    public String[] getExtraccionTres() {
        return tratarArray(this.valores.get(9));
    }

    public String getFecha() {
        return tratarFecha(this.valores.get(0));
    }

    public String getPDF() {
        return this.valores.get(3);
    }

    /**
     * @return Primer Premio
     */
    public String getPrimero() {
        return tratarNumero(this.valores.get(5));
    }

    /**
     * @return Reitegros
     */
    public String[] getReintegros() {
        return tratarArray(this.valores.get(11));
    }

    /**
     * @return Segundo Premio
     */
    public String getSegundo() {
        return tratarNumero(this.valores.get(6));
    }

    /**
     * @return Tercer premio
     */
    public String getTercero() {
        return tratarNumero(this.valores.get(7));
    }

    /**
     * @return Extracciones de 4 cifras.
     */
    public String[] getExtraccionCuatro() {
        return tratarArray(this.valores.get(8));
    }

    /**
     * @param string Array en formato JSON
     * @return Array de String con los numeros, si han salido ya, o con un '-'
     * si aun no han salido
     */
    private String[] tratarArray(String string) {
        String cad = string.substring(1, string.length() - 1);
        String[] array = cad.split(",");
        String[] res = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            if (Integer.parseInt(array[i].trim()) == -1) {
                res[i] = "-";
            } else {
                res[i] = array[i].trim();
            }
        }
        return res;
    }

}
