# Changelog

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
