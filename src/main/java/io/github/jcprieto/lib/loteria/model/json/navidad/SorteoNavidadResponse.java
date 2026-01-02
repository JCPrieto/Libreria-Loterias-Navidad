package io.github.jcprieto.lib.loteria.model.json.navidad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SorteoNavidadResponse {

    @JsonProperty("fecha_sorteo")
    private String fechaSorteo;
    private String estado;
    private PremioDetalle primerPremio;
    private PremioDetalle segundoPremio;
    private List<PremioDetalle> tercerosPremios;
    private List<PremioDetalle> cuartosPremios;
    private List<PremioDetalle> quintosPremios;
    private List<PremioDetalle> extraccionesDeCuatroCifras;
    private List<PremioDetalle> extraccionesDeTresCifras;
    private List<PremioDetalle> extraccionesDeDosCifras;
    private List<PremioDetalle> reintegros;
    private String urlListadoOficial;

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

    public PremioDetalle getPrimerPremio() {
        return primerPremio;
    }

    public void setPrimerPremio(PremioDetalle primerPremio) {
        this.primerPremio = primerPremio;
    }

    public PremioDetalle getSegundoPremio() {
        return segundoPremio;
    }

    public void setSegundoPremio(PremioDetalle segundoPremio) {
        this.segundoPremio = segundoPremio;
    }

    public List<PremioDetalle> getTercerosPremios() {
        return tercerosPremios;
    }

    public void setTercerosPremios(List<PremioDetalle> tercerosPremios) {
        this.tercerosPremios = tercerosPremios;
    }

    public List<PremioDetalle> getCuartosPremios() {
        return cuartosPremios;
    }

    public void setCuartosPremios(List<PremioDetalle> cuartosPremios) {
        this.cuartosPremios = cuartosPremios;
    }

    public List<PremioDetalle> getQuintosPremios() {
        return quintosPremios;
    }

    public void setQuintosPremios(List<PremioDetalle> quintosPremios) {
        this.quintosPremios = quintosPremios;
    }

    public List<PremioDetalle> getExtraccionesDeCuatroCifras() {
        return extraccionesDeCuatroCifras;
    }

    public void setExtraccionesDeCuatroCifras(List<PremioDetalle> extraccionesDeCuatroCifras) {
        this.extraccionesDeCuatroCifras = extraccionesDeCuatroCifras;
    }

    public List<PremioDetalle> getExtraccionesDeTresCifras() {
        return extraccionesDeTresCifras;
    }

    public void setExtraccionesDeTresCifras(List<PremioDetalle> extraccionesDeTresCifras) {
        this.extraccionesDeTresCifras = extraccionesDeTresCifras;
    }

    public List<PremioDetalle> getExtraccionesDeDosCifras() {
        return extraccionesDeDosCifras;
    }

    public void setExtraccionesDeDosCifras(List<PremioDetalle> extraccionesDeDosCifras) {
        this.extraccionesDeDosCifras = extraccionesDeDosCifras;
    }

    public List<PremioDetalle> getReintegros() {
        return reintegros;
    }

    public void setReintegros(List<PremioDetalle> reintegros) {
        this.reintegros = reintegros;
    }

    public String getUrlListadoOficial() {
        return urlListadoOficial;
    }

    public void setUrlListadoOficial(String urlListadoOficial) {
        this.urlListadoOficial = urlListadoOficial;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PremioDetalle {
        private String decimo;

        public String getDecimo() {
            return decimo;
        }

        public void setDecimo(String decimo) {
            this.decimo = decimo;
        }
    }
}
