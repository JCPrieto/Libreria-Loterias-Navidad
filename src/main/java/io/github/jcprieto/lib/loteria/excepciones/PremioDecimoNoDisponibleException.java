package io.github.jcprieto.lib.loteria.excepciones;

import java.io.IOException;

public class PremioDecimoNoDisponibleException extends IOException {
    public PremioDecimoNoDisponibleException(String message) {
        super(message);
    }
}
