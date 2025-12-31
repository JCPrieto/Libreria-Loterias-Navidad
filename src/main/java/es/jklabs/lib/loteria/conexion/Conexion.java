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
import feign.*;
import feign.codec.Decoder;
import feign.okhttp.OkHttpClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private final LoteriaApi api;
    private final LoteriaResultadosApi resultadosApi;

    public Conexion() {
        this(DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS, DEFAULT_RETRYER);
    }

    public Conexion(int connectTimeoutMillis, int readTimeoutMillis, Retryer retryer) {
        this(new okhttp3.OkHttpClient(), connectTimeoutMillis, readTimeoutMillis, retryer);
    }

    public Conexion(okhttp3.OkHttpClient okHttpClient) {
        this(okHttpClient, DEFAULT_CONNECT_TIMEOUT_MS, DEFAULT_READ_TIMEOUT_MS, DEFAULT_RETRYER);
    }

    public Conexion(okhttp3.OkHttpClient okHttpClient, int connectTimeoutMillis, int readTimeoutMillis, Retryer retryer) {
        okhttp3.OkHttpClient client = okHttpClient == null ? new okhttp3.OkHttpClient() : okHttpClient;
        Feign.Builder builder = Feign.builder()
                .client(new OkHttpClient(client))
                .decoder(new PrefixedJsonDecoder())
                .options(new feign.Request.Options(connectTimeoutMillis, TimeUnit.MILLISECONDS, readTimeoutMillis,
                        TimeUnit.MILLISECONDS, true))
                .retryer(retryer == null ? DEFAULT_RETRYER : retryer);
        this.api = builder.target(LoteriaApi.class, BASE_URL);
        this.resultadosApi = builder.target(LoteriaResultadosApi.class, BASE_URL_SORTEOS);
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

    public ResumenNino getResumenNino() throws IOException {
        try {
            es.jklabs.lib.loteria.model.json.nino.Premios premios = api.getResumenNino(Sorteo.NINO.getParametro());
            return ResumenNinoConverter.get(premios);
        } catch (FeignException e) {
            return null;
        } catch (RuntimeException e) {
            throw new IOException("Error al obtener el resumen del Nino", e);
        }
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

    public ResumenNavidad getResumenNavidad() throws IOException {
        try {
            String fecha = getUltimoVeintidosDiciembre();
            List<SorteoNavidadResponse> sorteos = resultadosApi.getResumenNavidad(fecha, fecha);
            if (sorteos == null || sorteos.isEmpty()) {
                return null;
            }
            return ResumenNavidadConverter.get(sorteos.getFirst());
        } catch (FeignException e) {
            return null;
        } catch (RuntimeException e) {
            throw new IOException("Error al obtener el resumen de Navidad", e);
        }
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
}
