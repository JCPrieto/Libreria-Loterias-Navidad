package es.jklabs.lib.loteria.converter;

import es.jklabs.lib.loteria.enumeradores.EstadoSorteo;
import es.jklabs.lib.loteria.model.json.navidad.SorteoNavidadResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class SorteoResponseConverterUtils {

    private SorteoResponseConverterUtils() {
    }

    public static EstadoSorteo getEstado(String estado) {
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

    public static String formatDecimo(String decimo) {
        if (decimo == null || decimo.isEmpty()) {
            return null;
        }
        if (decimo.length() >= 5) {
            return decimo;
        }
        return "0".repeat(5 - decimo.length()) +
                decimo;
    }

    public static SorteoNavidadResponse.PremioDetalle getFirst(List<SorteoNavidadResponse.PremioDetalle> premios) {
        if (premios == null || premios.isEmpty()) {
            return null;
        }
        return premios.getFirst();
    }

    public static List<String> extractDecimos(List<SorteoNavidadResponse.PremioDetalle> premios, boolean padToFive) {
        List<String> resultado = new ArrayList<>();
        if (premios == null) {
            return resultado;
        }
        for (SorteoNavidadResponse.PremioDetalle premio : premios) {
            if (premio == null || premio.getDecimo() == null) {
                continue;
            }
            String decimo = padToFive ? formatDecimo(premio.getDecimo()) : premio.getDecimo();
            if (decimo != null) {
                resultado.add(decimo);
            }
        }
        return resultado;
    }

    public static void setFechaActualizacion(String fechaSorteo, Consumer<LocalDateTime> localSetter,
                                             Consumer<Date> dateSetter) {
        if (fechaSorteo == null) {
            return;
        }
        try {
            localSetter.accept(LocalDateTime.parse(fechaSorteo.replace(' ', 'T')));
        } catch (NoClassDefFoundError n) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).parse(fechaSorteo);
                if (date != null) {
                    dateSetter.accept(date);
                }
            } catch (ParseException ignored) {
                //
            }
        } catch (RuntimeException ignored) {
            //
        }
    }
}
