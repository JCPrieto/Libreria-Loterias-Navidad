package io.github.jcprieto.lib.loteria.converter;

import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.model.json.navidad.Premios;
import io.github.jcprieto.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;

public class ResumenNavidadConverter {

    private ResumenNavidadConverter() {
    }

    public static ResumenNavidad get(Premios premios) {
        ResumenNavidad resumen = new ResumenNavidad();
        if (premios.getNumero1() > -1) {
            resumen.setGordo(String.format("%05d", premios.getNumero1()));
        }
        if (premios.getNumero2() > -1) {
            resumen.setSegundo(String.format("%05d", premios.getNumero2()));
        }
        if (premios.getNumero3() > -1) {
            resumen.setTercero(String.format("%05d", premios.getNumero3()));
        }
        resumen.setCuarto(new ArrayList<>());
        if (premios.getNumero4() > -1) {
            resumen.getCuarto().add(String.format("%05d", premios.getNumero4()));
            if (premios.getNumero5() > -1) {
                resumen.getCuarto().add(String.format("%05d", premios.getNumero5()));
            }
        }
        resumen.setQuinto(new ArrayList<>());
        getQuintoPremio(premios, resumen);
        setFechaActualizacion(premios, resumen);
        resumen.setUrlPDF(premios.getListaPDF());
        resumen.setEstado(EstadoSorteo.get(premios.getStatus()));
        return resumen;
    }

    public static ResumenNavidad get(String urlBase, SorteoNavidadResponse sorteo) {
        ResumenNavidad resumen = new ResumenNavidad();
        if (sorteo == null) {
            return resumen;
        }
        String base = urlBase == null ? "" : urlBase;
        resumen.setGordo(SorteoResponseConverterUtils.formatDecimo(getDecimo(sorteo.getPrimerPremio())));
        resumen.setSegundo(SorteoResponseConverterUtils.formatDecimo(getDecimo(sorteo.getSegundoPremio())));
        SorteoNavidadResponse.PremioDetalle tercero = SorteoResponseConverterUtils
                .getFirst(sorteo.getTercerosPremios())
                .orElse(null);
        resumen.setTercero(SorteoResponseConverterUtils.formatDecimo(getDecimo(tercero)));
        resumen.setCuarto(new ArrayList<>());
        resumen.getCuarto().addAll(SorteoResponseConverterUtils.extractDecimos(sorteo.getCuartosPremios(), true));
        resumen.setQuinto(new ArrayList<>());
        resumen.getQuinto().addAll(SorteoResponseConverterUtils.extractDecimos(sorteo.getQuintosPremios(), true));
        setFechaActualizacion(sorteo.getFechaSorteo(), resumen);
        resumen.setUrlPDF(base + sorteo.getUrlListadoOficial());
        resumen.setEstado(SorteoResponseConverterUtils.getEstado(sorteo.getEstado()));
        return resumen;
    }

    private static void setFechaActualizacion(Premios premios, ResumenNavidad resumen) {
        try {
            resumen.setFechaActualizacion(LocalDateTime.ofInstant(Instant.ofEpochSecond(premios.getTimestamp()), ZoneId
                    .systemDefault()));
        } catch (NoClassDefFoundError n) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(premios.getTimestamp() * 1000L);
            resumen.setFechaActualizacionAndroid(date.getTime());
        }
    }

    private static void setFechaActualizacion(String fechaSorteo, ResumenNavidad resumen) {
        SorteoResponseConverterUtils.setFechaActualizacion(
                fechaSorteo,
                resumen::setFechaActualizacion,
                resumen::setFechaActualizacionAndroid
        );
    }

    private static void getQuintoPremio(Premios premios, ResumenNavidad resumen) {
        int[] numeros = {
                premios.getNumero6(),
                premios.getNumero7(),
                premios.getNumero8(),
                premios.getNumero9(),
                premios.getNumero10(),
                premios.getNumero11(),
                premios.getNumero12(),
                premios.getNumero13()
        };
        for (int numero : numeros) {
            if (numero > -1) {
                resumen.getQuinto().add(String.format("%05d", numero));
            } else {
                break;
            }
        }
    }

    private static String getDecimo(SorteoNavidadResponse.PremioDetalle premio) {
        return premio == null ? null : premio.getDecimo();
    }
}
