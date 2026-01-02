package io.github.jcprieto.lib.loteria.converter;

import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import io.github.jcprieto.lib.loteria.model.json.nino.Premios;
import io.github.jcprieto.lib.loteria.model.nino.ResumenNino;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;

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
        setFechaActualizacion(premios, resumen);
        resumen.setUrlPDF(premios.getPdfURL());
        resumen.setEstado(EstadoSorteo.get(premios.getStatus()));
        return resumen;
    }

    public static ResumenNino get(String urlBase, SorteoNavidadResponse sorteo) {
        ResumenNino resumen = new ResumenNino();
        if (sorteo == null) {
            return resumen;
        }
        String base = urlBase == null ? "" : urlBase;
        resumen.setPrimero(SorteoResponseConverterUtils.formatDecimo(getDecimo(sorteo.getPrimerPremio())));
        resumen.setSegundo(SorteoResponseConverterUtils.formatDecimo(getDecimo(sorteo.getSegundoPremio())));
        resumen.setTercero(SorteoResponseConverterUtils.formatDecimo(
                getDecimo(SorteoResponseConverterUtils.getFirst(sorteo.getTercerosPremios()))));
        resumen.setCuatroCifras(new ArrayList<>(
                SorteoResponseConverterUtils.extractDecimos(sorteo.getExtraccionesDeCuatroCifras(), false)));
        resumen.setTresCifras(new ArrayList<>(
                SorteoResponseConverterUtils.extractDecimos(sorteo.getExtraccionesDeTresCifras(), false)));
        resumen.setDosCifras(new ArrayList<>(
                SorteoResponseConverterUtils.extractDecimos(sorteo.getExtraccionesDeDosCifras(), false)));
        resumen.setReintegros(new ArrayList<>(
                SorteoResponseConverterUtils.extractDecimos(sorteo.getReintegros(), false)));
        setFechaActualizacion(sorteo.getFechaSorteo(), resumen);
        resumen.setUrlPDF(base + sorteo.getUrlListadoOficial());
        resumen.setEstado(SorteoResponseConverterUtils.getEstado(sorteo.getEstado()));
        return resumen;
    }

    private static void setFechaActualizacion(Premios premios, ResumenNino resumen) {
        try {
            resumen.setFechaActualizacion(LocalDateTime.ofInstant(Instant.ofEpochSecond(premios.getTimestamp()), ZoneId
                    .systemDefault()));
        } catch (NoClassDefFoundError n) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(premios.getTimestamp() * 1000L);
            resumen.setFechaActualizacionAndroid(date.getTime());
        }
    }

    private static void setFechaActualizacion(String fechaSorteo, ResumenNino resumen) {
        SorteoResponseConverterUtils.setFechaActualizacion(
                fechaSorteo,
                resumen::setFechaActualizacion,
                resumen::setFechaActualizacionAndroid
        );
    }

    private static String getDecimo(SorteoNavidadResponse.PremioDetalle premio) {
        return premio == null ? null : premio.getDecimo();
    }
}
