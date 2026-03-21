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

    @Test
    public void testCantidadNullSeConvierteACero() {
        Premio premio = new Premio();

        premio.setCantidad(null);

        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
    }

    @Test
    public void testCantidadCeroConEscalaSeNormalizaABigDecimalZero() {
        Premio premio = new Premio();

        premio.setCantidad(new BigDecimal("0.00"));

        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
    }

    @Test
    public void testCantidadEliminaCerosFinalesSinPerderValor() {
        Premio premio = new Premio();

        premio.setCantidad(new BigDecimal("12.5000"));

        Assert.assertEquals(0, new BigDecimal("12.5").compareTo(premio.getCantidad()));
    }

    @Test
    public void testCantidadConEscalaNegativaSeConvierteAEnteroSinEscalaNegativa() {
        Premio premio = new Premio();

        premio.setCantidad(new BigDecimal("1000"));

        Assert.assertEquals(new BigDecimal("1000"), premio.getCantidad());
        Assert.assertEquals(0, premio.getCantidad().scale());
    }
}
