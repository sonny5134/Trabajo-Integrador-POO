package modelo;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class VacacionesProporcionales {
    private Empleado empleado;
    private Antiguedad aniosAntiguedad;

    public VacacionesProporcionales(Empleado empleado, Antiguedad antiguedad) {
      this.empleado = empleado;
      this.aniosAntiguedad = antiguedad;
    }

    public int diasBasePorLey() {
      int anios = aniosAntiguedad.AntiguedadEnAnios();
      if (anios <= 5) return 14;
      if (anios <= 10) return 21;
      if (anios <= 20) return 28;
      return 35;
      
    }

    public double calcularDiasProporcionales() {
      LocalDate egreso = empleado.getFechaEgreso();
      LocalDate inicioAnio = LocalDate.of(egreso.getYear(), 1, 1);
      long diasTrabajadosAnio = ChronoUnit.DAYS.between(inicioAnio, egreso) + 1; 
      int totalDiasAnio = egreso.isLeapYear() ? 366 : 365;
      return ((double) diasTrabajadosAnio * diasBasePorLey ()) / totalDiasAnio;
    }

    public double calcularMontoProporcional() {
      return calcularDiasProporcionales() * (empleado.getSueldoBruto() / 25.0);
    }
}
