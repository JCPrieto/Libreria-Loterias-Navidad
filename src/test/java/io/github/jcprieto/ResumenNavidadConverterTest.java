package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.converter.ResumenNavidadConverter;
import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.model.json.navidad.Premios;
import io.github.jcprieto.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ResumenNavidadConverterTest {

    @Test
    public void testGetDesdePremios() {
        Premios premios = new Premios();
        premios.setNumero1(1);
        premios.setNumero2(22);
        premios.setNumero3(333);
        premios.setNumero4(4444);
        premios.setNumero5(5555);
        premios.setNumero6(6666);
        premios.setNumero7(7777);
        premios.setNumero8(-1);
        premios.setTimestamp(1700000000L);
        premios.setStatus(1);
        premios.setListaPDF("http://pdf");

        ResumenNavidad resumen = ResumenNavidadConverter.get(premios);

        Assert.assertEquals("00001", resumen.getGordo());
        Assert.assertEquals("00022", resumen.getSegundo());
        Assert.assertEquals("00333", resumen.getTercero());
        Assert.assertEquals(2, resumen.getCuarto().size());
        Assert.assertEquals("04444", resumen.getCuarto().get(0));
        Assert.assertEquals("05555", resumen.getCuarto().get(1));
        Assert.assertEquals(2, resumen.getQuinto().size());
        Assert.assertEquals("06666", resumen.getQuinto().get(0));
        Assert.assertEquals("07777", resumen.getQuinto().get(1));
        Assert.assertEquals("http://pdf", resumen.getUrlPDF());
        Assert.assertEquals(EstadoSorteo.EN_PROCESO, resumen.getEstado());
        Assert.assertNotNull(resumen.getFechaActualizacion());
    }

    @Test
    public void testGetDesdePremiosSinNumerosDisponibles() {
        Premios premios = new Premios();
        premios.setNumero1(-1);
        premios.setNumero2(-1);
        premios.setNumero3(-1);
        premios.setNumero4(-1);
        premios.setNumero5(-1);
        premios.setNumero6(-1);
        premios.setNumero7(-1);
        premios.setNumero8(-1);
        premios.setNumero9(-1);
        premios.setNumero10(-1);
        premios.setNumero11(-1);
        premios.setNumero12(-1);
        premios.setNumero13(-1);

        ResumenNavidad resumen = ResumenNavidadConverter.get(premios);

        Assert.assertNull(resumen.getGordo());
        Assert.assertNull(resumen.getSegundo());
        Assert.assertNull(resumen.getTercero());
        Assert.assertTrue(resumen.getCuarto().isEmpty());
        Assert.assertTrue(resumen.getQuinto().isEmpty());
    }

    @Test
    public void testGetDesdePremiosConUnSoloCuartoDisponible() {
        Premios premios = new Premios();
        premios.setNumero4(4444);
        premios.setNumero5(-1);
        premios.setNumero6(-1);

        ResumenNavidad resumen = ResumenNavidadConverter.get(premios);

        Assert.assertEquals(1, resumen.getCuarto().size());
        Assert.assertEquals("04444", resumen.getCuarto().getFirst());
        Assert.assertTrue(resumen.getQuinto().isEmpty());
    }

    @Test
    public void testGetDesdePremiosConTodosLosQuintosDisponibles() {
        Premios premios = new Premios();
        premios.setNumero6(6);
        premios.setNumero7(7);
        premios.setNumero8(8);
        premios.setNumero9(9);
        premios.setNumero10(10);
        premios.setNumero11(11);
        premios.setNumero12(12);
        premios.setNumero13(13);

        ResumenNavidad resumen = ResumenNavidadConverter.get(premios);

        Assert.assertEquals(8, resumen.getQuinto().size());
        Assert.assertEquals("00006", resumen.getQuinto().get(0));
        Assert.assertEquals("00013", resumen.getQuinto().get(7));
    }

    @Test
    public void testGetDesdeSorteoNavidadResponse() {
        SorteoNavidadResponse sorteo = new SorteoNavidadResponse();
        sorteo.setFechaSorteo("2025-12-22 08:30:00");
        sorteo.setEstado("cerrado");
        sorteo.setUrlListadoOficial("/f/listado.pdf");
        sorteo.setPrimerPremio(premio("79432"));
        sorteo.setSegundoPremio(premio("70048"));
        List<SorteoNavidadResponse.PremioDetalle> terceros = new ArrayList<>();
        terceros.add(premio("90693"));
        sorteo.setTercerosPremios(terceros);
        sorteo.setCuartosPremios(List.of(premio("25508"), premio("78477")));
        sorteo.setQuintosPremios(List.of(premio("23112"), premio("25412")));

        ResumenNavidad resumen = ResumenNavidadConverter.get("", sorteo);

        Assert.assertEquals("79432", resumen.getGordo());
        Assert.assertEquals("70048", resumen.getSegundo());
        Assert.assertEquals("90693", resumen.getTercero());
        Assert.assertEquals(2, resumen.getCuarto().size());
        Assert.assertEquals("25508", resumen.getCuarto().get(0));
        Assert.assertEquals("78477", resumen.getCuarto().get(1));
        Assert.assertEquals(2, resumen.getQuinto().size());
        Assert.assertEquals("23112", resumen.getQuinto().get(0));
        Assert.assertEquals("25412", resumen.getQuinto().get(1));
        Assert.assertEquals("/f/listado.pdf", resumen.getUrlPDF());
        Assert.assertEquals(EstadoSorteo.TERMINADO, resumen.getEstado());
        Assert.assertNotNull(resumen.getFechaActualizacion());
    }

    @Test
    public void testGetDesdeSorteoNavidadResponseNullDevuelveResumenVacio() {
        ResumenNavidad resumen = ResumenNavidadConverter.get("https://www.loteriasyapuestas.es", null);

        Assert.assertNull(resumen.getGordo());
        Assert.assertNull(resumen.getSegundo());
        Assert.assertNull(resumen.getTercero());
        Assert.assertNull(resumen.getCuarto());
        Assert.assertNull(resumen.getQuinto());
        Assert.assertNull(resumen.getUrlPDF());
        Assert.assertNull(resumen.getEstado());
    }

    @Test
    public void testGetDesdeSorteoNavidadResponseConDatosOpcionalesAusentes() {
        SorteoNavidadResponse sorteo = new SorteoNavidadResponse();
        sorteo.setUrlListadoOficial("/f/listado.pdf");
        sorteo.setTercerosPremios(List.of());
        List<SorteoNavidadResponse.PremioDetalle> cuartos = new ArrayList<>();
        cuartos.add(null);
        cuartos.add(premio(null));
        cuartos.add(premio("abc"));
        sorteo.setCuartosPremios(cuartos);
        sorteo.setQuintosPremios(null);

        ResumenNavidad resumen = ResumenNavidadConverter.get(null, sorteo);

        Assert.assertNull(resumen.getGordo());
        Assert.assertNull(resumen.getSegundo());
        Assert.assertNull(resumen.getTercero());
        Assert.assertTrue(resumen.getCuarto().isEmpty());
        Assert.assertTrue(resumen.getQuinto().isEmpty());
        Assert.assertEquals("/f/listado.pdf", resumen.getUrlPDF());
        Assert.assertNull(resumen.getEstado());
        Assert.assertNull(resumen.getFechaActualizacion());
    }

    @Test
    public void testGetDesdeSorteoNavidadResponseConRellenoDeCeros() {
        SorteoNavidadResponse sorteo = new SorteoNavidadResponse();
        sorteo.setPrimerPremio(premio("123"));
        sorteo.setSegundoPremio(premio("7"));
        sorteo.setTercerosPremios(List.of(premio("45")));

        ResumenNavidad resumen = ResumenNavidadConverter.get("", sorteo);

        Assert.assertEquals("00123", resumen.getGordo());
        Assert.assertEquals("00007", resumen.getSegundo());
        Assert.assertEquals("00045", resumen.getTercero());
    }

    private SorteoNavidadResponse.PremioDetalle premio(String decimo) {
        SorteoNavidadResponse.PremioDetalle premio = new SorteoNavidadResponse.PremioDetalle();
        premio.setDecimo(decimo);
        return premio;
    }
}
