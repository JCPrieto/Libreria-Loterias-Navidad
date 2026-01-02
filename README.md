# Libreria-Loterias-Navidad

Librería de conexión con la Api de Loterías y Apuestas dle Estado para obtener los resultados de las Loterías de Navidad
y de El Niño

Actualmente esta librería está en uso en 2 aplicaciones de mi repositorio: LoteríasJava y LoteríasAndroid.

Dependencias añadidas para el cliente HTTP: OpenFeign + OkHttp.

### Uso ###

Ejemplo básico con timeouts y `OkHttpClient` propio:

```java
okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
        .callTimeout(java.time.Duration.ofSeconds(8))
        .build();

Conexion conexion = new Conexion(client);
ResumenNavidad resumen = conexion.getResumenNavidad();
```

Ejemplo con timeouts y retryer personalizados:

```java
Retryer retryer = new Retryer.Default(200, 1000, 2);
Conexion conexion = new Conexion(3000, 8000, retryer);
Premio premio = conexion.getPremio(Sorteo.NAVIDAD, "12345");
```

Nota: el numero de decimo debe ser numerico. Si tiene mas de 5 digitos, se usan los ultimos 5.

Logs: para desactivar el log a fichero, iniciar la JVM con `-Dloteria.logger.disableFile=true`.

getPremio: en caso de error o datos no disponibles, devuelve un `Premio` con `cantidad=0`.

Errores y estados:

- `getResumenNavidad` y `getResumenNino` devuelven `null` si no hay datos o hay error de red.
- `getPremio` devuelve un `Premio` con `cantidad=0` y `estado=NO_INICIADO` cuando no hay datos o hay error.

### Changelog ###

* 4.0.0

  * Integración de OpenFeign + OkHttp en la conexión HTTP
  * Scrapper de la web de Loterías y Apuestas del Estado
  * Actualización a Java 21

* 3.3.7

  * Correciones de seguridad y estabilidad

* 3.3.6

  * Correciones de seguridad y estabilidad

* 3.3.5

  * Correciones de seguridad y estabilidad

* 3.3.4

  * Correciones de seguridad y estabilidad

* 3.3.3

  * Correciones de seguridad y estabilidad

* 3.3.2

  * Actualización de seguridad de despendencias.
