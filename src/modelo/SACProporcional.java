package modelo;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SACProporcional {
    private Empleado empleado;

    public SACProporcional(Empleado empleado) {
        this.empleado = empleado;
    }

    public LocalDate calcularInicioSemestre() {
        LocalDate egreso = empleado.getFechaEgreso();
        if (egreso.getMonthValue() <= 6) return LocalDate.of(egreso.getYear(), 1, 1);
        return LocalDate.of(egreso.getYear(), 7, 1);
    }

    public long calcularDiasTrabajadosEnSemestre() {
        return ChronoUnit.DAYS.between(calcularInicioSemestre(), empleado.getFechaEgreso()) + 1;
    }

    public double calcularMontoProporcional() {
        return ((empleado.getSueldoBruto() / 2.0) * calcularDiasTrabajadosEnSemestre()) / 180.0;
    }
}