package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EntradaBitacora {
    private LocalDateTime fechaHora;
    private String tipo;   // "NOVEDAD", "LLAMADA", "AUDIENCIA", "DOCUMENTO", "OTRO"
    private String descripcion;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EntradaBitacora(String tipo, String descripcion) {
        this.fechaHora = LocalDateTime.now();
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public EntradaBitacora(LocalDateTime fechaHora, String tipo, String descripcion) {
        this.fechaHora = fechaHora;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getTipo()             { return tipo; }
    public String getDescripcion()      { return descripcion; }

    public String getFechaFormateada()  { return fechaHora.format(FMT); }

    @Override
    public String toString() {
        return "[" + getFechaFormateada() + "] " + tipo + ": " + descripcion;
    }
}