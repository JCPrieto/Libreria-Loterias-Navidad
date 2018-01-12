package es.jklabs.lib.loteria.model.navidad;

import es.jklabs.lib.loteria.enumeradores.EstadoSorteo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ResumenNavidad implements Serializable {

    private static final long serialVersionUID = 4536837407643098849L;
    private String gordo;
    private String segundo;
    private String tercero;
    private List<String> cuarto;
    private List<String> quinto;
    private LocalDateTime fechaActualizacion;
    private String urlPDF;
    private EstadoSorteo estado;

    public String getGordo() {
        return gordo;
    }

    public void setGordo(String gordo) {
        this.gordo = gordo;
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

    public List<String> getCuarto() {
        return cuarto;
    }

    public void setCuarto(List<String> cuarto) {
        this.cuarto = cuarto;
    }

    public List<String> getQuinto() {
        return quinto;
    }

    public void setQuinto(List<String> quinto) {
        this.quinto = quinto;
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
}
