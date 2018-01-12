package es.jklabs.lib.loteria.converter;

import es.jklabs.lib.loteria.enumeradores.EstadoSorteo;
import es.jklabs.lib.loteria.model.json.nino.Premios;
import es.jklabs.lib.loteria.model.nino.ResumenNino;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class ResumenNinoConverter {

    private ResumenNinoConverter() {

    }

    public static ResumenNino get(Premios premios) {
        ResumenNino resumen = new ResumenNino();
        if (premios.getPremio1() > -1) {
            resumen.setPrimero(String.format("%05d", premios.getPremio1()));
        }
        if (premios.getPremio2() > -1) {
            resumen.setSegundo(String.format("%05d", premios.getPremio2()));
        }
        if (premios.getPremio3() > -1) {
            resumen.setTercero(String.format("%05d", premios.getPremio3()));
        }
        resumen.setCuatroCifras(new ArrayList<>());
        for (String extraccion : premios.getExtracciones4cifras()) {
            if (!extraccion.equals("-1")) {
                resumen.getCuatroCifras().add(extraccion);
            }
        }
        resumen.setTresCifras(new ArrayList<>());
        for (String extraccion : premios.getExtracciones3cifras()) {
            if (!extraccion.equals("-1")) {
                resumen.getTresCifras().add(extraccion);
            }
        }
        resumen.setDosCifras(new ArrayList<>());
        for (String extraccion : premios.getExtracciones2cifras()) {
            if (!extraccion.equals("-1")) {
                resumen.getDosCifras().add(extraccion);
            }
        }
        resumen.setReintegros(new ArrayList<>());
        for (String extraccion : premios.getReintegros()) {
            if (!extraccion.equals("-1")) {
                resumen.getReintegros().add(extraccion);
            }
        }
        resumen.setFechaActualizacion(LocalDateTime.ofInstant(Instant.ofEpochSecond(premios.getTimestamp()), ZoneId
                .systemDefault()));
        resumen.setUrlPDF(premios.getPdfURL());
        resumen.setEstado(EstadoSorteo.get(premios.getStatus()));
        return resumen;
    }
}
