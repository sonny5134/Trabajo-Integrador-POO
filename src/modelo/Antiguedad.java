package modelo;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Antiguedad {
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso;
    
    public Antiguedad(LocalDate fechaIngreso, LocalDate fechaEgreso) {
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
    }

    public long AntiguedadEnDias() {
      return ChronoUnit.DAYS.between(fechaIngreso, fechaEgreso);
    }

    public int AntiguedadEnAnios() {
      return (int) ChronoUnit.YEARS.between(fechaIngreso, fechaEgreso);
    }

    public int calcularAniosLiquidacion() {
      int aniosCompletos = AntiguedadEnAnios();
      long diasRemanentes = AntiguedadEnDias() % 365;
      if (diasRemanentes > 90 || (aniosCompletos == 0 && AntiguedadEnDias() > 90)) {
          return aniosCompletos + 1; // Si hay más de 90 días remanentes, se cuenta como un año adicional
      }
    }
}
