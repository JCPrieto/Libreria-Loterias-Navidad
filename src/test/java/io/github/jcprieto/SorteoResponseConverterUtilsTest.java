package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.converter.SorteoResponseConverterUtils;
import io.github.jcprieto.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SorteoResponseConverterUtilsTest {

    @Test
    public void testExtractDecimosFiltraVacios() {
        List<SorteoNavidadResponse.PremioDetalle> premios = new ArrayList<>();
        premios.add(premio("   "));
        premios.add(premio(null));
        premios.add(premio("123"));
        premios.add(premio("12A45"));

        List<String> sinRelleno = SorteoResponseConverterUtils.extractDecimos(premios, false);
        List<String> conRelleno = SorteoResponseConverterUtils.extractDecimos(premios, true);

        Assert.assertEquals(List.of("123"), sinRelleno);
        Assert.assertEquals(List.of("00123"), conRelleno);
    }

    @Test
    public void testNormalizeDecimoRecortaYPaddea() {
        Assert.assertEquals("12345", SorteoResponseConverterUtils.normalizeDecimo("12345", true));
        Assert.assertEquals("01234", SorteoResponseConverterUtils.normalizeDecimo("1234", true));
        Assert.assertEquals("23456", SorteoResponseConverterUtils.normalizeDecimo("00123456", true));
        Assert.assertEquals("23456", SorteoResponseConverterUtils.normalizeDecimo("00123456", false));
        Assert.assertNull(SorteoResponseConverterUtils.normalizeDecimo("12A45", true));
        Assert.assertNull(SorteoResponseConverterUtils.normalizeDecimo("   ", true));
    }

    @Test
    public void testSetFechaActualizacionFromTimestamp() {
        AtomicReference<LocalDateTime> local = new AtomicReference<>();
        long timestamp = 1700000000L;

        SorteoResponseConverterUtils.setFechaActualizacionFromTimestamp(
                timestamp,
                local::set
        );

        LocalDateTime esperado = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp),
                ZoneId.of("Europe/Madrid"));
        Assert.assertEquals(esperado, local.get());
    }

    @Test
    public void testExtractDecimosConListaNulaOVacia() {
        Assert.assertTrue(SorteoResponseConverterUtils.extractDecimos(null, false).isEmpty());
        Assert.assertTrue(SorteoResponseConverterUtils.extractDecimos(List.of(), true).isEmpty());
    }

    @Test
    public void testExtractDecimosIgnoraElementoNulo() {
        List<SorteoNavidadResponse.PremioDetalle> premios = Arrays.asList(
                null,
                premio("7")
        );
        Assert.assertEquals(List.of("00007"), SorteoResponseConverterUtils.extractDecimos(premios, true));
    }

    @Test
    public void testGetEstadoMapeaValoresEsperados() {
        Assert.assertEquals(io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo.EN_PROCESO,
                SorteoResponseConverterUtils.getEstado("abierto"));
        Assert.assertEquals(io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo.TERMINADO,
                SorteoResponseConverterUtils.getEstado(" CERRADO "));
        Assert.assertEquals(io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo.NO_INICIADO,
                SorteoResponseConverterUtils.getEstado("Pendiente"));
        Assert.assertNull(SorteoResponseConverterUtils.getEstado("desconocido"));
        Assert.assertNull(SorteoResponseConverterUtils.getEstado(null));
    }

    @Test
    public void testFormatDecimoDelegadoANormalize() {
        Assert.assertEquals("00007", SorteoResponseConverterUtils.formatDecimo("7"));
        Assert.assertNull(SorteoResponseConverterUtils.formatDecimo(null));
        Assert.assertNull(SorteoResponseConverterUtils.formatDecimo(""));
    }

    @Test
    public void testGetFirstConNuloVacioYPrimerElementoNulo() {
        Assert.assertTrue(SorteoResponseConverterUtils.getFirst(null).isEmpty());
        Assert.assertTrue(SorteoResponseConverterUtils.getFirst(List.of()).isEmpty());
        Assert.assertTrue(SorteoResponseConverterUtils.getFirst(Arrays.asList(null, premio("12345"))).isEmpty());
        Assert.assertEquals("12345",
                SorteoResponseConverterUtils.getFirst(List.of(premio("12345"))).orElseThrow().getDecimo());
    }

    @Test
    public void testSetFechaActualizacionConFechaNulaNoHaceNada() {
        AtomicReference<LocalDateTime> local = new AtomicReference<>();

        SorteoResponseConverterUtils.setFechaActualizacion(null, local::set);

        Assert.assertNull(local.get());
    }

    @Test
    public void testSetFechaActualizacionConFormatoValidoSeteaLocalDateTime() {
        AtomicReference<LocalDateTime> local = new AtomicReference<>();

        SorteoResponseConverterUtils.setFechaActualizacion("2025-12-22 08:30:00", local::set);

        Assert.assertEquals(LocalDateTime.of(2025, 12, 22, 8, 30, 0), local.get());
    }

    @Test
    public void testSetFechaActualizacionConFormatoInvalidoNoSeteaCampos() {
        AtomicReference<LocalDateTime> local = new AtomicReference<>();

        SorteoResponseConverterUtils.setFechaActualizacion("2025/12/22", local::set);

        Assert.assertNull(local.get());
    }

    private SorteoNavidadResponse.PremioDetalle premio(String decimo) {
        SorteoNavidadResponse.PremioDetalle premio = new SorteoNavidadResponse.PremioDetalle();
        premio.setDecimo(decimo);
        return premio;
    }
}
