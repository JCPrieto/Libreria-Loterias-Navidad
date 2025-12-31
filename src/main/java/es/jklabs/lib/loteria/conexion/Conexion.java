package es.jklabs.lib.loteria.conexion;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jklabs.lib.loteria.converter.PremioConverter;
import es.jklabs.lib.loteria.converter.ResumenNavidadConverter;
import es.jklabs.lib.loteria.converter.ResumenNinoConverter;
import es.jklabs.lib.loteria.enumeradores.Sorteo;
import es.jklabs.lib.loteria.model.Premio;
import es.jklabs.lib.loteria.model.json.Busqueda;
import es.jklabs.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import es.jklabs.lib.loteria.model.navidad.ResumenNavidad;
import es.jklabs.lib.loteria.model.nino.ResumenNino;
import es.jklabs.utilidades.Logger;
import feign.*;
import feign.codec.Decoder;
import feign.okhttp.OkHttpClient;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by juanky on 20/03/15.
 */
public class Conexion {

    private static final String BASE_URL = "https://api.elpais.com";
    private static final String BASE_URL_SORTEOS = "https://www.loteriasyapuestas.es";
    private static final String PREMIADOS_N = "Premiados?n=";
    private static final String RESUMEN = "resumen";
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 5_000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 10_000;
    private static final Retryer DEFAULT_RETRYER = new Retryer.Default(200, TimeUnit.SECONDS.toMillis(1), 2);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;
    private static final String LOTERIAS_USER_AGENT = "PostmanRuntime/7.51.0";
    private final LoteriaApi api;
    private final LoteriaResultadosApi resultadosApi;
    private static InMemoryCookieJar cookieJar;
    private static String cmsCookie;
    private final okhttp3.OkHttpClient rawClient;
    private final AtomicBoolean loteriasWarmup = new AtomicBoolean(false);

    public Conexion() {
        this(DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS, DEFAULT_RETRYER);
    }

    public Conexion(int connectTimeoutMillis, int readTimeoutMillis, Retryer retryer) {
        this(new okhttp3.OkHttpClient(), connectTimeoutMillis, readTimeoutMillis, retryer, null);
    }

    public Conexion(okhttp3.OkHttpClient okHttpClient) {
        this(okHttpClient, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS, DEFAULT_RETRYER, null);
    }

    public Conexion(String cmsCookie) {
        this(new okhttp3.OkHttpClient(), DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS, DEFAULT_RETRYER,
                cmsCookie);
    }

    public Conexion(okhttp3.OkHttpClient okHttpClient, int connectTimeoutMillis, int readTimeoutMillis, Retryer retryer) {
        this(okHttpClient, connectTimeoutMillis, readTimeoutMillis, retryer, null);
    }

    public Conexion(okhttp3.OkHttpClient okHttpClient, int connectTimeoutMillis, int readTimeoutMillis, Retryer retryer,
                    String cmsCookie) {
        okhttp3.OkHttpClient client = okHttpClient == null ? new okhttp3.OkHttpClient() : okHttpClient;
        InMemoryCookieJar jar = null;
        if (client.cookieJar() == CookieJar.NO_COOKIES) {
            jar = new InMemoryCookieJar();
            client = client.newBuilder().cookieJar(jar).build();
        }
        this.cookieJar = jar;
        this.rawClient = client;
        this.cmsCookie = cmsCookie;
        Feign.Builder baseBuilder = Feign.builder()
                .client(new OkHttpClient(this.rawClient))
                .decoder(new PrefixedJsonDecoder())
                .options(new feign.Request.Options(connectTimeoutMillis, TimeUnit.MILLISECONDS, readTimeoutMillis,
                        TimeUnit.MILLISECONDS, true))
                .retryer(retryer == null ? DEFAULT_RETRYER : retryer);
        this.api = baseBuilder.target(LoteriaApi.class, BASE_URL);
        this.resultadosApi = baseBuilder
                .requestInterceptor(loteriasHeaders())
                .target(LoteriaResultadosApi.class, BASE_URL_SORTEOS);
    }

    private static String getUltimoVeintidosDiciembre() {
        LocalDate ahora = LocalDate.now();
        int year = ahora.getYear();
        LocalDate fechaSorteo = LocalDate.of(year, Month.DECEMBER, 22);
        if (ahora.isBefore(fechaSorteo)) {
            fechaSorteo = LocalDate.of(year - 1, Month.DECEMBER, 22);
        }
        return fechaSorteo.format(DATE_FORMAT);
    }

    private static RequestInterceptor loteriasHeaders() {
        return template -> {
            template.header("User-Agent", LOTERIAS_USER_AGENT);
            template.header("Accept", "*/*");
            template.header("Cache-Control", "no-cache");
            template.header("Accept-Language", "es-ES,es;q=0.9");
            template.header("Referer", "https://www.loteriasyapuestas.es/");
            template.header("Origin", "https://www.loteriasyapuestas.es");
            String cookie = getCmsCookieHeader();
            if (cookie != null) {
                template.header("Cookie", cookie);
            }
        };
    }

