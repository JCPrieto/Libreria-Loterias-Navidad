package es.jklabs.lib.loteria.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Busqueda {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String numero;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int premio;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int status;
    private int error;

    public Busqueda() {

    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getPremio() {
        return premio;
    }

    public void setPremio(int premio) {
        this.premio = premio;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
