package es.jklabs.lib.loteria.enumeradores;

public enum EstadoSorteo {
    NO_INICIADO, EN_PROCESO, TERMINADO_PROVISIONAL, TERMINADO, TERMINADO_OFICIAL;

    public static EstadoSorteo get(int status) {
        EstadoSorteo retorno = null;
        switch (status) {
            case 0:
                retorno = EstadoSorteo.NO_INICIADO;
                break;
            case 1:
                retorno = EstadoSorteo.EN_PROCESO;
                break;
            case 2:
                retorno = EstadoSorteo.TERMINADO_PROVISIONAL;
                break;
            case 3:
                retorno = EstadoSorteo.TERMINADO;
                break;
            case 4:
                retorno = EstadoSorteo.TERMINADO_OFICIAL;
                break;
            default:
                break;
        }
        return retorno;
    }
}
