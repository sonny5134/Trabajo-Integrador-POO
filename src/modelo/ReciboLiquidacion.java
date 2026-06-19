package modelo;

public class ReciboLiquidacion {
    private Indemnizacion indemnizacion;
    private String tipoRegimen;

    public ReciboLiquidacion(Indemnizacion indemnizacion, String tipoRegimen) {
        this.indemnizacion = indemnizacion;
        this.tipoRegimen = tipoRegimen;
    }

    public String generarReporte() {
        Empleado emp = indemnizacion.getEmpleado();
        return String.format(
            "==========================================\n" +
            "      RECIBO DE LIQUIDACIÓN FINAL         \n" +
            "==========================================\n" +
            "Empleado: %s\n" +
            "Régimen: %s\n" +
            "Sueldo Base: $%.2f\n" +
            "Antigüedad (Años para liquidar): %d\n" +
            "------------------------------------------\n" +
            "TOTAL INDEMNIZACIÓN: $%.2f\n" +
            "==========================================\n",
            emp.getNombre(), tipoRegimen, emp.getSueldoBruto(), 
            emp.getAntiguedad().calcularAniosLiquidacion(), 
            indemnizacion.calcularIndemnizacion()
        );
    }
}