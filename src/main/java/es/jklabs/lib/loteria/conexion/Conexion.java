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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * Created by juanky on 20/03/15.
 */
public class Conexion {

    private static final String HTTP_API_ELPAIS_COM_WS_LOTERIA = "http://api.elpais.com/ws/Loteria";
    private static final String PREMIADOS_N = "Premiados?n=";
    private static final String RESUMEN = "resumen";

    public ResumenNavidad getResumenNavidad() throws IOException {
        URL url = new URL(HTTP_API_ELPAIS_COM_WS_LOTERIA + Sorteo.NAVIDAD.getParametro() + PREMIADOS_N
                + RESUMEN);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (Objects.equals(urlConnection.getResponseCode(), HttpResponseCode.OK)) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            ObjectMapper mapper = new ObjectMapper();
            return ResumenNavidadConverter.get(mapper.readValue(rd.readLine().split("=")[1], Premios.class));
        } else {
            return null;
        }
    }

    public ResumenNino getResumenNino() throws IOException {
        URL url = new URL(HTTP_API_ELPAIS_COM_WS_LOTERIA + Sorteo.NINO.getParametro() + PREMIADOS_N
                + RESUMEN);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (Objects.equals(urlConnection.getResponseCode(), HttpResponseCode.OK)) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            ObjectMapper mapper = new ObjectMapper();
            return ResumenNinoConverter.get(mapper.readValue(rd.readLine().split("=")[1], es.jklabs.lib.loteria.model.json.nino.Premios.class));
        } else {
            return null;
        }
    }

    public Premio getPremio(Sorteo sorteo, String numero) throws IOException {
        URL url = new URL(HTTP_API_ELPAIS_COM_WS_LOTERIA + sorteo.getParametro() + PREMIADOS_N
                + Integer.parseInt(numero));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (Objects.equals(urlConnection.getResponseCode(), HttpResponseCode.OK)) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            ObjectMapper mapper = new ObjectMapper();
            return PremioConverter.get(mapper.readValue(rd.readLine().split("=")[1], Busqueda.class));
        } else {
            return null;
        }
    }

}
