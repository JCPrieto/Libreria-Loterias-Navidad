package io.github.jcprieto;

import io.github.jcprieto.lib.loteria.conexion.Conexion;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class ConexionCookieJarConcurrencyTest {

    private static Cookie cookie(int value) {
        return new Cookie.Builder()
                .name("cms")
                .value("v" + value)
                .domain("www.loteriasyapuestas.es")
                .path("/")
                .build();
    }

    private static CookieJar createInMemoryCookieJar() throws Exception {
        Class<?> jarClass = Arrays.stream(Conexion.class.getDeclaredClasses())
                .filter(it -> "InMemoryCookieJar".equals(it.getSimpleName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("InMemoryCookieJar no encontrado"));
        Constructor<?> ctor = jarClass.getDeclaredConstructor();
        ctor.setAccessible(true);
        return (CookieJar) ctor.newInstance();
    }

    private static String getCookieHeader(CookieJar jar) throws Exception {
        Method method = jar.getClass().getDeclaredMethod("getCookieHeader");
        method.setAccessible(true);
        return (String) method.invoke(jar);
    }

    @Test
    public void testInMemoryCookieJarSoportaAccesoConcurrente() throws Exception {
        CookieJar jar = createInMemoryCookieJar();
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse("https://www.loteriasyapuestas.es/"));
        ExecutorService pool = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);

        try {
            Future<?>[] futures = new Future[100];
            for (int i = 0; i < 100; i++) {
                final int value = i;
                futures[i] = pool.submit(() -> {
                    start.await();
                    jar.saveFromResponse(url, List.of(cookie(value)));
                    jar.loadForRequest(url);
                    return null;
                });
            }
            start.countDown();
            for (Future<?> future : futures) {
                future.get(5, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }

        String cookieHeader = getCookieHeader(jar);
        Assert.assertNotNull(cookieHeader);
        Assert.assertFalse(cookieHeader.isBlank());
    }
}
