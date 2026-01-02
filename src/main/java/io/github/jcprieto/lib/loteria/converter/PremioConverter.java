package io.github.jcprieto.lib.loteria.converter;

import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.model.Premio;
import io.github.jcprieto.lib.loteria.model.json.Busqueda;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

public class PremioConverter {

    private static final ZoneId MADRID_ZONE = ZoneId.of("Europe/Madrid");

    private PremioConverter() {

    }

    public static Premio get(Busqueda busqueda) {
        Premio premio = new Premio();
        setCantidad(premio, busqueda.getPremio(), 20);
        setFechaActualizacionFromTimestamp(busqueda.getTimestamp(), premio);
        premio.setEstado(EstadoSorteo.get(busqueda.getStatus()));
        return premio;
    }

    public static Premio get(String estado, String fechaSorteo, long premio, int importePorDefecto) {
        Premio resultado = new Premio();
        setCantidad(resultado, premio, importePorDefecto);
        setFechaActualizacionFromSorteo(fechaSorteo, resultado);
        resultado.setEstado(SorteoResponseConverterUtils.getEstado(estado));
        return resultado;
    }

    private static void setCantidad(Premio premio, long cantidad, int divisor) {
        premio.setCantidad(calculateCantidad(cantidad, divisor));
    }

    private static double calculateCantidad(long cantidad, int divisor) {
        if (cantidad != 0 && divisor > 0) {
            return cantidad / (double) divisor;
        }
        return 0D;
    }

    private static void setFechaActualizacionFromTimestamp(long timestamp, Premio premio) {
        try {
            premio.setFechaActualizacion(LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp),
                    MADRID_ZONE));
        } catch (NoClassDefFoundError n) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(timestamp * 1000L);
            premio.setFechaActualizacionAndroid(date.getTime());
        }
    }

    private static void setFechaActualizacionFromSorteo(String fechaSorteo, Premio premio) {
        SorteoResponseConverterUtils.setFechaActualizacion(fechaSorteo, premio::setFechaActualizacion,
                premio::setFechaActualizacionAndroid);
    }
}
