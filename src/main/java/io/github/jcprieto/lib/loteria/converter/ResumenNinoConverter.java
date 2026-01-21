package io.github.jcprieto.lib.loteria.converter;

import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import io.github.jcprieto.lib.loteria.model.json.nino.Premios;
import io.github.jcprieto.lib.loteria.model.nino.ResumenNino;

import java.util.ArrayList;

public class ResumenNinoConverter {

    private static final int PREMIO_NO_DISPONIBLE = -1;
    private static final String EXTRACCION_NO_DISPONIBLE = "-1";

    private ResumenNinoConverter() {

    }

    public static ResumenNino get(Premios premios) {
        ResumenNino resumen = new ResumenNino();
        if (premios.getPremio1() > PREMIO_NO_DISPONIBLE) {
            resumen.setPrimero(String.format("%05d", premios.getPremio1()));
        }
        if (premios.getPremio2() > PREMIO_NO_DISPONIBLE) {
            resumen.setSegundo(String.format("%05d", premios.getPremio2()));
        }
        if (premios.getPremio3() > PREMIO_NO_DISPONIBLE) {
            resumen.setTercero(String.format("%05d", premios.getPremio3()));
        }
        resumen.setCuatroCifras(filterExtracciones(premios.getExtracciones4cifras()));
        resumen.setTresCifras(filterExtracciones(premios.getExtracciones3cifras()));
        resumen.setDosCifras(filterExtracciones(premios.getExtracciones2cifras()));
        resumen.setReintegros(filterExtracciones(premios.getReintegros()));
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
        SorteoNavidadResponse.PremioDetalle tercero = SorteoResponseConverterUtils
                .getFirst(sorteo.getTercerosPremios())
                .orElse(null);
        resumen.setTercero(SorteoResponseConverterUtils.formatDecimo(getDecimo(tercero)));
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
        SorteoResponseConverterUtils.setFechaActualizacionFromTimestamp(
                premios.getTimestamp(),
                resumen::setFechaActualizacion,
                resumen::setFechaActualizacionAndroid
        );
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

    private static ArrayList<String> filterExtracciones(String[] extracciones) {
        ArrayList<String> resultado = new ArrayList<>();
        if (extracciones == null) {
            return resultado;
        }
        for (String extraccion : extracciones) {
            if (!EXTRACCION_NO_DISPONIBLE.equals(extraccion)) {
                resultado.add(extraccion);
            }
        }
        return resultado;
    }
}
