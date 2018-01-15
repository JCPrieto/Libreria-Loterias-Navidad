package es.jklabs.lib.loteria.converter;

import es.jklabs.lib.loteria.enumeradores.EstadoSorteo;
import es.jklabs.lib.loteria.model.Premio;
import es.jklabs.lib.loteria.model.json.Busqueda;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

public class PremioConverter {

    private PremioConverter() {

    }

    public static Premio get(Busqueda busqueda) {
        Premio premio = new Premio();
        if (busqueda.getPremio() != 0) {
            premio.setCantidad(busqueda.getPremio() / 20D);
        } else {
            premio.setCantidad(0D);
        }
        try {
            premio.setFechaActualizacion(LocalDateTime.ofInstant(Instant.ofEpochSecond(busqueda.getTimestamp()),
                    ZoneId.of("Europe/Madrid")));
        } catch (NoClassDefFoundError n) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(busqueda.getTimestamp() * 1000L);
            premio.setFechaActualizacionAndroid(date.getTime());
        }
        premio.setEstado(EstadoSorteo.get(busqueda.getStatus()));
        return premio;
    }
}
