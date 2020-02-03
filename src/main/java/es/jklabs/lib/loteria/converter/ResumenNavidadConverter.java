package es.jklabs.lib.loteria.converter;

import es.jklabs.lib.loteria.enumeradores.EstadoSorteo;
import es.jklabs.lib.loteria.model.json.navidad.Premios;
import es.jklabs.lib.loteria.model.navidad.ResumenNavidad;
import es.jklabs.utilidades.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        int i = 6;
        boolean fin = false;
        while (i <= 13 && !fin) {
            try {
                Method method = Premios.class.getDeclaredMethod("getNumero" + i);
                int valor = (int) method.invoke(premios);
                if (valor > -1) {
                    resumen.getQuinto().add(String.format("%05d", valor));
                    i++;
                } else {
                    fin = true;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                fin = true;
                Logger.error(e);
            }
        }
    }
}
