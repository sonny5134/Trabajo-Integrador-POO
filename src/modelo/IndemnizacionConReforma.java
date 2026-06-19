package modelo;

public class IndemnizacionConReforma extends Indemnizacion {
    public IndemnizacionConReforma(Empleado emp, Preaviso pre, VacacionesProporcionales vac, SACProporcional sac) {
        super(emp, pre, vac, sac);
    }

    @Override
    public double calcularIndemnizacion() {
        double base = empleado.getSueldoBruto() * empleado.getAntiguedad().calcularAniosLiquidacion();
        return base + preaviso.calcularMontoPreaviso() + vacacionesProporcionales.calcularMontoProporcional() + sacProporcional.calcularMontoProporcional();
    }
}