package io.github.jcprieto.lib.loteria.model.json.navidad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SorteoConEscrutinioResponse {

    @JsonProperty("id_sorteo")
    private String idSorteo;
    @JsonProperty("fecha_sorteo")
    private String fechaSorteo;
    private String estado;

    public String getIdSorteo() {
        return idSorteo;
    }

    public void setIdSorteo(String idSorteo) {
        this.idSorteo = idSorteo;
    }

    public String getFechaSorteo() {
        return fechaSorteo;
    }

    public void setFechaSorteo(String fechaSorteo) {
        this.fechaSorteo = fechaSorteo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
