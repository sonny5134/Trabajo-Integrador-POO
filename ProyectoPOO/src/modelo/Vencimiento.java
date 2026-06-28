package modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Vencimiento {
    private String descripcion;
    private LocalDate fecha;
    private boolean cumplido;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Vencimiento(String descripcion, LocalDate fecha) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.cumplido = false;
    }

    public String getDescripcion() { return descripcion; }
    public LocalDate getFecha()    { return fecha; }
    public boolean isCumplido()    { return cumplido; }

    public void setCumplido(boolean c) { this.cumplido = c; }
    public void setDescripcion(String d) { this.descripcion = d; }
    public void setFecha(LocalDate f)    { this.fecha = f; }

    public long diasRestantes() {
        return ChronoUnit.DAYS.between(LocalDate.now(), fecha);
    }

    public boolean estaVencido() {
        return !cumplido && fecha.isBefore(LocalDate.now());
    }

    public boolean venceProximamente(int dias) {
        long restantes = diasRestantes();
        return !cumplido && restantes >= 0 && restantes <= dias;
    }

    public String getFechaFormateada() { return fecha.format(FMT); }

    @Override
    public String toString() {
        String estado = cumplido ? "[OK]" : (estaVencido() ? "[VENCIDO]" : "[" + diasRestantes() + " dias]");
        return estado + " " + descripcion + " — " + getFechaFormateada();
    }
}
