package es.jklabs.lib.loteria.converter;

import es.jklabs.lib.loteria.enumeradores.EstadoSorteo;
import es.jklabs.lib.loteria.model.json.navidad.Premios;
import es.jklabs.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import es.jklabs.lib.loteria.model.navidad.ResumenNavidad;
import es.jklabs.utilidades.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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
        resumen.setGordo(formatDecimo(getDecimo(sorteo.getPrimerPremio())));
        resumen.setSegundo(formatDecimo(getDecimo(sorteo.getSegundoPremio())));
        resumen.setTercero(formatDecimo(getDecimo(getFirst(sorteo.getTercerosPremios()))));
        resumen.setCuarto(new ArrayList<>());
        for (SorteoNavidadResponse.PremioDetalle premio : safeList(sorteo.getCuartosPremios())) {
            String decimo = formatDecimo(getDecimo(premio));
            if (decimo != null) {
                resumen.getCuarto().add(decimo);
            }
        }
        resumen.setQuinto(new ArrayList<>());
        for (SorteoNavidadResponse.PremioDetalle premio : safeList(sorteo.getQuintosPremios())) {
            String decimo = formatDecimo(getDecimo(premio));
            if (decimo != null) {
                resumen.getQuinto().add(decimo);
            }
        }
        setFechaActualizacion(sorteo.getFechaSorteo(), resumen);
        resumen.setUrlPDF(urlBase + sorteo.getUrlListadoOficial());
        resumen.setEstado(getEstado(sorteo.getEstado()));
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
        if (fechaSorteo == null) {
            return;
        }
        try {
            resumen.setFechaActualizacion(LocalDateTime.parse(fechaSorteo.replace(' ', 'T')));
        } catch (NoClassDefFoundError n) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).parse(fechaSorteo);
                if (date != null) {
                    resumen.setFechaActualizacionAndroid(date);
                }
            } catch (ParseException ignored) {
                //
            }
        } catch (RuntimeException ignored) {
            //
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

    private static EstadoSorteo getEstado(String estado) {
        if (estado == null) {
            return null;
        }
        String normalizado = estado.trim().toLowerCase(Locale.ROOT);
        return switch (normalizado) {
            case "abierto" -> EstadoSorteo.EN_PROCESO;
            case "cerrado" -> EstadoSorteo.TERMINADO;
            case "pendiente" -> EstadoSorteo.NO_INICIADO;
            default -> null;
        };
    }

    private static String getDecimo(SorteoNavidadResponse.PremioDetalle premio) {
        return premio == null ? null : premio.getDecimo();
    }

    private static SorteoNavidadResponse.PremioDetalle getFirst(List<SorteoNavidadResponse.PremioDetalle> premios) {
        if (premios == null || premios.isEmpty()) {
            return null;
        }
        return premios.getFirst();
    }

    private static List<SorteoNavidadResponse.PremioDetalle> safeList(List<SorteoNavidadResponse.PremioDetalle> premios) {
        return premios == null ? new ArrayList<>() : premios;
    }

    private static String formatDecimo(String decimo) {
        if (decimo == null || decimo.isEmpty()) {
            return null;
        }
        if (decimo.length() >= 5) {
            return decimo;
        }
        return "0".repeat(5 - decimo.length()) +
                decimo;
    }
}
