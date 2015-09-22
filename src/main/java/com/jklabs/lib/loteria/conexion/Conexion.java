package com.jklabs.lib.loteria.conexion;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by juanky on 20/03/15.
 */
public class Conexion {
    private final transient String url;
    private transient String resultado;

    public Conexion(String string, String string2) {
        this.url = "http://api.elpais.com/ws/Loteria" + string + "Premiados?n="
                + eliminarCeros(string2);
        this.resultado = "";
    }

    public boolean consulta() {
        boolean noError = false;
        try {
            URL urlc = new URL(this.url);
            HttpURLConnection conexion = (HttpURLConnection) urlc
                    .openConnection();
            conexion.setRequestMethod("GET");
            BufferedReader read = new BufferedReader(new InputStreamReader(
                    conexion.getInputStream()));
            String[] res = read.readLine().split("=");
            read.close();
            if (!(isError(res[1]))) {
                noError = true;
                this.resultado = res[1];
            }
        } catch (IOException e) {
            Logger.getLogger("Conexion").log(Level.SEVERE, "Error Critico", e);
        }
        return noError;
    }

    private String eliminarCeros(String string) {
        boolean cero = true;
        int count = 0;
        while (cero && count < string.length()) {
            if (string.charAt(count) == '0')
                ++count;
            else {
                cero = false;
            }
        }
        String cad;
        if (count == string.length()) {
            cad = "0";
        } else {
            cad = string.substring(count, string.length());
        }
        return cad;
    }

    public String getResultado() {
        return this.resultado;
    }

    private boolean isError(String string) {
        JSONParser parser = new JSONParser();
        ContainerFactory containerFactory = new ContainerFactory() {
            public List creatArrayContainer() {
                return new ArrayList();
            }

            public Map createObjectContainer() {
                return new LinkedHashMap();
            }
        };
        boolean error = true;
        try {
            Map json = (Map) parser.parse(string, containerFactory);
            Iterator iter = json.entrySet().iterator();
            Map.Entry entry = (Map.Entry) iter.next();
            error = entry.getKey().equals("error");
        } catch (ParseException e) {
            Logger.getLogger("Conexion").log(Level.SEVERE, "Error Critico", e);
        }
        return error;
    }
}
