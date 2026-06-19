package modelo;

public class ReciboLiquidacion {
    private Indemnizacion indemnizacion;
    private String tipoRegimen;
    private Empresa empresa;

    public ReciboLiquidacion(Indemnizacion indemnizacion, String tipoRegimen, Empresa empresa) {
        this.indemnizacion = indemnizacion;
        this.tipoRegimen = tipoRegimen;
        this.empresa = empresa;
        
    }

    public String generarReporte() {
        Empleado emp = indemnizacion.getEmpleado();
        return String.format(
            "==========================================\n" +
            "           %s                 \n" + // 👉 El %s es para la razón social
            "      RECIBO DE LIQUIDACIÓN FINAL         \n" +
            "==========================================\n" +
            "Empleado: %s\n" +
            "Régimen: %s\n" +
            "Sueldo Base: $%.2f\n" +
            "Antigüedad (Años para liquidar): %d\n" +
            "------------------------------------------\n" +
            "TOTAL INDEMNIZACIÓN: $%.2f\n" +
            "==========================================\n",
            empresa.getRazonSocial().toUpperCase(), // 👉 Llamamos al getter en mayúsculas
            emp.getNombre(), tipoRegimen, emp.getSueldoBruto(), 
            emp.getAntiguedad().calcularAniosLiquidacion(), 
            indemnizacion.calcularIndemnizacion()
        );
    }
}