package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import org.junit.Assert;
import org.junit.Test;

public class EstadoSorteoTest {

    @Test
    public void testGetDevuelveEstadoEsperadoParaStatusValidos() {
        Assert.assertEquals(EstadoSorteo.NO_INICIADO, EstadoSorteo.get(0));
        Assert.assertEquals(EstadoSorteo.EN_PROCESO, EstadoSorteo.get(1));
        Assert.assertEquals(EstadoSorteo.TERMINADO_PROVISIONAL, EstadoSorteo.get(2));
        Assert.assertEquals(EstadoSorteo.TERMINADO, EstadoSorteo.get(3));
        Assert.assertEquals(EstadoSorteo.TERMINADO_OFICIAL, EstadoSorteo.get(4));
    }

    @Test
    public void testGetDevuelveNullParaStatusNoSoportados() {
        Assert.assertNull(EstadoSorteo.get(-1));
        Assert.assertNull(EstadoSorteo.get(5));
        Assert.assertNull(EstadoSorteo.get(999));
    }
}
