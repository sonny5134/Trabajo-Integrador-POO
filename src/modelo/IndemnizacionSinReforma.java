package modelo;

public class IndemnizacionSinReforma extends Indemnizacion {
    public IndemnizacionSinReforma(Empleado emp, Preaviso pre, VacacionesProporcionales vac, SACProporcional sac) {
        super(emp, pre, vac, sac);
    }

    @Override
    public double calcularIndemnizacion() {
        double base = (empleado.getSueldoBruto() + (empleado.getSueldoBruto() / 12)) * empleado.getAntiguedad().calcularAniosLiquidacion();
        return base + preaviso.calcularMontoPreaviso() + vacacionesProporcionales.calcularMontoProporcional() + sacProporcional.calcularMontoProporcional();
    }
}