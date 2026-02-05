package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.converter.PremioConverter;
import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.model.Premio;
import io.github.jcprieto.lib.loteria.model.json.Busqueda;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class PremioConverterTest {

    @Test
    public void testGetDesdeBusqueda() {
        Busqueda busqueda = new Busqueda();
        busqueda.setPremio(2000);
        busqueda.setTimestamp(1700000000L);
        busqueda.setStatus(3);

        Premio premio = PremioConverter.get(busqueda);

        Assert.assertEquals(new BigDecimal("100"), premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.TERMINADO, premio.getEstado());
        LocalDateTime esperado = LocalDateTime.ofInstant(Instant.ofEpochSecond(1700000000L),
                ZoneId.of("Europe/Madrid"));
        Assert.assertEquals(esperado, premio.getFechaActualizacion());
    }

    @Test
    public void testGetDesdeBusquedaSinPremio() {
        Busqueda busqueda = new Busqueda();
        busqueda.setPremio(0);
        busqueda.setTimestamp(1700000000L);
        busqueda.setStatus(1);

        Premio premio = PremioConverter.get(busqueda);

        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.EN_PROCESO, premio.getEstado());
        Assert.assertNotNull(premio.getFechaActualizacion());
    }

    @Test
    public void testGetDesdeSorteo() {
        Premio premio = PremioConverter.get("cerrado", "2025-12-22 08:30:00", 12000L, 200);

        Assert.assertEquals(new BigDecimal("60"), premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.TERMINADO, premio.getEstado());
        Assert.assertEquals(LocalDateTime.of(2025, 12, 22, 8, 30, 0), premio.getFechaActualizacion());
    }

    @Test
    public void testGetDesdeSorteoSinImporte() {
        Premio premio = PremioConverter.get("abierto", "2025-12-22 08:30:00", 12000L, 0);

        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.EN_PROCESO, premio.getEstado());
        Assert.assertNotNull(premio.getFechaActualizacion());
    }

    @Test
    public void testGetDesdeSorteoConImporteNegativo() {
        Premio premio = PremioConverter.get("cerrado", "2025-12-22 08:30:00", 12000L, -5);

        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.TERMINADO, premio.getEstado());
        Assert.assertNotNull(premio.getFechaActualizacion());
    }

    @Test
    public void testGetDesdeSorteoConDivisionNoExacta() {
        Premio premio = PremioConverter.get("cerrado", "2025-12-22 08:30:00", 1L, 3);

        Assert.assertEquals(new BigDecimal("0.33333333"), premio.getCantidad());
    }
}
