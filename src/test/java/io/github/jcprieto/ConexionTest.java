package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.conexion.Conexion;
import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.enumeradores.Sorteo;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
        Assert.assertEquals(new BigDecimal("50"), premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.TERMINADO, premio.getEstado());
        Assert.assertNotNull(premio.getFechaActualizacion());
    }

    @Test
    public void testPremioConDecimoNoNumericoDevuelveCantidadCero() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(premioDecimoJson()));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12A45");

        Assert.assertNotNull(premio);
        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.NO_INICIADO, premio.getEstado());
    }

    @Test
    public void testPremioConDecimoMayorA5DigitosUsaUltimos5() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(premioDecimoJson()));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "0012345");

        Assert.assertNotNull(premio);
        Assert.assertEquals(new BigDecimal("50"), premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.TERMINADO, premio.getEstado());
    }

    @Test
    public void testPremioConDecimoVacioDevuelveCantidadCero() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(premioDecimoJson()));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "   ");

        Assert.assertNotNull(premio);
        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.NO_INICIADO, premio.getEstado());
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

    @Test
    public void testPremioRecargaCacheSiCambiaFechaConsulta() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(premioDecimoJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(premioDecimoJson()));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12345");
        expirePremioCache(conexion);
        Premio premioRecargado = conexion.getPremio(Sorteo.NAVIDAD, "12345");

        Assert.assertNotNull(premio);
        Assert.assertNotNull(premioRecargado);
        Assert.assertEquals(5, server.getRequestCount());
    }

    @Test
    public void testResumenNavidadUsaWarmupUnaSolaVez() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(resumenNavidadJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(resumenNavidadJson()));

        Conexion conexion = createConexion();
        ResumenNavidad first = conexion.getResumenNavidad();
        ResumenNavidad second = conexion.getResumenNavidad();

        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertEquals("/", server.takeRequest().getPath());
        Assert.assertTrue(server.takeRequest().getPath().startsWith("/servicios/buscadorSorteos"));
        Assert.assertTrue(server.takeRequest().getPath().startsWith("/servicios/buscadorSorteos"));
        Assert.assertEquals(3, server.getRequestCount());
    }

    @Test
    public void testWarmupConcurrenteEvitaSegundoWarmup() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(resumenNavidadJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(resumenNavidadJson()));
        CountDownLatch warmupStarted = new CountDownLatch(1);
        CountDownLatch releaseWarmup = new CountDownLatch(1);
        Conexion conexion = createConexionWithBlockingWarmup(warmupStarted, releaseWarmup);
        AtomicReference<Throwable> firstError = new AtomicReference<>();
        AtomicReference<Throwable> secondError = new AtomicReference<>();

        Thread first = new Thread(() -> getResumenNavidad(conexion, firstError));
        first.start();
        Assert.assertTrue(warmupStarted.await(5, TimeUnit.SECONDS));
        Thread second = new Thread(() -> getResumenNavidad(conexion, secondError));
        second.start();

        releaseWarmup.countDown();
        first.join(5_000);
        second.join(5_000);

        Assert.assertFalse(first.isAlive());
        Assert.assertFalse(second.isAlive());
        Assert.assertNull(firstError.get());
        Assert.assertNull(secondError.get());
        Assert.assertEquals("/", server.takeRequest().getPath());
        Assert.assertTrue(server.takeRequest().getPath().startsWith("/servicios/buscadorSorteos"));
        Assert.assertTrue(server.takeRequest().getPath().startsWith("/servicios/buscadorSorteos"));
        Assert.assertEquals(3, server.getRequestCount());
    }

    @Test
    public void testWarmupFallaReintentaEnSiguienteLlamada() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(resumenNavidadJson()));
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(resumenNavidadJson()));

        Conexion conexion = createConexion();
        ResumenNavidad first = conexion.getResumenNavidad();
        ResumenNavidad second = conexion.getResumenNavidad();

        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertEquals("/", server.takeRequest().getPath());
        Assert.assertTrue(server.takeRequest().getPath().startsWith("/servicios/buscadorSorteos"));
        Assert.assertEquals("/", server.takeRequest().getPath());
        Assert.assertTrue(server.takeRequest().getPath().startsWith("/servicios/buscadorSorteos"));
        Assert.assertEquals(4, server.getRequestCount());
    }

    @Test
    public void testWarmupConIOExceptionContinuaConsulta() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(resumenNavidadJson()));

        Conexion conexion = createConexionWithWarmupIOException();
        ResumenNavidad resumen = conexion.getResumenNavidad();

        RecordedRequest request = server.takeRequest();
        Assert.assertTrue(request.getPath().startsWith("/servicios/buscadorSorteos"));
        Assert.assertNotNull(resumen);
        Assert.assertEquals(1, server.getRequestCount());
    }

    @Test
    public void testResumenNavidadErrorDevuelveNull() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500));

        Conexion conexion = createConexion();
        ResumenNavidad resumen = conexion.getResumenNavidad();

        Assert.assertNull(resumen);
    }

    @Test
    public void testResumenNavidadConRuntimeExceptionLanzaIOException() {
        server.enqueue(new MockResponse().setResponseCode(200));

        Conexion conexion = createConexionWithServiceRuntimeException();

        try {
            conexion.getResumenNavidad();
            Assert.fail("Expected IOException");
        } catch (IOException exception) {
            Assert.assertEquals("Error al obtener el resumen de Navidad", exception.getMessage());
        }
    }

    @Test
    public void testResumenNavidadConJsonPrefijado() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("callback=" + resumenNavidadJson()));

        Conexion conexion = createConexion();
        ResumenNavidad resumen = conexion.getResumenNavidad();

        Assert.assertNotNull(resumen);
        Assert.assertEquals("79432", resumen.getGordo());
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

    @Test
    public void testResumenNinoListaVaciaDevuelveNull() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[]"));

        Conexion conexion = createConexion();
        ResumenNino resumen = conexion.getResumenNino();

        Assert.assertNull(resumen);
    }

    @Test
    public void testResumenNinoErrorDevuelveNull() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse().setResponseCode(500));

        Conexion conexion = createConexion();
        ResumenNino resumen = conexion.getResumenNino();

        Assert.assertNull(resumen);
    }

    @Test
    public void testResumenNinoConRuntimeExceptionLanzaIOException() {
        server.enqueue(new MockResponse().setResponseCode(200));

        Conexion conexion = createConexionWithServiceRuntimeException();

        try {
            conexion.getResumenNino();
            Assert.fail("Expected IOException");
        } catch (IOException exception) {
            Assert.assertEquals("Error al obtener el resumen del Nino", exception.getMessage());
        }
    }

    @Test
    public void testPremioSinCompruebeDevuelveCantidadCero() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(premioDecimoJsonSinCompruebe()));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12345");

        Assert.assertNotNull(premio);
        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.TERMINADO, premio.getEstado());
    }

    @Test
    public void testPremioIgnoraEntradasInvalidasDeCompruebe() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"importePorDefecto\":2000,\"compruebe\":[null,"
                        + "{\"decimo\":\"12A45\",\"prize\":100000},"
                        + "{\"decimo\":\"012345\",\"prize\":100000}]}"));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12345");

        Assert.assertNotNull(premio);
        Assert.assertEquals(new BigDecimal("50"), premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.TERMINADO, premio.getEstado());
    }

    @Test
    public void testResumenNavidadIncluyeCookieCmsNormalizada() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(resumenNavidadJson()));

        Conexion conexion = createConexionWithCmsCookie("  valorCookie  ");
        conexion.getResumenNavidad();

        server.takeRequest();
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("cms=valorCookie", request.getHeader("Cookie"));
    }

    @Test
    public void testResumenNavidadMantieneCookieCmsYaFormada() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(resumenNavidadJson()));

        Conexion conexion = createConexionWithCmsCookie("cms=valorCookie");
        conexion.getResumenNavidad();

        server.takeRequest();
        RecordedRequest request = server.takeRequest();
        Assert.assertEquals("cms=valorCookie", request.getHeader("Cookie"));
    }

    @Test
    public void testGetPremioConImportePorDefectoInvalidoDevuelveCantidadCero() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"importePorDefecto\":0,\"compruebe\":[{\"decimo\":\"012345\",\"prize\":100000}]}"));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12345");

        Assert.assertNotNull(premio);
        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.NO_INICIADO, premio.getEstado());
    }

    @Test
    public void testPremioConErrorHttpDevuelveCantidadCero() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse().setResponseCode(500));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12345");

        Assert.assertNotNull(premio);
        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.NO_INICIADO, premio.getEstado());
    }

    @Test
    public void testPremioConRuntimeExceptionLanzaIOException() {
        server.enqueue(new MockResponse().setResponseCode(200));

        Conexion conexion = createConexionWithServiceRuntimeException();

        try {
            conexion.getPremio(Sorteo.NAVIDAD, "12345");
            Assert.fail("Expected IOException");
        } catch (IOException exception) {
            Assert.assertEquals("Error al obtener el premio", exception.getMessage());
        }
    }

    @Test
    public void testGetPremioConRespuestaVaciaDevuelveCantidadCero() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("   "));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12345");

        Assert.assertNotNull(premio);
        Assert.assertEquals(BigDecimal.ZERO, premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.NO_INICIADO, premio.getEstado());
    }

    @Test
    public void testPremioNinoUsaFechaSeisEnero() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(sorteoConEscrutinioJson()));
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(premioDecimoJson()));

        Conexion conexion = createConexion();
        Premio premio = conexion.getPremio(Sorteo.NINO, "12345");

        server.takeRequest();
        RecordedRequest request = server.takeRequest();
        String fecha = getUltimoSeisEnero();
        Assert.assertEquals("/servicios/buscadorSorteosConEscrutinio?fechaInicioInclusiva=" + fecha
                + "&fechaFinInclusiva=" + fecha
                + "&game_id=LNAC&limiteMaxResultados=1", request.getPath());
        Assert.assertEquals(new BigDecimal("50"), premio.getCantidad());
        Assert.assertEquals(EstadoSorteo.TERMINADO, premio.getEstado());
    }

    @Test
    public void testConstructorConClienteNuloCreaCookieJarInterno() throws Exception {
        Conexion conexion = new Conexion(null, 5_000, 10_000, null);

        Field cookieJarField = Conexion.class.getDeclaredField("cookieJar");
        cookieJarField.setAccessible(true);
        Assert.assertNotNull(cookieJarField.get(conexion));
    }

    @Test
    public void testConstructorConCmsCookieNormalizaCabecera() throws Exception {
        Conexion conexion = new Conexion("valorCookie");

        Method method = Conexion.class.getDeclaredMethod("getCmsCookieHeader");
        method.setAccessible(true);
        String header = (String) method.invoke(conexion);

        Assert.assertEquals("cms=valorCookie", header);
    }

    @Test
    public void testGetCmsCookieHeaderDesdeCookieJar() throws Exception {
        Conexion conexion = new Conexion();
        Field cookieJarField = Conexion.class.getDeclaredField("cookieJar");
        cookieJarField.setAccessible(true);
        CookieJar cookieJar = (CookieJar) cookieJarField.get(conexion);
        Assert.assertNotNull(cookieJar);

        HttpUrl url = HttpUrl.parse("https://www.loteriasyapuestas.es/");
        Assert.assertNotNull(url);
        Cookie cookie = new Cookie.Builder()
                .name("cms")
                .value("valorJar")
                .domain("www.loteriasyapuestas.es")
                .path("/")
                .build();
        Cookie otherCookie = new Cookie.Builder()
                .name("other")
                .value("valorOther")
                .domain("www.loteriasyapuestas.es")
                .path("/")
                .build();
        cookieJar.saveFromResponse(url, List.of(cookie, otherCookie));

        Method method = Conexion.class.getDeclaredMethod("getCmsCookieHeader");
        method.setAccessible(true);
        String header = (String) method.invoke(conexion);
        Assert.assertEquals("cms=valorJar; other=valorOther", header);
    }

    @Test
    public void testGetCmsCookieHeaderConCookieJarVacioDevuelveNull() throws Exception {
        Conexion conexion = new Conexion();
        Field cookieJarField = Conexion.class.getDeclaredField("cookieJar");
        cookieJarField.setAccessible(true);
        CookieJar cookieJar = (CookieJar) cookieJarField.get(conexion);
        Assert.assertNotNull(cookieJar);

        HttpUrl url = HttpUrl.parse("https://www.loteriasyapuestas.es/");
        Assert.assertNotNull(url);
        cookieJar.saveFromResponse(url, List.of());

        Method method = Conexion.class.getDeclaredMethod("getCmsCookieHeader");
        method.setAccessible(true);
        String header = (String) method.invoke(conexion);

        Assert.assertNull(header);
    }

    @Test
    public void testGetCmsCookieHeaderConCookieEnBlancoDevuelveNull() throws Exception {
        Conexion conexion = createConexionWithCmsCookie("   ");

        Method method = Conexion.class.getDeclaredMethod("getCmsCookieHeader");
        method.setAccessible(true);
        String header = (String) method.invoke(conexion);

        Assert.assertNull(header);
    }

    @Test
    public void testParsePremioDecimoSinRespuestaDevuelveNull() throws Exception {
        Conexion conexion = new Conexion();
        Method method = Conexion.class.getDeclaredMethod("parsePremioDecimo", feign.Response.class);
        method.setAccessible(true);

        Assert.assertNull(method.invoke(conexion, new Object[]{null}));
    }

    @Test
    public void testParsePremioDecimoSinBodyDevuelveNull() throws Exception {
        Conexion conexion = new Conexion();
        Method method = Conexion.class.getDeclaredMethod("parsePremioDecimo", feign.Response.class);
        method.setAccessible(true);
        feign.Response response = feign.Response.builder()
                .status(200)
                .reason("OK")
                .request(feignRequest())
                .build();

        Assert.assertNull(method.invoke(conexion, response));
    }

    @Test
    public void testPrefixedJsonDecoderSinBodyDevuelveNull() throws Exception {
        Class<?> decoderClass = Class.forName("io.github.jcprieto.lib.loteria.conexion.Conexion$PrefixedJsonDecoder");
        var constructor = decoderClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object decoder = constructor.newInstance();
        Method method = decoderClass.getDeclaredMethod("decode", feign.Response.class, java.lang.reflect.Type.class);
        method.setAccessible(true);
        feign.Response response = feign.Response.builder()
                .status(200)
                .reason("OK")
                .request(feignRequest())
                .build();

        Assert.assertNull(method.invoke(decoder, response, ResumenNavidad[].class));
    }

    @Test
    public void testGetFirstOrNullConListaNullDevuelveNull() throws Exception {
        Method method = Conexion.class.getDeclaredMethod("getFirstOrNull", List.class);
        method.setAccessible(true);

        Assert.assertNull(method.invoke(null, new Object[]{null}));
    }

    @Test
    public void testGetCmsCookieHeaderSinCookieJarDevuelveNull() throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return List.of();
                    }
                })
                .build();
        Conexion conexion = new Conexion(client);

        Method method = Conexion.class.getDeclaredMethod("getCmsCookieHeader");
        method.setAccessible(true);
        String header = (String) method.invoke(conexion);

        Assert.assertNull(header);
    }

    private Conexion createConexion() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HostRewriteInterceptor(server.url("/")))
                .build();
        return new Conexion(client);
    }

    private Conexion createConexionWithWarmupIOException() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new WarmupIOExceptionInterceptor(server.url("/")))
                .build();
        return new Conexion(client);
    }

    private Conexion createConexionWithBlockingWarmup(CountDownLatch warmupStarted, CountDownLatch releaseWarmup) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BlockingWarmupInterceptor(server.url("/"), warmupStarted, releaseWarmup))
                .build();
        return new Conexion(client);
    }

    private Conexion createConexionWithServiceRuntimeException() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ServiceRuntimeExceptionInterceptor(server.url("/")))
                .build();
        return new Conexion(client);
    }

    private Conexion createConexionWithCmsCookie(String cmsCookie) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HostRewriteInterceptor(server.url("/")))
                .build();
        return new Conexion(client, 5_000, 10_000, null, cmsCookie);
    }

    private feign.Request feignRequest() {
        return feign.Request.create(feign.Request.HttpMethod.GET, "https://www.loteriasyapuestas.es/",
                Map.of(), null, null, null);
    }

    @SuppressWarnings("unchecked")
    private void expirePremioCache(Conexion conexion) throws Exception {
        Field cacheField = Conexion.class.getDeclaredField("premioCache");
        cacheField.setAccessible(true);
        Map<Sorteo, Object> cache = (Map<Sorteo, Object>) cacheField.get(conexion);
        Object navidadCache = cache.get(Sorteo.NAVIDAD);
        Assert.assertNotNull(navidadCache);
        Field fechaConsultaField = navidadCache.getClass().getDeclaredField("fechaConsulta");
        fechaConsultaField.setAccessible(true);
        fechaConsultaField.set(navidadCache, "19000101");
    }

    private void getResumenNavidad(Conexion conexion, AtomicReference<Throwable> error) {
        try {
            Assert.assertNotNull(conexion.getResumenNavidad());
        } catch (Throwable throwable) {
            error.set(throwable);
        }
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

    private String premioDecimoJsonSinCompruebe() {
        return "{"
                + "\"importePorDefecto\":2000"
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

    private record WarmupIOExceptionInterceptor(HttpUrl baseUrl) implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if ("/".equals(request.url().encodedPath())) {
                throw new IOException("warmup unavailable");
            }
            HttpUrl newUrl = request.url().newBuilder()
                    .scheme(baseUrl.scheme())
                    .host(baseUrl.host())
                    .port(baseUrl.port())
                    .build();
            return chain.proceed(request.newBuilder().url(newUrl).build());
        }
    }

    private record BlockingWarmupInterceptor(HttpUrl baseUrl, CountDownLatch warmupStarted,
                                             CountDownLatch releaseWarmup) implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if ("/".equals(request.url().encodedPath())) {
                warmupStarted.countDown();
                try {
                    Assert.assertTrue(releaseWarmup.await(5, TimeUnit.SECONDS));
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IOException("warmup interrupted", exception);
                }
            }
            HttpUrl newUrl = request.url().newBuilder()
                    .scheme(baseUrl.scheme())
                    .host(baseUrl.host())
                    .port(baseUrl.port())
                    .build();
            return chain.proceed(request.newBuilder().url(newUrl).build());
        }
    }

    private record ServiceRuntimeExceptionInterceptor(HttpUrl baseUrl) implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            HttpUrl newUrl = request.url().newBuilder()
                    .scheme(baseUrl.scheme())
                    .host(baseUrl.host())
                    .port(baseUrl.port())
                    .build();
            if (!"/".equals(request.url().encodedPath())) {
                throw new IllegalStateException("service unavailable");
            }
            return chain.proceed(request.newBuilder().url(newUrl).build());
        }
    }
}
