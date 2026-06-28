package modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

// ==================== ANTIGUEDAD ====================
class Antiguedad {
    private LocalDate fechaIngreso, fechaEgreso;
    public Antiguedad(LocalDate fi, LocalDate fe) { fechaIngreso=fi; fechaEgreso=fe; }
    public long AntiguedadEnDias()  { return ChronoUnit.DAYS.between(fechaIngreso, fechaEgreso); }
    public int AntiguedadEnAnios()  { return (int) ChronoUnit.YEARS.between(fechaIngreso, fechaEgreso); }
    public int calcularAniosLiquidacion() {
        int a = AntiguedadEnAnios();
        long d = AntiguedadEnDias() % 365;
        if (d > 90 || (a == 0 && AntiguedadEnDias() > 90)) return a + 1;
        return a;
    }
}

// ==================== EMPLEADO LIQUIDACION ====================
class EmpleadoLiq {
    private String nombre;
    private double sueldoBruto;
    private LocalDate fechaIngreso, fechaEgreso;
    private boolean preaviso;
    private Antiguedad antiguedad;

    public EmpleadoLiq(String n, double s, LocalDate fi, LocalDate fe, boolean p) {
        nombre=n; sueldoBruto=s; fechaIngreso=fi; fechaEgreso=fe; preaviso=p;
        antiguedad = new Antiguedad(fi, fe);
    }
    public String getNombre()         { return nombre; }
    public double getSueldoBruto()    { return sueldoBruto; }
    public LocalDate getFechaEgreso() { return fechaEgreso; }
    public boolean isPreaviso()       { return preaviso; }
    public Antiguedad getAntiguedad() { return antiguedad; }
}

// ==================== PREAVISO ====================
class Preaviso {
    private EmpleadoLiq emp;
    public Preaviso(EmpleadoLiq e) { emp = e; }
    public int calcularMeses() { return emp.getAntiguedad().AntiguedadEnAnios() < 5 ? 1 : 2; }
    public double calcularMonto() {
        if (emp.isPreaviso()) return 0.0;
        return emp.getSueldoBruto() * calcularMeses();
    }
}

// ==================== SAC PROPORCIONAL ====================
class SACProporcional {
    private EmpleadoLiq emp;
    public SACProporcional(EmpleadoLiq e) { emp = e; }
    public LocalDate inicioSemestre() {
        LocalDate eg = emp.getFechaEgreso();
        return eg.getMonthValue() <= 6 ? LocalDate.of(eg.getYear(),1,1) : LocalDate.of(eg.getYear(),7,1);
    }
    public long diasEnSemestre() { return ChronoUnit.DAYS.between(inicioSemestre(), emp.getFechaEgreso()) + 1; }
    public double calcularMonto() { return ((emp.getSueldoBruto()/2.0) * diasEnSemestre()) / 180.0; }
}

// ==================== VACACIONES PROPORCIONALES ====================
class VacacionesProporcionales {
    private EmpleadoLiq emp;
    private Antiguedad ant;
    public VacacionesProporcionales(EmpleadoLiq e, Antiguedad a) { emp=e; ant=a; }
    public int diasBase() {
        int a = ant.AntiguedadEnAnios();
        if (a<=5) return 14; if (a<=10) return 21; if (a<=20) return 28; return 35;
    }
    public double calcularDias() {
        LocalDate eg = emp.getFechaEgreso();
        long dias = ChronoUnit.DAYS.between(LocalDate.of(eg.getYear(),1,1), eg) + 1;
        return ((double) dias * diasBase()) / (eg.isLeapYear() ? 366 : 365);
    }
    public double calcularMonto() { return calcularDias() * (emp.getSueldoBruto()/25.0); }
}

