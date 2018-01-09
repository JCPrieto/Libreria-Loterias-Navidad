package es.jklabs.lib.loteria.conexion;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jklabs.lib.loteria.constant.HttpResponseCode;
import es.jklabs.lib.loteria.converter.PremioConverter;
import es.jklabs.lib.loteria.converter.ResumenNavidadConverter;
import es.jklabs.lib.loteria.converter.ResumenNinoConverter;
import es.jklabs.lib.loteria.enumeradores.Sorteo;
import es.jklabs.lib.loteria.model.Premio;
import es.jklabs.lib.loteria.model.json.Busqueda;
import es.jklabs.lib.loteria.model.json.navidad.Premios;
import es.jklabs.lib.loteria.model.navidad.ResumenNavidad;
import es.jklabs.lib.loteria.model.nino.ResumenNino;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Created by juanky on 20/03/15.
 */
public class Conexion {

    private static final String RESUMEN = "resumen";

    public ResumenNavidad getResumenNavidad() throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://api.elpais.com/ws/Loteria" + Sorteo.NAVIDAD.getParametro() + "Premiados?n="
                + RESUMEN);
        HttpResponse response = client.execute(request);
        if (Objects.equals(response.getStatusLine().getStatusCode(), HttpResponseCode.OK)) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            ObjectMapper mapper = new ObjectMapper();
            return ResumenNavidadConverter.get(mapper.readValue(rd.readLine().split("=")[1], Premios.class));
        } else {
            return null;
        }
    }

    public ResumenNino getResumenNino() throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://api.elpais.com/ws/Loteria" + Sorteo.NINO.getParametro() + "Premiados?n="
                + RESUMEN);
        HttpResponse response = client.execute(request);
        if (Objects.equals(response.getStatusLine().getStatusCode(), HttpResponseCode.OK)) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            ObjectMapper mapper = new ObjectMapper();
            return ResumenNinoConverter.get(mapper.readValue(rd.readLine().split("=")[1], es.jklabs.lib.loteria.model.json.nino.Premios.class));
        } else {
            return null;
        }
    }

    public Premio getPremio(Sorteo sorteo, String numero) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://api.elpais.com/ws/Loteria" + sorteo.getParametro() + "Premiados?n="
                + Integer.parseInt(numero));
        HttpResponse response = client.execute(request);
        if (Objects.equals(response.getStatusLine().getStatusCode(), HttpResponseCode.OK)) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            ObjectMapper mapper = new ObjectMapper();
            return PremioConverter.get(mapper.readValue(rd.readLine().split("=")[1], Busqueda.class));
        } else {
            return null;
        }
    }

}
