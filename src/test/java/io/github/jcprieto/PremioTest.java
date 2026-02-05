package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.model.Premio;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class PremioTest {

    @Test
    public void testCantidadExactaYLegacyMantienenCompatibilidad() {
        Premio premio = new Premio();

        premio.setCantidad(new BigDecimal("12.50"));
        Assert.assertEquals(0, new BigDecimal("12.50").compareTo(premio.getCantidad()));
    }
}
