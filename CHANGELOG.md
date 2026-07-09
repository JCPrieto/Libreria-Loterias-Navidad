# Changelog

## 7.0.0

- Cambio rompedor: se elimina el soporte legacy de fechas de actualizacion basadas en `java.util.Date` para Android.
  Las fechas de actualizacion se exponen y calculan unicamente como `LocalDateTime`.
- Eliminados los accesores `getFechaActualizacionAndroid()` y `setFechaActualizacionAndroid(...)` de `Premio`,
  `ResumenNavidad` y `ResumenNino`.
- Simplificados los convertidores de premios y resumenes para usar directamente `LocalDateTime`, eliminando el fallback
  interno que capturaba `NoClassDefFoundError`.
- Cambio rompedor en `SorteoResponseConverterUtils`: los metodos de fecha ya no reciben `Consumer<Date>` y solo aceptan
  el setter de `LocalDateTime`.
- Actualizacion de mantenimiento: `jackson-databind` de `2.22.0` a `2.22.1`.
- Ampliacion y ajuste de tests unitarios de `PremioConverter` y `SorteoResponseConverterUtils` para reflejar el nuevo
  contrato sin fallback legacy.

## 6.0.12

- Actualizacion de mantenimiento: OpenFeign de `13.12` a `13.13` y `central-publishing-maven-plugin` de `0.10.0` a
  `0.11.0`
- Ajuste interno en `Conexion`: el calculo de la ultima fecha de sorteo usa explicitamente
  `Clock.systemDefaultZone()`, manteniendo el mismo comportamiento con la zona horaria por defecto del sistema
- Ampliacion de tests unitarios de `PremioConverter` para cubrir el caso de premio cero con importe por defecto valido
  detectado por SonarQube
- Sin cambios en API publica ni en comportamiento funcional de produccion de la libreria

## 6.0.11

- Actualizacion de mantenimiento: `jackson-databind` de `2.21.3` a `2.22.0`, `okhttp-jvm` y `mockwebserver` de
  `5.3.2` a `5.4.0`, y `jacoco-maven-plugin` de `0.8.14` a `0.8.15`
- Ajuste de `ConexionPremioSorteoSinIdTest` para conservar el nombre de cada caso parametrizado en los mensajes de
  asercion y evitar parametros sin uso detectados por SonarQube
- Ampliacion de tests unitarios de `ResumenNinoConverter` para cubrir respuestas nulas, URL base ausente y premios no
  disponibles
- Sin cambios en API publica ni en comportamiento funcional de produccion de la libreria

## 6.0.10

- Actualizacion de mantenimiento: `slf4j-api` de `2.0.17` a `2.0.18`
- Ampliacion del workflow de release para publicar tambien en Maven Central con el perfil `release-sign`, usando
  credenciales y clave GPG desde secretos de GitHub Actions antes de crear la release de GitHub
- Reorganizacion y ampliacion de tests unitarios de `Conexion`: se parametrizan los casos equivalentes sin `id_sorteo`
  y se cubren ramas adicionales de warm-up, cache de premios, errores HTTP/runtime, JSON prefijado, cookies y parseo
  defensivo
- Sin cambios en API publica ni en comportamiento funcional de produccion de la libreria

## 6.0.9

- Actualizacion de dependencias: OpenFeign de `13.11` a `13.12` y `jackson-databind` de `2.21.2` a `2.21.3`
- Reorganizacion de tests de `Conexion`: los casos equivalentes de respuestas no procesables pasan a un test
  parametrizado para eliminar duplicacion detectada por SonarQube
- Ampliacion de tests unitarios de `ResumenNavidadConverter` para cubrir ramas de numeros no disponibles, datos
  opcionales ausentes y recorrido completo de quintos premios
- Sin cambios en API publica ni en comportamiento funcional de produccion de la libreria

## 6.0.8

- Actualizacion de seguridad y mantenimiento: `jackson-databind` de `2.21.1` a `2.21.2`
- Ampliacion de `ConexionTest` para cubrir ramas de borde en premios y cabeceras CMS (`[]`, `id_sorteo` en blanco,
  `"E019"` y ausencia de `cookieJar`)
- Ajuste de higiene del repositorio: `.codex/` pasa a ignorarse en `.gitignore`
- Sin cambios en API publica ni en comportamiento funcional de produccion de la libreria

## 6.0.7

- Actualizacion de dependencia: OpenFeign de `13.9.3` a `13.11`
- Correccion interna en `Conexion`: se usa `DefaultRetryer` compatible con la version actual de Feign para mantener la
  politica de reintentos por defecto