// ==================== INDEMNIZACION (abstracta) ====================
abstract class Indemnizacion {
    protected EmpleadoLiq emp;
    protected Preaviso pre;
    protected VacacionesProporcionales vac;
    protected SACProporcional sac;
    public Indemnizacion(EmpleadoLiq e, Preaviso p, VacacionesProporcionales v, SACProporcional s) {
        emp=e; pre=p; vac=v; sac=s;
    }
    public EmpleadoLiq getEmpleado() { return emp; }
    public abstract double calcularBase();
    public double calcularTotal() { return calcularBase()+pre.calcularMonto()+vac.calcularMonto()+sac.calcularMonto(); }
    public double getPreaviso()   { return pre.calcularMonto(); }
    public double getVacaciones() { return vac.calcularMonto(); }
    public double getSAC()        { return sac.calcularMonto(); }
}

// ==================== SIN REFORMA ====================
class IndemnizacionSinReforma extends Indemnizacion {
    public IndemnizacionSinReforma(EmpleadoLiq e, Preaviso p, VacacionesProporcionales v, SACProporcional s) { super(e,p,v,s); }
    public double calcularBase() { return (emp.getSueldoBruto()+emp.getSueldoBruto()/12)*emp.getAntiguedad().calcularAniosLiquidacion(); }
}

// ==================== CON REFORMA ====================
class IndemnizacionConReforma extends Indemnizacion {
    public IndemnizacionConReforma(EmpleadoLiq e, Preaviso p, VacacionesProporcionales v, SACProporcional s) { super(e,p,v,s); }
    public double calcularBase() { return emp.getSueldoBruto()*emp.getAntiguedad().calcularAniosLiquidacion(); }
}

// ==================== ACCIDENTE ART (Abstracta) ====================
abstract class CalculadoraART {
    protected static final double PISO_2026 = 97502420.0;
    protected String nombre; 
    protected double ibm; 
    protected int edad;
    protected double pctIncap;

    public CalculadoraART(String n, double i, int e, double p) {
        this.nombre = n; 
        this.ibm = i; 
        this.edad = e; 
        this.pctIncap = p;
    }
    
    public double formula() { 
        return 53.0 * ibm * (pctIncap / 100.0) * (65.0 / edad); 
    }
    
    public double piso() { 
        return PISO_2026 * (pctIncap / 100.0); 
    }
    
    public double base() { 
        return Math.max(formula(), piso()); 
    }
    
    // Métodos abstractos que definirán las clases hijas
    public abstract double iapu();
    public abstract String getTipoSiniestro();
    
    public double total() { 
        return base() + iapu(); 
    }
    
    // Getters comunes
    public String getNombre()  { return nombre; }
    public double getIBM()     { return ibm; }
    public int getEdad()       { return edad; }
    public double getPct()     { return pctIncap; }
}

// ==================== ACCIDENTE EN EL TRABAJO ====================
class AccidenteLaboral extends CalculadoraART {
    public AccidenteLaboral(String n, double i, int e, double p) { 
        super(n, i, e, p); 
    }
    
    @Override
    public double iapu() { 
        // Los accidentes dentro del establecimiento llevan el 20% adicional (L. 26.773)
        return base() * 0.20; 
    }
    
    @Override
    public String getTipoSiniestro() { 
        return "Accidente Laboral / Enf. Profesional"; 
    }
}

// ==================== ACCIDENTE IN ITINERE ====================
class AccidenteInItinere extends CalculadoraART {
    public AccidenteInItinere(String n, double i, int e, double p) { 
        super(n, i, e, p); 
    }
    
    @Override
    public double iapu() { 
        // Los accidentes de trayecto (in itinere) están excluidos del adicional del 20%
        return 0.0; 
    }
    
    @Override
    public String getTipoSiniestro() { 
        return "Accidente In Itinere"; 
    }
}

// ==================== FABRICA DE LIQUIDACIONES ====================
public class FabricaLiquidacion {

