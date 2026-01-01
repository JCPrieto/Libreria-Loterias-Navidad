package es.jklabs;

import es.jklabs.lib.loteria.converter.ResumenNinoConverter;
import es.jklabs.lib.loteria.enumeradores.EstadoSorteo;
import es.jklabs.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import es.jklabs.lib.loteria.model.json.nino.Premios;
import es.jklabs.lib.loteria.model.nino.ResumenNino;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ResumenNinoConverterTest {

    @Test
    public void testGetDesdePremios() {
        Premios premios = new Premios();
        premios.setPremio1(1);
        premios.setPremio2(22);
        premios.setPremio3(333);
        premios.setExtracciones4cifras(new String[]{"1234", "-1"});
        premios.setExtracciones3cifras(new String[]{"234", "-1"});
        premios.setExtracciones2cifras(new String[]{"12", "-1"});
        premios.setReintegros(new String[]{"1", "-1"});
        premios.setTimestamp(1700000000L);
        premios.setStatus(3);
        premios.setPdfURL("http://pdf");

        ResumenNino resumen = ResumenNinoConverter.get(premios);

        Assert.assertEquals("00001", resumen.getPrimero());
        Assert.assertEquals("00022", resumen.getSegundo());
        Assert.assertEquals("00333", resumen.getTercero());
        Assert.assertEquals(List.of("1234"), resumen.getCuatroCifras());
        Assert.assertEquals(List.of("234"), resumen.getTresCifras());
        Assert.assertEquals(List.of("12"), resumen.getDosCifras());
        Assert.assertEquals(List.of("1"), resumen.getReintegros());
        Assert.assertEquals("http://pdf", resumen.getUrlPDF());
        Assert.assertEquals(EstadoSorteo.TERMINADO, resumen.getEstado());
        Assert.assertNotNull(resumen.getFechaActualizacion());
    }

    @Test
    public void testGetDesdeSorteoNavidadResponse() {
        SorteoNavidadResponse sorteo = new SorteoNavidadResponse();
        sorteo.setFechaSorteo("2025-01-06 12:00:00");
        sorteo.setEstado("cerrado");
        sorteo.setUrlListadoOficial("/f/listado.pdf");
        sorteo.setPrimerPremio(premio("78908"));
        sorteo.setSegundoPremio(premio("06766"));
        sorteo.setTercerosPremios(List.of(premio("66777")));
        sorteo.setExtraccionesDeCuatroCifras(List.of(premio("1454"), premio("4276")));
        sorteo.setExtraccionesDeTresCifras(List.of(premio("040"), premio("184")));
        sorteo.setExtraccionesDeDosCifras(List.of(premio("11"), premio("26")));
        sorteo.setReintegros(List.of(premio("0"), premio("5"), premio("8")));

        ResumenNino resumen = ResumenNinoConverter.get("https://www.loteriasyapuestas.es", sorteo);

        Assert.assertEquals("78908", resumen.getPrimero());
        Assert.assertEquals("06766", resumen.getSegundo());
        Assert.assertEquals("66777", resumen.getTercero());
        Assert.assertEquals(List.of("1454", "4276"), resumen.getCuatroCifras());
        Assert.assertEquals(List.of("040", "184"), resumen.getTresCifras());
        Assert.assertEquals(List.of("11", "26"), resumen.getDosCifras());
        Assert.assertEquals(List.of("0", "5", "8"), resumen.getReintegros());
        Assert.assertEquals("https://www.loteriasyapuestas.es/f/listado.pdf", resumen.getUrlPDF());
        Assert.assertEquals(EstadoSorteo.TERMINADO, resumen.getEstado());
        Assert.assertNotNull(resumen.getFechaActualizacion());
    }

    @Test
    public void testGetDesdeSorteoNavidadResponseConRellenoDeCeros() {
        SorteoNavidadResponse sorteo = new SorteoNavidadResponse();
        sorteo.setPrimerPremio(premio("123"));
        sorteo.setSegundoPremio(premio("7"));
        sorteo.setTercerosPremios(List.of(premio("45")));

        ResumenNino resumen = ResumenNinoConverter.get("", sorteo);

        Assert.assertEquals("00123", resumen.getPrimero());
        Assert.assertEquals("00007", resumen.getSegundo());
        Assert.assertEquals("00045", resumen.getTercero());
    }

    private SorteoNavidadResponse.PremioDetalle premio(String decimo) {
        SorteoNavidadResponse.PremioDetalle premio = new SorteoNavidadResponse.PremioDetalle();
        premio.setDecimo(decimo);
        return premio;
    }
}
