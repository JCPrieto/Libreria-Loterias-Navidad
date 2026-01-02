package io.github.jcprieto.lib.loteria.model.json.navidad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PremioDecimoResponse {

    private int importePorDefecto;
    private List<PremioDetalle> compruebe;

    public int getImportePorDefecto() {
        return importePorDefecto;
    }

    public void setImportePorDefecto(int importePorDefecto) {
        this.importePorDefecto = importePorDefecto;
    }

    public List<PremioDetalle> getCompruebe() {
        return compruebe;
    }

    public void setCompruebe(List<PremioDetalle> compruebe) {
        this.compruebe = compruebe;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PremioDetalle {
        private String decimo;
        private long prize;

        public String getDecimo() {
            return decimo;
        }

        public void setDecimo(String decimo) {
            this.decimo = decimo;
        }

        public long getPrize() {
            return prize;
        }

        public void setPrize(long prize) {
            this.prize = prize;
        }
    }
}
