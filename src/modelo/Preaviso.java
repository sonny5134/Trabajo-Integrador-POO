package modelo;

public class Preaviso {
    private Empleado empleado;

    public Preaviso(Empleado empleado) {
        this.empleado = empleado;
    }

    public int calcularMesesPreaviso() {
      if (empleado.getAntiguedad().AntiguedadEnAnios() < 5) return 1;
      return 2;
    }

    public double calcularMontoPreaviso() {
      if (empleado.isPreaviso()) return 0.0;
      return empleado.getSueldoBruto() * calcularMesesPreaviso();
    }
}
