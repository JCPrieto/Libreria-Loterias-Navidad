package io.github.jcprieto.lib.loteria.model.nino;

import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class ResumenNino implements Serializable {

    private static final long serialVersionUID = -8842164079641350214L;
    private String primero;
    private String segundo;
    private String tercero;
    private List<String> cuatroCifras;
    private List<String> tresCifras;
    private List<String> dosCifras;
    private List<String> reintegros;
    private LocalDateTime fechaActualizacion;
    private String urlPDF;
    private EstadoSorteo estado;
    private Date fechaActualizacionAndroid;

    public String getPrimero() {
        return primero;
    }

    public void setPrimero(String primero) {
        this.primero = primero;
    }

    public String getSegundo() {
        return segundo;
    }

    public void setSegundo(String segundo) {
        this.segundo = segundo;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public List<String> getCuatroCifras() {
        return cuatroCifras;
    }

    public void setCuatroCifras(List<String> cuatroCifras) {
        this.cuatroCifras = cuatroCifras;
    }

    public List<String> getTresCifras() {
        return tresCifras;
    }

    public void setTresCifras(List<String> tresCifras) {
        this.tresCifras = tresCifras;
    }

    public List<String> getDosCifras() {
        return dosCifras;
    }

    public void setDosCifras(List<String> dosCifras) {
        this.dosCifras = dosCifras;
    }

    public List<String> getReintegros() {
        return reintegros;
    }

    public void setReintegros(List<String> reintegros) {
        this.reintegros = reintegros;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getUrlPDF() {
        return urlPDF;
    }

    public void setUrlPDF(String urlPDF) {
        this.urlPDF = urlPDF;
    }

    public EstadoSorteo getEstado() {
        return estado;
    }

    public void setEstado(EstadoSorteo estado) {
        this.estado = estado;
    }

    public Date getFechaActualizacionAndroid() {
        return fechaActualizacionAndroid;
    }

    public void setFechaActualizacionAndroid(Date fechaActualizacionAndroid) {
        this.fechaActualizacionAndroid = fechaActualizacionAndroid;
    }
}
