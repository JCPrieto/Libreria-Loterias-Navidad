package es.jklabs.lib.loteria.enumeradores;

public enum Sorteo {

    NAVIDAD("Navidad"),
    NINO("Nino");

    private final String parametro;

    private Sorteo(String parametro) {
        this.parametro = parametro;
    }

    public String getParametro() {
        return parametro;
    }
}
