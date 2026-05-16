package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.conexion.Conexion;
import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.enumeradores.Sorteo;
import io.github.jcprieto.lib.loteria.model.Premio;
import okhttp3.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ConexionPremioSorteoSinIdTest {

    private final String responseBody;
    private MockWebServer server;

    public ConexionPremioSorteoSinIdTest(String testName, String responseBody) {
        this.responseBody = responseBody;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"sin id sorteo", "[{\"fecha_sorteo\":\"2025-12-22 08:30:00\",\"estado\":\"cerrado\"}]"},
                {"sin sorteos", "[]"},
                {"id sorteo vacio", "[{\"fecha_sorteo\":\"2025-12-22 08:30:00\",\"estado\":\"cerrado\",\"id_sorteo\":\"   \"}]"}
        });
    }

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
    public void testGetPremioSinIdSorteoDevuelveCantidadCero() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12345");

        Assert.assertNotNull(premio);
        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.NO_INICIADO, premio.getEstado());
        Assert.assertEquals(2, server.getRequestCount());
    }

    private Conexion createConexion() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HostRewriteInterceptor(server.url("/")))
                .build();
        return new Conexion(client);
    }

    private record HostRewriteInterceptor(HttpUrl baseUrl) implements Interceptor {

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
