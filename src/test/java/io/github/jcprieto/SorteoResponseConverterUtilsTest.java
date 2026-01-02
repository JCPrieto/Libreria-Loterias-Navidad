package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.converter.SorteoResponseConverterUtils;
import io.github.jcprieto.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    private SorteoNavidadResponse.PremioDetalle premio(String decimo) {
        SorteoNavidadResponse.PremioDetalle premio = new SorteoNavidadResponse.PremioDetalle();
        premio.setDecimo(decimo);
        return premio;
    }
}
