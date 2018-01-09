package es.jklabs.lib.loteria.model.json.nino;

public class Premios {

    private long timestamp;
    private int status;
    private String fraseTexto;
    private String pdfURL;
    private int error;
    private int premio1;
    private int premio2;
    private int premio3;
    private String[] extracciones4cifras;
    private String[] extracciones3cifras;
    private String[] extracciones2cifras;
    private String[] reintegros;

    public Premios() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFraseTexto() {
        return fraseTexto;
    }

    public void setFraseTexto(String fraseTexto) {
        this.fraseTexto = fraseTexto;
    }

    public String getPdfURL() {
        return pdfURL;
    }

    public void setPdfURL(String pdfURL) {
        this.pdfURL = pdfURL;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getPremio1() {
        return premio1;
    }

    public void setPremio1(int premio1) {
        this.premio1 = premio1;
    }

    public int getPremio2() {
        return premio2;
    }

    public void setPremio2(int premio2) {
        this.premio2 = premio2;
    }

    public int getPremio3() {
        return premio3;
    }

    public void setPremio3(int premio3) {
        this.premio3 = premio3;
    }

    public String[] getExtracciones4cifras() {
        return extracciones4cifras;
    }

    public void setExtracciones4cifras(String[] extracciones4cifras) {
        this.extracciones4cifras = extracciones4cifras;
    }

    public String[] getExtracciones3cifras() {
        return extracciones3cifras;
    }

    public void setExtracciones3cifras(String[] extracciones3cifras) {
        this.extracciones3cifras = extracciones3cifras;
    }

    public String[] getExtracciones2cifras() {
        return extracciones2cifras;
    }

    public void setExtracciones2cifras(String[] extracciones2cifras) {
        this.extracciones2cifras = extracciones2cifras;
    }

    public String[] getReintegros() {
        return reintegros;
    }

    public void setReintegros(String[] reintegros) {
        this.reintegros = reintegros;
    }
}