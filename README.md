# Libreria-Loterias-Navidad

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=JCPrieto_Libreria-Loterias-Navidad&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=JCPrieto_Libreria-Loterias-Navidad)

Librería de conexión con la API de Loterías y Apuestas del Estado para obtener los resultados de las Loterías de Navidad
y de El Niño.

Actualmente, esta librería está en uso en dos aplicaciones de mi repositorio: LoteríasJava y LoteríasAndroid.

Cliente HTTP: OpenFeign + OkHttp.

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

Nota: el número de décimo debe ser numérico. Si tiene más de 5 dígitos, se usan los últimos 5.

Logs: la librería usa SLF4J; el backend y la configuración los aporta la aplicación consumidora.

Nota de migración (5.0.0): se elimina el logger propio; añade un backend SLF4J en tu aplicación si quieres ver logs.

Ejemplo de backend con Logback (app consumidora):

Maven:

```xml

<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>1.5.18</version>
</dependency>
```

Ejemplo de backend con Log4j2 (app consumidora):

Maven:

```xml

<dependency>
  <groupId>org.apache.logging.log4j</groupId>
  <artifactId>log4j-slf4j2-impl</artifactId>
  <version>2.25.1</version>
</dependency>
```

### Migración 5.0.0 ###

- Se elimina `io.github.jcprieto.utilidades.Logger`.
- La librería solo expone SLF4J; la aplicación debe añadir un backend (Logback/Log4j2) para ver logs.

Configuración mínima Logback (app consumidora), archivo `src/main/resources/logback.xml`:

```xml

<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger - %msg%n</pattern>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="STDOUT"/>
  </root>
</configuration>
```

Configuración mínima Log4j2 (app consumidora), archivo `src/main/resources/log4j2.xml`:

```xml

<Configuration status="WARN">
  <Appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5level %logger - %msg%n"/>
    </Console>
  </Appenders>
  <Loggers>
    <Root level="info">
      <AppenderRef ref="Console"/>
    </Root>
  </Loggers>
</Configuration>
```

`getPremio`: en caso de error o datos no disponibles, devuelve un `Premio` con `cantidad=0`.

Precision en importes:

- `Premio#getCantidad()` devuelve el importe como `BigDecimal`.

Errores y estados:

- `getResumenNavidad` y `getResumenNino` devuelven `null` si no hay datos o hay error de red.
- `getPremio` devuelve un `Premio` con `cantidad=0` y `estado=NO_INICIADO` cuando no hay datos o hay error.

### Dependencia (Maven Central) ###

Maven:

```xml
<dependency>
  <groupId>io.github.jcprieto</groupId>
  <artifactId>loteria-navidad</artifactId>
    <version>6.0.1</version>
</dependency>
```

Gradle:

```gradle
implementation "io.github.jcprieto:loteria-navidad:6.0.1"
```

### Changelog ###

Consulta el historial de versiones en [`CHANGELOG.md`](CHANGELOG.md).
