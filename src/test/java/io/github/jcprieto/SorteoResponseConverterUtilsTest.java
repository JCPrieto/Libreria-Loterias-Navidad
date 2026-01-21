package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.converter.SorteoResponseConverterUtils;
import io.github.jcprieto.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
        AtomicReference<java.util.Date> legacy = new AtomicReference<>();
        long timestamp = 1700000000L;

        SorteoResponseConverterUtils.setFechaActualizacionFromTimestamp(
                timestamp,
                local::set,
                legacy::set
        );

        LocalDateTime esperado = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp),
                ZoneId.of("Europe/Madrid"));
        Assert.assertEquals(esperado, local.get());
        Assert.assertNull(legacy.get());
    }

    @Test
    public void testExtractDecimosConListaNulaOVacia() {
        Assert.assertTrue(SorteoResponseConverterUtils.extractDecimos(null, false).isEmpty());
        Assert.assertTrue(SorteoResponseConverterUtils.extractDecimos(List.of(), true).isEmpty());
    }

    private SorteoNavidadResponse.PremioDetalle premio(String decimo) {
        SorteoNavidadResponse.PremioDetalle premio = new SorteoNavidadResponse.PremioDetalle();
        premio.setDecimo(decimo);
        return premio;
    }
}
