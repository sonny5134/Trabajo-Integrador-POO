package modelo;

import java.io.File;

public class DocumentoAdjunto {
    public enum TipoDocumento {
        RECIBO_SUELDO("Recibo de Sueldo"),
        TELEGRAMA("Telegrama de Despido"),
        CONTRATO("Contrato Laboral"),
        WHATSAPP("Captura WhatsApp"),
        FOTO("Fotografia"),
        OTRO("Otro");

        private final String etiqueta;
        TipoDocumento(String e) { this.etiqueta = e; }
        public String getEtiqueta() { return etiqueta; }
        @Override public String toString() { return etiqueta; }
    }

    private String nombre;
    private TipoDocumento tipo;
    private String rutaArchivo;
    private String descripcion;

    public DocumentoAdjunto(String nombre, TipoDocumento tipo, String rutaArchivo, String descripcion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.rutaArchivo = rutaArchivo;
        this.descripcion = descripcion;
    }

    public String getNombre()       { return nombre; }
    public TipoDocumento getTipo()  { return tipo; }
    public String getRutaArchivo()  { return rutaArchivo; }
    public String getDescripcion()  { return descripcion; }

    public boolean existeArchivo()  { return new File(rutaArchivo).exists(); }

    @Override
    public String toString() { return tipo.getEtiqueta() + ": " + nombre; }
}