    public static String calcularDespido(String nombre, double sueldo,
            LocalDate ingreso, LocalDate egreso, boolean preaviso, boolean conReforma) {
        EmpleadoLiq emp = new EmpleadoLiq(nombre, sueldo, ingreso, egreso, preaviso);
        Preaviso pre = new Preaviso(emp);
        VacacionesProporcionales vac = new VacacionesProporcionales(emp, emp.getAntiguedad());
        SACProporcional sac = new SACProporcional(emp);
        Indemnizacion ind = conReforma
            ? new IndemnizacionConReforma(emp, pre, vac, sac)
            : new IndemnizacionSinReforma(emp, pre, vac, sac);

        return String.format(
            "  Empleado        : %s\n" +
            "  Regimen         : %s\n" +
            "  Sueldo          : $%,.2f\n" +
            "  Antiguedad      : %d anos\n" +
            "  ----------------------------\n" +
            "  Indem. base     : $%,.2f\n" +
            "  Preaviso        : $%,.2f\n" +
            "  Vacaciones prop.: $%,.2f\n" +
            "  SAC proporcional: $%,.2f\n" +
            "  ----------------------------\n" +
            "  TOTAL           : $%,.2f\n",
            emp.getNombre(),
            conReforma ? "Con Reforma Laboral" : "Sin Reforma (LCT Art 245)",
            emp.getSueldoBruto(),
            emp.getAntiguedad().calcularAniosLiquidacion(),
            ind.calcularBase(), ind.getPreaviso(),
            ind.getVacaciones(), ind.getSAC(),
            ind.calcularTotal()
        );
    }

    public static double calcularTotalDespido(String nombre, double sueldo,
            LocalDate ingreso, LocalDate egreso, boolean preaviso, boolean conReforma) {
        EmpleadoLiq emp = new EmpleadoLiq(nombre, sueldo, ingreso, egreso, preaviso);
        Preaviso pre = new Preaviso(emp);
        VacacionesProporcionales vac = new VacacionesProporcionales(emp, emp.getAntiguedad());
        SACProporcional sac = new SACProporcional(emp);
        Indemnizacion ind = conReforma
            ? new IndemnizacionConReforma(emp, pre, vac, sac)
            : new IndemnizacionSinReforma(emp, pre, vac, sac);
        return ind.calcularTotal();
    }

    public static String calcularART(String nombre, double ibm, int edad, double pct, boolean esLaboral) {
        // Fábrica polimórfica según la bandera recibida desde la vista
        CalculadoraART c = esLaboral 
            ? new AccidenteLaboral(nombre, ibm, edad, pct)
            : new AccidenteInItinere(nombre, ibm, edad, pct);
            
        String criterio = c.formula() >= c.piso() ? "Formula Legal" : "Piso Minimo (Res. SRT 15/2026)";
        
        return String.format(
            "  Trabajador      : %s\n" +
            "  Siniestro       : %s\n" +
            "  IBM             : $%,.2f\n" +
            "  Edad            : %d anos\n" +
            "  Incapacidad     : %.1f%%\n" +
            "  ----------------------------\n" +
            "  Formula legal   : $%,.2f\n" +
            "  Piso min. 2026  : $%,.2f\n" +
            "  Criterio        : %s\n" +
            "  Base            : $%,.2f\n" +
            "  IAPU (20%%)     : $%,.2f\n" +
            "  ----------------------------\n" +
            "  TOTAL ESTIMADO  : $%,.2f\n" +
            "  (*) Resultado orientativo.\n",
            c.getNombre(),
            c.getTipoSiniestro(), // Dinámico por subclase
            c.getIBM(), c.getEdad(), c.getPct(),
            c.formula(), c.piso(), criterio,
            c.base(), 
            c.iapu(),             // Dinámico por subclase ($0 o 20%)
            c.total()
        );
    }

    public static double calcularTotalART(double ibm, int edad, double pct, boolean esLaboral) {
        CalculadoraART c = esLaboral 
            ? new AccidenteLaboral("", ibm, edad, pct)
            : new AccidenteInItinere("", ibm, edad, pct);
        return c.total();
    }
}