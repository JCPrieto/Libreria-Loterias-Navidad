package es.jklabs.lib.loteria.converter;

import es.jklabs.lib.loteria.enumeradores.EstadoSorteo;
import es.jklabs.lib.loteria.model.json.navidad.Premios;
import es.jklabs.lib.loteria.model.navidad.ResumenNavidad;

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

    private static void getQuintoPremio(Premios premios, ResumenNavidad resumen) {
        if (premios.getNumero6() > -1) {
            resumen.getQuinto().add(String.format("%05d", premios.getNumero6()));
            if (premios.getNumero7() > -1) {
                resumen.getQuinto().add(String.format("%05d", premios.getNumero7()));
                if (premios.getNumero8() > -1) {
                    resumen.getQuinto().add(String.format("%05d", premios.getNumero8()));
                    if (premios.getNumero9() > -1) {
                        resumen.getQuinto().add(String.format("%05d", premios.getNumero9()));
                        if (premios.getNumero10() > -1) {
                            resumen.getQuinto().add(String.format("%05d", premios.getNumero10()));
                            if (premios.getNumero11() > -1) {
                                resumen.getQuinto().add(String.format("%05d", premios.getNumero11()));
                                if (premios.getNumero12() > -1) {
                                    resumen.getQuinto().add(String.format("%05d", premios.getNumero12()));
                                    if (premios.getNumero13() > -1) {
                                        resumen.getQuinto().add(String.format("%05d", premios.getNumero13()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
