package io.github.jcprieto.lib.loteria.conexion;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.*;
import feign.codec.Decoder;
import feign.okhttp.OkHttpClient;
import io.github.jcprieto.lib.loteria.converter.PremioConverter;
import io.github.jcprieto.lib.loteria.converter.ResumenNavidadConverter;
import io.github.jcprieto.lib.loteria.converter.ResumenNinoConverter;
import io.github.jcprieto.lib.loteria.converter.SorteoResponseConverterUtils;
import io.github.jcprieto.lib.loteria.enumeradores.Sorteo;
import io.github.jcprieto.lib.loteria.excepciones.PremioDecimoNoDisponibleException;
import io.github.jcprieto.lib.loteria.model.Premio;
import io.github.jcprieto.lib.loteria.model.json.navidad.PremioDecimoResponse;
import io.github.jcprieto.lib.loteria.model.json.navidad.SorteoConEscrutinioResponse;
import io.github.jcprieto.lib.loteria.model.json.navidad.SorteoNavidadResponse;
import io.github.jcprieto.lib.loteria.model.navidad.ResumenNavidad;
import io.github.jcprieto.lib.loteria.model.nino.ResumenNino;
import io.github.jcprieto.utilidades.Logger;
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

    private static final String BASE_URL_SORTEOS = "https://www.loteriasyapuestas.es";
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 5_000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 10_000;
    private static final Retryer DEFAULT_RETRYER = new Retryer.Default(200, TimeUnit.SECONDS.toMillis(1), 2);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;
    private static final String LOTERIAS_USER_AGENT = "PostmanRuntime/7.51.0";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final LoteriaResultadosApi resultadosApi;
    private final InMemoryCookieJar cookieJar;
    private final String cmsCookie;
    private final okhttp3.OkHttpClient rawClient;
    private final AtomicBoolean loteriasWarmup = new AtomicBoolean(false);
    private final Map<Sorteo, PremioDecimoCache> premioCache = new HashMap<>();

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
        cookieJar = jar;
        this.rawClient = client;
        this.cmsCookie = cmsCookie;
        Feign.Builder baseBuilder = Feign.builder()
                .client(new OkHttpClient(this.rawClient))
                .decoder(new PrefixedJsonDecoder())
                .options(new feign.Request.Options(connectTimeoutMillis, TimeUnit.MILLISECONDS, readTimeoutMillis,
                        TimeUnit.MILLISECONDS, true))
                .retryer(retryer == null ? DEFAULT_RETRYER : retryer);
        this.resultadosApi = baseBuilder
                .requestInterceptor(loteriasHeaders())
                .target(LoteriaResultadosApi.class, BASE_URL_SORTEOS);
    }

    private static String getUltimoVeintidosDiciembre() {
        return getUltimaFecha(Month.DECEMBER, 22);
    }

    private static String getUltimoSeisEnero() {
        return getUltimaFecha(Month.JANUARY, 6);
    }

    private static String getUltimaFecha(Month month, int dayOfMonth) {
        LocalDate ahora = LocalDate.now();
        int year = ahora.getYear();
        LocalDate fechaSorteo = LocalDate.of(year, month, dayOfMonth);
        if (ahora.isBefore(fechaSorteo)) {
            fechaSorteo = LocalDate.of(year - 1, month, dayOfMonth);
        }
        return fechaSorteo.format(DATE_FORMAT);
    }

    private RequestInterceptor loteriasHeaders() {
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

    private String getCmsCookieHeader() {
        if (cmsCookie != null && !cmsCookie.isBlank()) {
            return normalizeCmsCookie(cmsCookie);
        }
        if (cookieJar == null) {
            return null;
        }
        String cookieHeader = cookieJar.getCookieHeader();
        return cookieHeader == null || cookieHeader.isBlank() ? null : cookieHeader;
    }

    private static <T> T getFirstOrNull(List<T> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        return items.getFirst();
    }

    private static String normalizeCmsCookie(String cookie) {
        String trimmed = cookie.trim();
        return trimmed.contains("=") ? trimmed : "cms=" + trimmed;
    }

    private static boolean isNumeric(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static String summarizeBody(String body) {
        String normalized = body == null ? "" : body.replace('\n', ' ').replace('\r', ' ').trim();
        if (normalized.length() <= 200) {
            return normalized;
        }
        return normalized.substring(0, 200) + "...";
    }

    private static Premio emptyPremio() {
        Premio premio = new Premio();
        premio.setCantidad(0D);
        return premio;
    }

    public Premio getPremio(Sorteo sorteo, String numero) throws IOException {
        try {
            warmUpLoterias();
            PremioDecimoCache cache = getPremioCache(sorteo);
            if (cache == null) {
                return emptyPremio();
            }
            String normalized = normalizeDecimo(numero);
            if (normalized == null) {
                return emptyPremio();
            }
            long premio = cache.premiosPorDecimo.getOrDefault(normalized, 0L);
            return PremioConverter.get(cache.estado, cache.fechaSorteo, premio, cache.importePorDefecto);
        } catch (FeignException e) {
            Logger.error(e);
            return emptyPremio();
        } catch (RuntimeException e) {
            throw new IOException("Error al obtener el premio", e);
        }
    }

    private PremioDecimoCache getPremioCache(Sorteo sorteo) throws IOException {
        String fecha = getFechaSorteo(sorteo);
        synchronized (premioCache) {
            PremioDecimoCache cached = premioCache.get(sorteo);
            if (cached != null && fecha.equals(cached.fechaConsulta)) {
                return cached;
            }
        }
        List<SorteoConEscrutinioResponse> sorteos = resultadosApi.getSorteosConEscrutinio(fecha, fecha);
        SorteoConEscrutinioResponse sorteoResponse = getFirstOrNull(sorteos);
        if (sorteoResponse == null) {
            return null;
        }
        if (sorteoResponse.getIdSorteo() == null || sorteoResponse.getIdSorteo().isBlank()) {
            return null;
        }
        PremioDecimoResponse premioResponse = parsePremioDecimo(resultadosApi.getPremioDecimo(
                sorteoResponse.getIdSorteo()));
        if (premioResponse == null || premioResponse.getImportePorDefecto() <= 0) {
            return null;
        }
        PremioDecimoCache cache = buildPremioCache(fecha, sorteoResponse, premioResponse);
        synchronized (premioCache) {
            premioCache.put(sorteo, cache);
        }
        return cache;
    }

    public ResumenNino getResumenNino() throws IOException {
        try {
            warmUpLoterias();
            String fecha = getUltimoSeisEnero();
            List<SorteoNavidadResponse> sorteos = resultadosApi.getResumenNavidad(fecha, fecha);
            SorteoNavidadResponse sorteo = getFirstOrNull(sorteos);
            if (sorteo == null) {
                return null;
            }
            return ResumenNinoConverter.get(BASE_URL_SORTEOS, sorteo);
        } catch (FeignException e) {
            Logger.error(e);
            return null;
        } catch (RuntimeException e) {
            throw new IOException("Error al obtener el resumen del Nino", e);
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

    private PremioDecimoResponse parsePremioDecimo(Response response) throws IOException {
        if (response == null || response.body() == null) {
            return null;
        }
        String raw = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if ("E019".equals(trimmed) || "\"E019\"".equals(trimmed)) {
            throw new PremioDecimoNoDisponibleException("Premio no disponible para el sorteo solicitado");
        }
        try {
            return OBJECT_MAPPER.readValue(trimmed, PremioDecimoResponse.class);
        } catch (IOException e) {
            Logger.error("Error al parsear premioDecimo: " + summarizeBody(trimmed), e);
            throw e;
        }
    }

    private PremioDecimoCache buildPremioCache(String fechaConsulta, SorteoConEscrutinioResponse sorteoResponse,
                                               PremioDecimoResponse premioResponse) {
        PremioDecimoCache cache = new PremioDecimoCache();
        cache.fechaConsulta = fechaConsulta;
        cache.estado = sorteoResponse.getEstado();
        cache.fechaSorteo = sorteoResponse.getFechaSorteo();
        cache.importePorDefecto = premioResponse.getImportePorDefecto();
        cache.premiosPorDecimo = new HashMap<>();
        if (premioResponse.getCompruebe() == null) {
            return cache;
        }
        for (PremioDecimoResponse.PremioDetalle premio : premioResponse.getCompruebe()) {
            if (premio == null) {
                continue;
            }
            String decimo = normalizeDecimo(premio.getDecimo());
            if (decimo != null) {
                cache.premiosPorDecimo.put(decimo, premio.getPrize());
            }
        }
        return cache;
    }

    private String getFechaSorteo(Sorteo sorteo) {
        return sorteo == Sorteo.NINO ? getUltimoSeisEnero() : getUltimoVeintidosDiciembre();
    }

    private String normalizeDecimo(String numero) {
        if (numero == null) {
            return null;
        }
        String trimmed = numero.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (!isNumeric(trimmed)) {
            return null;
        }
        String normalized = trimmed.length() > 5 ? trimmed.substring(trimmed.length() - 5) : trimmed;
        return SorteoResponseConverterUtils.formatDecimo(normalized);
    }

    public ResumenNavidad getResumenNavidad() throws IOException {
        try {
            warmUpLoterias();
            String fecha = getUltimoVeintidosDiciembre();
            List<SorteoNavidadResponse> sorteos = resultadosApi.getResumenNavidad(fecha, fecha);
            SorteoNavidadResponse sorteo = getFirstOrNull(sorteos);
            if (sorteo == null) {
                return null;
            }
            return ResumenNavidadConverter.get(BASE_URL_SORTEOS, sorteo);
        } catch (FeignException e) {
            Logger.error(e);
            return null;
        } catch (RuntimeException e) {
            throw new IOException("Error al obtener el resumen de Navidad", e);
        }
    }

    private interface LoteriaResultadosApi {
        @RequestLine("GET /servicios/buscadorSorteos?fechaInicioInclusiva={fechaInicioInclusiva}"
                + "&fechaFinInclusiva={fechaFinInclusiva}&game_id=LNAC&celebrados=true")
        List<SorteoNavidadResponse> getResumenNavidad(
                @Param("fechaInicioInclusiva") String fechaInicioInclusiva,
                @Param("fechaFinInclusiva") String fechaFinInclusiva
        );

        @RequestLine("GET /servicios/buscadorSorteosConEscrutinio?fechaInicioInclusiva={fechaInicioInclusiva}"
                + "&fechaFinInclusiva={fechaFinInclusiva}&game_id=LNAC&limiteMaxResultados=1")
        List<SorteoConEscrutinioResponse> getSorteosConEscrutinio(
                @Param("fechaInicioInclusiva") String fechaInicioInclusiva,
                @Param("fechaFinInclusiva") String fechaFinInclusiva
        );

        @RequestLine("GET /servicios/premioDecimoWeb?idsorteo={idsorteo}")
        Response getPremioDecimo(@Param("idsorteo") String idsorteo);
    }

    private static class PremioDecimoCache {
        private String fechaConsulta;
        private String estado;
        private String fechaSorteo;
        private int importePorDefecto;
        private Map<String, Long> premiosPorDecimo;
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

        private String getCookieHeader() {
            List<Cookie> cookies = store.get("www.loteriasyapuestas.es");
            if (cookies == null || cookies.isEmpty()) {
                return null;
            }
            StringBuilder header = new StringBuilder();
            for (Cookie cookie : cookies) {
                if (!header.isEmpty()) {
                    header.append("; ");
                }
                header.append(cookie.name()).append('=').append(cookie.value());
            }
            return header.toString();
        }
    }
}
