package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.conexion.Conexion;
import io.github.jcprieto.lib.loteria.enumeradores.Sorteo;
import io.github.jcprieto.lib.loteria.excepciones.PremioDecimoNoDisponibleException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ConexionPremioRespuestaInvalidaTest {

    private final String responseBody;
    private final Class<? extends Exception> expectedException;
    private final String testName;
    private MockWebServer server;

    public ConexionPremioRespuestaInvalidaTest(String testName, String responseBody,
                                               Class<? extends Exception> expectedException) {
        this.testName = testName;
        this.responseBody = responseBody;
        this.expectedException = expectedException;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"premio decimo no disponible", "E019", PremioDecimoNoDisponibleException.class},
                {"respuesta invalida lanza IOException", "{invalid-json", IOException.class},
                {"premio decimo no disponible entre comillas", "\"E019\"", PremioDecimoNoDisponibleException.class}
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
    public void testGetPremioConRespuestaNoProcesableLanzaExcepcionEsperada() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseBody));

        Conexion conexion = createConexion();

        try {
            conexion.getPremio(Sorteo.NAVIDAD, "12345");
            Assert.fail(testName + ": expected exception " + expectedException.getName());
        } catch (Exception exception) {
            Assert.assertTrue(testName + ": expected " + expectedException.getName()
                            + " but was " + exception.getClass().getName(),
                    expectedException.isAssignableFrom(exception.getClass()));
        }
    }

    private Conexion createConexion() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HostRewriteInterceptor(server.url("/")))
                .build();
        return new Conexion(client);
    }

    private String sorteoConEscrutinioJson() {
        return "[{"
                + "\"fecha_sorteo\":\"2025-12-22 08:30:00\","
                + "\"estado\":\"cerrado\","
                + "\"id_sorteo\":\"1295909102\""
                + "}]";
    }

    private record HostRewriteInterceptor(HttpUrl baseUrl) implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            okhttp3.Request original = chain.request();
            HttpUrl newUrl = original.url().newBuilder()
                    .scheme(baseUrl.scheme())
                    .host(baseUrl.host())
                    .port(baseUrl.port())
                    .build();
            return chain.proceed(original.newBuilder().url(newUrl).build());
        }
    }
}
