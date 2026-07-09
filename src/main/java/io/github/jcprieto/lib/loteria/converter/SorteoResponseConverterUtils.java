package io.github.jcprieto.lib.loteria.converter;

import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public final class SorteoResponseConverterUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SorteoResponseConverterUtils.class);
    private static final ZoneId MADRID_ZONE = ZoneId.of("Europe/Madrid");
    private static final int DECIMO_LENGTH = 5;

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
        return normalizeDecimo(decimo, true);
    }

    public static String normalizeDecimo(String decimo, boolean padToFive) {
        if (decimo == null || decimo.isEmpty()) {
            return null;
        }
        String trimmed = decimo.trim();
        if (trimmed.isEmpty() || !isNumeric(trimmed)) {
            return null;
        }
        String normalized = trimmed.length() > DECIMO_LENGTH
                ? trimmed.substring(trimmed.length() - DECIMO_LENGTH)
                : trimmed;
        if (!padToFive || normalized.length() >= DECIMO_LENGTH) {
            return normalized;
        }
        return "0".repeat(DECIMO_LENGTH - normalized.length()) + normalized;
    }

    public static Optional<SorteoNavidadResponse.PremioDetalle> getFirst(
            List<SorteoNavidadResponse.PremioDetalle> premios) {
        if (premios == null || premios.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(premios.getFirst());
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
            String decimo = normalizeDecimo(premio.getDecimo(), padToFive);
            if (decimo != null) {
                resultado.add(decimo);
            }
        }
        return resultado;
    }

    public static void setFechaActualizacion(String fechaSorteo, Consumer<LocalDateTime> localSetter) {
        if (fechaSorteo == null) {
            return;
        }
        try {
            localSetter.accept(LocalDateTime.parse(fechaSorteo.replace(' ', 'T')));
        } catch (RuntimeException e) {
            LOG.error("Error al parsear fecha: {}", fechaSorteo, e);
        }
    }

    public static void setFechaActualizacionFromTimestamp(long timestamp, Consumer<LocalDateTime> localSetter) {
        localSetter.accept(LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), MADRID_ZONE));
    }

    private static boolean isNumeric(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