    private static String getCmsCookieHeader() {
        if (cmsCookie != null && !cmsCookie.isBlank()) {
            return normalizeCmsCookie(cmsCookie);
        }
        if (cookieJar == null) {
            return null;
        }
        String cookieHeader = cookieJar.getCookieHeader("www.loteriasyapuestas.es");
        return cookieHeader == null || cookieHeader.isBlank() ? null : cookieHeader;
    }

    public Premio getPremio(Sorteo sorteo, String numero) throws IOException {
        try {
            Busqueda busqueda = api.getPremio(sorteo.getParametro(), Integer.parseInt(numero));
            return PremioConverter.get(busqueda);
        } catch (FeignException e) {
            return null;
        } catch (RuntimeException e) {
            throw new IOException("Error al obtener el premio", e);
        }
    }

    private static String normalizeCmsCookie(String cookie) {
        String trimmed = cookie.trim();
        return trimmed.contains("=") ? trimmed : "cms=" + trimmed;
    }

    private interface LoteriaApi {
        @RequestLine("GET /ws/Loteria{parametro}" + PREMIADOS_N + RESUMEN)
        es.jklabs.lib.loteria.model.json.navidad.Premios getResumenNavidad(@Param("parametro") String parametro);

        @RequestLine("GET /ws/Loteria{parametro}" + PREMIADOS_N + RESUMEN)
        es.jklabs.lib.loteria.model.json.nino.Premios getResumenNino(@Param("parametro") String parametro);

        @RequestLine("GET /ws/Loteria{parametro}" + PREMIADOS_N + "{numero}")
        Busqueda getPremio(@Param("parametro") String parametro, @Param("numero") int numero);
    }

    private interface LoteriaResultadosApi {
        @RequestLine("GET /servicios/buscadorSorteos?fechaInicioInclusiva={fechaInicioInclusiva}"
                + "&fechaFinInclusiva={fechaFinInclusiva}&game_id=LNAC&celebrados=true")
        List<SorteoNavidadResponse> getResumenNavidad(
                @Param("fechaInicioInclusiva") String fechaInicioInclusiva,
                @Param("fechaFinInclusiva") String fechaFinInclusiva
        );
    }

    public ResumenNino getResumenNino() throws IOException {
        try {
            es.jklabs.lib.loteria.model.json.nino.Premios premios = api.getResumenNino(Sorteo.NINO.getParametro());
            return ResumenNinoConverter.get(premios);
        } catch (FeignException e) {
            Logger.error(e);
            return null;
        } catch (RuntimeException e) {
            throw new IOException("Error al obtener el resumen del Nino", e);
        }
    }

    public ResumenNavidad getResumenNavidad() throws IOException {
        try {
            warmUpLoterias();
            String fecha = getUltimoVeintidosDiciembre();
            List<SorteoNavidadResponse> sorteos = resultadosApi.getResumenNavidad(fecha, fecha);
            if (sorteos == null || sorteos.isEmpty()) {
                return null;
            }
            return ResumenNavidadConverter.get(sorteos.getFirst());
        } catch (FeignException e) {
            Logger.error(e);
            return null;
        } catch (RuntimeException e) {
            throw new IOException("Error al obtener el resumen de Navidad", e);
        }
    }

    private void warmUpLoterias() {
        if (loteriasWarmup.getAndSet(true)) {
            return;
        }
        Request request = new Request.Builder()
                .url(BASE_URL_SORTEOS + "/")
                .header("User-Agent", LOTERIAS_USER_AGENT)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "es-ES,es;q=0.9")
                .header("Referer", "https://www.loteriasyapuestas.es/")
                .header("Cache-Control", "no-cache")
                .build();
        try (okhttp3.Response ignored = rawClient.newCall(request).execute()) {
            // Best-effort to obtain cookies from the origin before API call.
        } catch (IOException ignored) {
            // Ignore warmup failures; the API call may still succeed.
        }
    }

    private static class PrefixedJsonDecoder implements Decoder {
        private static final ObjectMapper MAPPER = new ObjectMapper();

        @Override
        public Object decode(Response response, Type type) throws IOException {
            if (response.body() == null) {
                return null;
            }
            byte[] bytes = response.body().asInputStream().readAllBytes();
            String raw = new String(bytes, StandardCharsets.UTF_8);
            int prefixIndex = raw.indexOf('=');
            String json = prefixIndex >= 0 ? raw.substring(prefixIndex + 1) : raw;
            return MAPPER.readValue(json, MAPPER.constructType(type));
        }
    }

    private static class InMemoryCookieJar implements CookieJar {
        private final Map<String, List<Cookie>> store = new HashMap<>();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            store.put(url.host(), new ArrayList<>(cookies));
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = store.get(url.host());
            return cookies == null ? List.of() : cookies;
        }

        private String getCookieHeader(String host) {
            List<Cookie> cookies = store.get(host);
            if (cookies == null || cookies.isEmpty()) {
                return null;
            }
            StringBuilder header = new StringBuilder();
            for (Cookie cookie : cookies) {
                if (header.length() > 0) {
                    header.append("; ");
                }
                header.append(cookie.name()).append('=').append(cookie.value());
            }
            return header.toString();
        }
    }
}
