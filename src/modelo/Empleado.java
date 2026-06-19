package modelo;
import java.time.LocalDate;


public class Empleado {
    private String nombre;
    private double sueldoBruto;
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso;
    private boolean preaviso;
    private Antiguedad antiguedad;

    // Sobrecarga 1: Empleado desvinculado
    public Empleado(String nombre, double sueldoBruto, LocalDate fechaIngreso, LocalDate fechaEgreso, boolean preaviso) {
        this.nombre = nombre;
        this.sueldoBruto = sueldoBruto;
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
        this.preaviso = preaviso;
        this.antiguedad = new Antiguedad (fechaIngreso, fechaEgreso);
    }

    // Sobrecarga 2: Empleado activo (sin fecha de egreso)
    public Empleado(String nombre, double sueldoBruto, LocalDate fechaIngreso){
        this.nombre = nombre;
        this.sueldoBruto = sueldoBruto;
        this.fechaIngreso = fechaIngreso;
        this.preaviso = false; // Por defecto, un empleado activo no tiene preaviso
        this.fechaEgreso = null; // No hay fecha de egreso para un empleado activo
    }

    public String getNombre() { return nombre; }
    public double getSueldoBruto(){ return sueldoBruto; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public LocalDate getFechaEgreso() { return fechaEgreso; }
    public boolean isPreaviso() { return preaviso; }
    public Antiguedad getAntiguedad() { return antiguedad; }

}
