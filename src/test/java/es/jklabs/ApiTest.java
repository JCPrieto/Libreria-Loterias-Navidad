package es.jklabs;

import es.jklabs.lib.loteria.conexion.Conexion;
import es.jklabs.lib.loteria.model.navidad.ResumenNavidad;
import es.jklabs.utilidades.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ApiTest {

    @Test
    public void testResumenNavidad() {
        Conexion conexion = new Conexion();
        ResumenNavidad resumen = null;
        try {
            resumen = conexion.getResumenNavidad();
        } catch (IOException e) {
            Logger.error(e);
        }
        Assert.assertNotNull(resumen);
    }
}
