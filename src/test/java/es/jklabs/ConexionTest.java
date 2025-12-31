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
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("resultado=" + resumenNavidadJson()));

        Conexion conexion = createConexion();
        ResumenNavidad resumen = conexion.getResumenNavidad();

        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("/ws/LoteriaNavidadPremiados?n=resumen", request.getPath());
        Assert.assertNotNull(resumen);
        Assert.assertEquals("12345", resumen.getGordo());
        Assert.assertEquals("54321", resumen.getSegundo());
        Assert.assertEquals("11111", resumen.getTercero());
        Assert.assertEquals(2, resumen.getCuarto().size());
        Assert.assertEquals("22222", resumen.getCuarto().get(0));
        Assert.assertEquals("33333", resumen.getCuarto().get(1));
        Assert.assertEquals(1, resumen.getQuinto().size());
        Assert.assertEquals("44444", resumen.getQuinto().get(0));
        Assert.assertEquals("http://pdf", resumen.getUrlPDF());
        Assert.assertEquals(EstadoSorteo.EN_PROCESO, resumen.getEstado());
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
        return "{"
                + "\"timestamp\":1700000000,"
                + "\"status\":1,"
                + "\"numero1\":12345,"
                + "\"numero2\":54321,"
                + "\"numero3\":11111,"
                + "\"numero4\":22222,"
                + "\"numero5\":33333,"
                + "\"numero6\":44444,"
                + "\"numero7\":-1,"
                + "\"numero8\":-1,"
                + "\"numero9\":-1,"
                + "\"numero10\":-1,"
                + "\"numero11\":-1,"
                + "\"numero12\":-1,"
                + "\"numero13\":-1,"
                + "\"fraseSorteoPDF\":\"frase\","
                + "\"fraseListaPDF\":\"lista\","
                + "\"listaPDF\":\"http://pdf\","
                + "\"urlAudio\":\"http://audio\","
                + "\"error\":0"
                + "}";
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
