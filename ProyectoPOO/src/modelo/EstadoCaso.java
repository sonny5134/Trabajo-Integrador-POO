package modelo;

public enum EstadoCaso {
    INTERCAMBIO_TELEGRAFICO("En Intercambio Telegrafico"),
    MEDIACION("En Mediacion (SECLO)"),
    JUICIO("En Juicio"),
    SENTENCIA("Con Sentencia / Acuerdo");

    private final String etiqueta;

    EstadoCaso(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}
