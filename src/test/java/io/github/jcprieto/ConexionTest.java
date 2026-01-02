package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.conexion.Conexion;
import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.enumeradores.Sorteo;
import io.github.jcprieto.lib.loteria.excepciones.PremioDecimoNoDisponibleException;
import io.github.jcprieto.lib.loteria.model.Premio;
import io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad;
import io.github.jcprieto.lib.loteria.model.nino.ResumenNino;
import okhttp3.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class ConexionTest {

    private MockWebServer server;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void testResumenNavidad() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(resumenNavidadJson()));

        Conexion conexion = createConexion();
        ResumenNavidad resumen = conexion.getResumenNavidad();

        RecordedRequest warmupRequest = server.takeRequest();
        Assert.assertEquals("/", warmupRequest.getPath());
        RecordedRequest request = server.takeRequest();
        String fecha = getUltimoVeintidosDiciembre();
        Assert.assertEquals("/servicios/buscadorSorteos?fechaInicioInclusiva=" + fecha
                + "&fechaFinInclusiva=" + fecha
                + "&game_id=LNAC&celebrados=true", request.getPath());
        Assert.assertEquals("*/*", request.getHeader("Accept"));
        Assert.assertEquals("no-cache", request.getHeader("Cache-Control"));
        Assert.assertEquals("es-ES,es;q=0.9", request.getHeader("Accept-Language"));
        Assert.assertEquals("https://www.loteriasyapuestas.es/", request.getHeader("Referer"));
        Assert.assertNotNull(request.getHeader("User-Agent"));
        Assert.assertNotNull(resumen);
        Assert.assertEquals("79432", resumen.getGordo());
        Assert.assertEquals("70048", resumen.getSegundo());
        Assert.assertEquals("90693", resumen.getTercero());
        Assert.assertEquals(2, resumen.getCuarto().size());
        Assert.assertEquals("25508", resumen.getCuarto().get(0));
        Assert.assertEquals("78477", resumen.getCuarto().get(1));
        Assert.assertEquals(2, resumen.getQuinto().size());
        Assert.assertEquals("23112", resumen.getQuinto().get(0));
        Assert.assertEquals("25412", resumen.getQuinto().get(1));
        Assert.assertEquals("https://www.loteriasyapuestas.es/f/loterias/documentos/Lotería Nacional/listas de premios/SM_LISTAOFICIAL.A2025.S102.pdf",
                resumen.getUrlPDF());
        Assert.assertEquals(EstadoSorteo.TERMINADO, resumen.getEstado());
        Assert.assertNotNull(resumen.getFechaActualizacion());
    }

    @Test
    public void testResumenNino() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(resumenNinoJson()));

        Conexion conexion = createConexion();
        ResumenNino resumen = conexion.getResumenNino();

        RecordedRequest warmupRequest = server.takeRequest();
        Assert.assertEquals("/", warmupRequest.getPath());
        RecordedRequest request = server.takeRequest();
        String fecha = getUltimoSeisEnero();
        Assert.assertEquals("/servicios/buscadorSorteos?fechaInicioInclusiva=" + fecha
                + "&fechaFinInclusiva=" + fecha
                + "&game_id=LNAC&celebrados=true", request.getPath());
        Assert.assertNotNull(resumen);
        Assert.assertEquals("78908", resumen.getPrimero());
        Assert.assertEquals("06766", resumen.getSegundo());
        Assert.assertEquals("66777", resumen.getTercero());
        Assert.assertEquals(2, resumen.getCuatroCifras().size());
        Assert.assertEquals("1454", resumen.getCuatroCifras().get(0));
        Assert.assertEquals("4276", resumen.getCuatroCifras().get(1));
        Assert.assertEquals(2, resumen.getTresCifras().size());
        Assert.assertEquals("040", resumen.getTresCifras().get(0));
        Assert.assertEquals("184", resumen.getTresCifras().get(1));
        Assert.assertEquals(2, resumen.getDosCifras().size());
        Assert.assertEquals("11", resumen.getDosCifras().get(0));
        Assert.assertEquals("26", resumen.getDosCifras().get(1));
        Assert.assertEquals(3, resumen.getReintegros().size());
        Assert.assertEquals("0", resumen.getReintegros().get(0));
        Assert.assertEquals("5", resumen.getReintegros().get(1));
        Assert.assertEquals("8", resumen.getReintegros().get(2));
        Assert.assertEquals("https://www.loteriasyapuestas.es/f/loterias/documentos/Lotería Nacional/listas de premios/SM_LISTAOFICIAL.A2025.S002.pdf",
                resumen.getUrlPDF());
        Assert.assertEquals(EstadoSorteo.TERMINADO, resumen.getEstado());
        Assert.assertNotNull(resumen.getFechaActualizacion());
    }

    @Test
    public void testPremio() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(premioDecimoJson()));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12345");

        RecordedRequest warmupRequest = server.takeRequest();
        Assert.assertEquals("/", warmupRequest.getPath());
        RecordedRequest request = server.takeRequest();
        String fecha = getUltimoVeintidosDiciembre();
        Assert.assertEquals("/servicios/buscadorSorteosConEscrutinio?fechaInicioInclusiva=" + fecha
                + "&fechaFinInclusiva=" + fecha
                + "&game_id=LNAC&limiteMaxResultados=1", request.getPath());
        RecordedRequest premioRequest = server.takeRequest();
        Assert.assertEquals("/servicios/premioDecimoWeb?idsorteo=1295909102", premioRequest.getPath());
        Assert.assertNotNull(premio);
        Assert.assertEquals(50.0, premio.getCantidad(), 0.001);
        Assert.assertEquals(EstadoSorteo.TERMINADO, premio.getEstado());
        Assert.assertNotNull(premio.getFechaActualizacion());
    }

    @Test
    public void testPremioUsaCache() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(premioDecimoJson()));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12345");
        Premio premioCache = conexion.getPremio(Sorteo.NAVIDAD, "54321");

        Assert.assertNotNull(premio);
        Assert.assertNotNull(premioCache);
        Assert.assertEquals(3, server.getRequestCount());
    }

    @Test(expected = PremioDecimoNoDisponibleException.class)
    public void testPremioDecimoNoDisponible() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("E019"));

        Conexion conexion = createConexion();
        conexion.getPremio(Sorteo.NAVIDAD, "12345");
    }

    @Test
    public void testResumenNavidadErrorDevuelveNull() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500));

        Conexion conexion = createConexion();
        ResumenNavidad resumen = conexion.getResumenNavidad();

        Assert.assertNull(resumen);
    }

    @Test
    public void testResumenNavidadListaVaciaDevuelveNull() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[]"));

        Conexion conexion = createConexion();
        ResumenNavidad resumen = conexion.getResumenNavidad();

        Assert.assertNull(resumen);
    }

    private Conexion createConexion() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HostRewriteInterceptor(server.url("/")))
                .build();
        return new Conexion(client);
    }

    private String resumenNavidadJson() {
        return "[{"
                + "\"fecha_sorteo\":\"2025-12-22 08:30:00\","
                + "\"estado\":\"cerrado\","
                + "\"primerPremio\":{\"decimo\":\"79432\"},"
                + "\"segundoPremio\":{\"decimo\":\"70048\"},"
                + "\"tercerosPremios\":[{\"decimo\":\"90693\"}],"
                + "\"cuartosPremios\":[{\"decimo\":\"25508\"},{\"decimo\":\"78477\"}],"
                + "\"quintosPremios\":[{\"decimo\":\"23112\"},{\"decimo\":\"25412\"}],"
                + "\"urlListadoOficial\":\"/f/loterias/documentos/Lotería Nacional/listas de premios/SM_LISTAOFICIAL.A2025.S102.pdf\""
                + "}]";
    }

    private String resumenNinoJson() {
        return "[{"
                + "\"fecha_sorteo\":\"2025-01-06 12:00:00\","
                + "\"estado\":\"cerrado\","
                + "\"primerPremio\":{\"decimo\":\"78908\"},"
                + "\"segundoPremio\":{\"decimo\":\"06766\"},"
                + "\"tercerosPremios\":[{\"decimo\":\"66777\"}],"
                + "\"extraccionesDeCuatroCifras\":[{\"decimo\":\"1454\"},{\"decimo\":\"4276\"}],"
                + "\"extraccionesDeTresCifras\":[{\"decimo\":\"040\"},{\"decimo\":\"184\"}],"
                + "\"extraccionesDeDosCifras\":[{\"decimo\":\"11\"},{\"decimo\":\"26\"}],"
                + "\"reintegros\":[{\"decimo\":\"0\"},{\"decimo\":\"5\"},{\"decimo\":\"8\"}],"
                + "\"urlListadoOficial\":\"/f/loterias/documentos/Lotería Nacional/listas de premios/SM_LISTAOFICIAL.A2025.S002.pdf\""
                + "}]";
    }

    private String sorteoConEscrutinioJson() {
        return "[{"
                + "\"fecha_sorteo\":\"2025-12-22 08:30:00\","
                + "\"estado\":\"cerrado\","
                + "\"id_sorteo\":\"1295909102\""
                + "}]";
    }

    private String premioDecimoJson() {
        return "{"
                + "\"importePorDefecto\":2000,"
                + "\"compruebe\":[{\"decimo\":\"012345\",\"prize\":100000}]"
                + "}";
    }

    private String getUltimoSeisEnero() {
        LocalDate ahora = LocalDate.now();
        int year = ahora.getYear();
        LocalDate fechaSorteo = LocalDate.of(year, Month.JANUARY, 6);
        if (ahora.isBefore(fechaSorteo)) {
            fechaSorteo = LocalDate.of(year - 1, Month.JANUARY, 6);
        }
        return fechaSorteo.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    private String getUltimoVeintidosDiciembre() {
        LocalDate ahora = LocalDate.now();
        int year = ahora.getYear();
        LocalDate fechaSorteo = LocalDate.of(year, Month.DECEMBER, 22);
        if (ahora.isBefore(fechaSorteo)) {
            fechaSorteo = LocalDate.of(year - 1, Month.DECEMBER, 22);
        }
        return fechaSorteo.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    private static class HostRewriteInterceptor implements Interceptor {
        private final HttpUrl baseUrl;

        private HostRewriteInterceptor(HttpUrl baseUrl) {
            this.baseUrl = baseUrl;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            HttpUrl newUrl = request.url().newBuilder()
                    .scheme(baseUrl.scheme())
                    .host(baseUrl.host())
                    .port(baseUrl.port())
                    .build();
            return chain.proceed(request.newBuilder().url(newUrl).build());
        }
    }
}
