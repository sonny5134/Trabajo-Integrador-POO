package modelo;

public abstract class Indemnizacion {
    protected Empleado empleado;
    protected Preaviso preaviso;
    protected VacacionesProporcionales vacacionesProporcionales;
    protected SACProporcional sacProporcional;

    public Indemnizacion(Empleado empleado, Preaviso preaviso, VacacionesProporcionales vacaciones, SACProporcional sac) {
        this.empleado = empleado;
        this.preaviso = preaviso;
        this.vacacionesProporcionales = vacaciones;
        this.sacProporcional = sac;
    }

    public Empleado getEmpleado() { return empleado; }
    
    // Método polimórfico
    public abstract double calcularIndemnizacion();
}