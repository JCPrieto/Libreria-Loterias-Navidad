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

        List<String> sinRelleno = SorteoResponseConverterUtils.extractDecimos(premios, false);
        List<String> conRelleno = SorteoResponseConverterUtils.extractDecimos(premios, true);

        Assert.assertEquals(List.of("123"), sinRelleno);
        Assert.assertEquals(List.of("00123"), conRelleno);
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
                ZoneId.systemDefault());
        Assert.assertEquals(esperado, local.get());
        Assert.assertNull(legacy.get());
    }

    private SorteoNavidadResponse.PremioDetalle premio(String decimo) {
        SorteoNavidadResponse.PremioDetalle premio = new SorteoNavidadResponse.PremioDetalle();
        premio.setDecimo(decimo);
        return premio;
    }
}