- Nuevos tests unitarios en `Premio` para validar normalizacion de `BigDecimal` con valores nulos, ceros, escalas y
  escala negativa
- Sin cambios en API publica ni en el contrato funcional esperado de la libreria

## 6.0.6

- Actualizacion de dependencia: OpenFeign de `13.9` a `13.9.3` (fix de mantenimiento y seguridad)
- Refactor interno en `SorteoResponseConverterUtils` para simplificar condiciones redundantes y mejorar el manejo de
  fecha en fallback legacy
- Ampliacion de tests unitarios de `SorteoResponseConverterUtils` para cubrir ramas/condiciones reportadas por SonarQube
- Ajuste en `ConexionCookieJarConcurrencyTest` para evitar reflexion fragil por nombre de clase y validar tipo con
  `instanceof`
- Limpieza de modelo interno: eliminado constructor vacio explicito redundante en `Premios`
- Sin cambios en API publica ni en comportamiento funcional esperado de la libreria

## 6.0.5

- Actualizacion de dependencias: OpenFeign de `13.8` a `13.9` y `jackson-databind` de `2.21.0` a `2.21.1`
- Limpieza interna en `Conexion`: eliminado metodo privado no usado (`summarizeBody`) y simplificado el parseo de
  `PremioDecimoResponse`
- Nuevos tests unitarios para `EstadoSorteo` cubriendo mapeo de estados validos y no soportados
- Sin cambios en API publica ni en comportamiento funcional de la libreria

## 6.0.4

- Hotfix de seguridad en CI: la acción de release de GitHub (`softprops/action-gh-release`) queda fijada a SHA completo
- Actualización de mantenimiento en cobertura: `jacoco-maven-plugin` de `0.8.13` a `0.8.14`
- Ampliación de tests unitarios de `Conexion` para cookies CMS y rutas de borde/error en consulta de premios
- Ajuste interno menor en `Conexion` para reutilizar la constante `BASE_URL_SORTEOS` en la cabecera `Origin`
- Sin cambios en API publica ni en comportamiento funcional de la libreria

## 6.0.3

- Corregido el fallo de CI/SonarQube con Java 21 al actualizar JaCoCo de `0.8.7` a `0.8.13`
- Se mantiene la firma GPG solo para el flujo manual con perfil `release-sign`
- Sin cambios en API publica ni en comportamiento funcional de la libreria

## 6.0.2

- Ajuste de proceso de release: la firma GPG (`maven-gpg-plugin`) se mueve a un perfil manual `release-sign`
- `verify` deja de requerir clave privada GPG por defecto, evitando fallos en CI/SonarQube
- Sin cambios en API publica ni en comportamiento funcional de la libreria

## 6.0.1

- Mantenimiento interno en `Conexion`: `premioCache` pasa de `HashMap` a `EnumMap` para uso mas eficiente con `Sorteo`
- Limpieza de modelos JSON internos: eliminada la clase `Info` no usada y constructor vacio redundante en `Premios`
- Integracion de SonarQube en CI con perfil Maven `coverage` (JaCoCo XML) y badge de estado de calidad en README
- Actualizacion de OpenFeign de `13.6` a `13.8`

## 6.0.0

- Cambio rompedor: `Premio#getCantidad()` y `Premio#setCantidad(...)` pasan de `double` a `BigDecimal`
- Precision decimal en conversion de importes no exactos (escala 8 y normalizacion de ceros)
- Soporte documentado y probado para decimos con mas de 5 digitos (usa los ultimos 5)
- Nueva prueba de concurrencia para `InMemoryCookieJar`
- Actualizacion de `maven-compiler-plugin` a `3.15.0`

## 5.0.2

- Normalizacion y validacion unificadas para decimos
- Constantes de negocio para sentinels y divisor de premios

## 5.0.1

- Mejora el warm-up para reintentos tras fallo
- Cierre correcto de recursos HTTP
- Cookie jar seguro en concurrencia

## 5.0.0

- Cambio de logging a SLF4J (rompe compatibilidad)

## 4.0.0

- Integración de OpenFeign + OkHttp en la conexión HTTP
- Scraper de la web de Loterías y Apuestas del Estado
- Actualización a Java 21

## 3.3.7

- Correcciones de seguridad y estabilidad

## 3.3.6

- Correcciones de seguridad y estabilidad

## 3.3.5

- Correcciones de seguridad y estabilidad

## 3.3.4

- Correcciones de seguridad y estabilidad

## 3.3.3

- Correcciones de seguridad y estabilidad

## 3.3.2

- Actualización de seguridad de dependencias.
