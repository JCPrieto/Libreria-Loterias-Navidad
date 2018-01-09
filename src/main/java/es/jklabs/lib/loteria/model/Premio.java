package es.jklabs.lib.loteria.model;

import es.jklabs.lib.loteria.enumeradores.EstadoSorteo;

import java.time.LocalDateTime;

public class Premio {
    private double cantidad;
    private LocalDateTime fechaActualizacion;
    private EstadoSorteo estado;

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public EstadoSorteo getEstado() {
        return estado;
    }

    public void setEstado(EstadoSorteo estado) {
        this.estado = estado;
    }
}
