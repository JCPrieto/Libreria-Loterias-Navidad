package es.jklabs;

import es.jklabs.lib.loteria.conexion.Conexion;
import es.jklabs.lib.loteria.enumeradores.EstadoSorteo;
import es.jklabs.lib.loteria.enumeradores.Sorteo;
import es.jklabs.lib.loteria.model.Premio;
import es.jklabs.lib.loteria.model.navidad.ResumenNavidad;
import es.jklabs.lib.loteria.model.nino.ResumenNino;
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
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("resultado=" + resumenNinoJson()));

        Conexion conexion = createConexion();
        ResumenNino resumen = conexion.getResumenNino();

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("/ws/LoteriaNinoPremiados?n=resumen", request.getPath());
        Assert.assertNotNull(resumen);
        Assert.assertEquals("11111", resumen.getPrimero());
        Assert.assertEquals("22222", resumen.getSegundo());
        Assert.assertEquals("33333", resumen.getTercero());
        Assert.assertEquals(1, resumen.getCuatroCifras().size());
        Assert.assertEquals("1234", resumen.getCuatroCifras().get(0));
        Assert.assertEquals(1, resumen.getTresCifras().size());
        Assert.assertEquals("234", resumen.getTresCifras().get(0));
        Assert.assertEquals(1, resumen.getDosCifras().size());
        Assert.assertEquals("12", resumen.getDosCifras().get(0));
        Assert.assertEquals(1, resumen.getReintegros().size());
        Assert.assertEquals("1", resumen.getReintegros().get(0));
        Assert.assertEquals("http://pdf", resumen.getUrlPDF());
        Assert.assertEquals(EstadoSorteo.TERMINADO, resumen.getEstado());
        Assert.assertNotNull(resumen.getFechaActualizacion());
    }

    @Test
    public void testPremio() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("resultado=" + premioJson()));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12345");

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("/ws/LoteriaNavidadPremiados?n=12345", request.getPath());
        Assert.assertNotNull(premio);
        Assert.assertEquals(50.0, premio.getCantidad(), 0.001);
        Assert.assertEquals(EstadoSorteo.TERMINADO_OFICIAL, premio.getEstado());
        Assert.assertNotNull(premio.getFechaActualizacion());
    }

    @Test
    public void testResumenNavidadErrorDevuelveNull() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500));

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
        return "{"
                + "\"timestamp\":1700000000,"
                + "\"status\":3,"
                + "\"fraseTexto\":\"algo\","
                + "\"pdfURL\":\"http://pdf\","
                + "\"error\":0,"
                + "\"premio1\":11111,"
                + "\"premio2\":22222,"
                + "\"premio3\":33333,"
                + "\"extracciones4cifras\":[\"1234\",\"-1\"],"
                + "\"extracciones3cifras\":[\"234\",\"-1\"],"
                + "\"extracciones2cifras\":[\"12\",\"-1\"],"
                + "\"reintegros\":[\"1\",\"-1\"]"
                + "}";
    }

    private String premioJson() {
        return "{"
                + "\"numero\":\"12345\","
                + "\"premio\":1000,"
                + "\"timestamp\":1700000000,"
                + "\"status\":4,"
                + "\"error\":0"
                + "}";
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
