package com.jklabs.lib.loteria.service;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by juanky on 20/03/15.
 */
public class Resultado {
    protected transient List<String> valores;

    public Resultado(String consulta) {
        JSONParser parser = new JSONParser();
        ContainerFactory containerFactory = new ContainerFactory() {
            public List creatArrayContainer() {
                return new ArrayList();
            }

            public Map createObjectContainer() {
                return new LinkedHashMap();
            }

        };
        try {
            Map json = (Map) parser.parse(consulta, containerFactory);
            Iterator iter = json.entrySet().iterator();
            this.valores = new ArrayList();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                this.valores.add(entry.getValue().toString());
            }
        } catch (ParseException e) {
            Logger.getLogger("Resultados")
                    .log(Level.SEVERE, "Error Critico", e);
        }
    }

    protected String tratarEstado(String string) {
        String cadena;
        switch (Integer.parseInt(string)) {
            case 0:
                cadena = "El sorteo no ha comenzado aún.";
                break;
            case 1:
                cadena = "El sorteo ha está en proceso.";
                break;
            case 2:
                cadena = "El sorteo ha terminado y la lista de números y premios debería ser la correcta aunque, tomada al oído, no podemos estar seguros de ella.";
                break;
            case 3:
                cadena = "El sorteo ha terminado y existe una lista oficial.";
                break;
            case 4:
                cadena = "El sorteo ha terminado y la lista de números y premios está basada en la oficial.";
                break;
            default:
                cadena = "Algo ha fallado";
        }

        return cadena;
    }

    protected String tratarFecha(String string) {
        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar date = Calendar.getInstance();
        int time = Integer.parseInt(string);
        date.setTimeInMillis(time * 1000L);
        return date.get(Calendar.DAY_OF_MONTH) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.YEAR) + " " +
                mFormat.format(date.get(Calendar.HOUR_OF_DAY)) + ":" + mFormat.format(date.get(Calendar.MINUTE));
    }

    protected String tratarNumero(String string) {
        String cad;
        if (Integer.parseInt(string) == -1)
            cad = "-";
        else {
            cad = String.format("%05d",
                    new Object[]{Integer.valueOf(Integer.parseInt(string))});
        }
        return cad;
    }
}
