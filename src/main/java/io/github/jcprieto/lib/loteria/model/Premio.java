package io.github.jcprieto.lib.loteria.model;

import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public class Premio {
    private BigDecimal cantidad = BigDecimal.ZERO;
    private LocalDateTime fechaActualizacion;
    private EstadoSorteo estado;
    private Date fechaActualizacionAndroid;

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        if (cantidad == null) {
            this.cantidad = BigDecimal.ZERO;
            return;
        }
        BigDecimal normalized = cantidad.stripTrailingZeros();
        if (normalized.signum() == 0) {
            this.cantidad = BigDecimal.ZERO;
            return;
        }
        this.cantidad = normalized.scale() < 0 ? normalized.setScale(0) : normalized;
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

    public Date getFechaActualizacionAndroid() {
        return fechaActualizacionAndroid;
    }

    public void setFechaActualizacionAndroid(Date fechaActualizacionAndroid) {
        this.fechaActualizacionAndroid = fechaActualizacionAndroid;
    }
}
