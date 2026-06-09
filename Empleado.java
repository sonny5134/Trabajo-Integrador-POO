// Importamos las librerías necesarias para manejar fechas y cálculos temporales
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// ==========================================
// CLASE ANTIGUEDAD
// ==========================================
class Antiguedad {
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso;

    // Constructor que recibe las fechas de ingreso y egreso para calcular la antigüedad
    public Antiguedad(LocalDate fechaIngreso, LocalDate fechaEgreso) {
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
    }
    // Método para calcular la antigüedad en días, sumando 1 para incluir el día de egreso
    public long AntiguedadEnDias() {
        return ChronoUnit.DAYS.between(fechaIngreso, fechaEgreso) + 1;
    }

    // Método para calcular la antigüedad en años completos entre las fechas de ingreso y egreso
    public int AntiguedadEnAnios() {
        return (int) ChronoUnit.YEARS.between(fechaIngreso, fechaEgreso);
    }

    /**
     * Aplica la regla del Art. 245: Fracción mayor a 3 meses (90 días) 
     * incrementa en 1 los años a indemnizar.
     */
    public int calcularAniosLiquidacion() {
        int aniosCompletos = AntiguedadEnAnios();
        long diasRemanentes = AntiguedadEnDias() % 365;

        if (diasRemanentes > 90 || (aniosCompletos == 0 && AntiguedadEnDias() > 90)) {
            return aniosCompletos + 1;
        }
        return aniosCompletos;
    }
}

// ==========================================
// CLASE EMPLEADO
// ==========================================
// Clase Empleado que representa a un empleado con su salario bruto, fecha de ingreso y fecha de egreso
public class Empleado {
    private double sueldoBruto;
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso;
    private boolean preaviso;
    private Antiguedad antiguedad;
    // Constructor de la clase Empleado
    public Empleado(double sueldoBruto, LocalDate fechaIngreso, LocalDate fechaEgreso, boolean preaviso) {
        this.sueldoBruto = sueldoBruto;
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
        this.preaviso = preaviso;
        this.antiguedad = new Antiguedad(fechaIngreso, fechaEgreso);// Inicializamos la antigüedad al crear el empleado
    }
    // Getters para acceder a los atributos privados
    public double getSueldoBruto() {
        return sueldoBruto;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public LocalDate getFechaEgreso() {
        return fechaEgreso;
    }
    // Por convención estandar en booleanos se usa is en el getter
    public boolean isPreaviso() {
        return preaviso;
    }

    public Antiguedad getAntiguedad() {
        return antiguedad;
    }
}

// ==========================================
// CLASE PREAVISO
// ==========================================
class Preaviso {
    private Empleado empleado;

    // Constructor que recibe un empleado para calcular su preaviso
    public Preaviso(Empleado empleado) {
        this.empleado = empleado;
    }

    public int calcularMesesPreaviso() {
        int anios = empleado.getAntiguedad().AntiguedadEnAnios();
        if (anios < 5) {
            return 1;// 1 mes de preaviso para empleados con menos de 5 años de antigüedad
        } else {
            return 2;// 2 meses de preaviso para empleados con 5 o más años de antigüedad
        }
    }

    // Método para calcular el monto del preaviso según la legislación vigente
    public double calcularMontoPreaviso() {
        if (empleado.isPreaviso()) {
            return 0.0; // No corresponde preaviso
        } else {
            return empleado.getSueldoBruto() * calcularMesesPreaviso();// Monto del preaviso basado en el sueldo bruto y los meses correspondientes
        }
    }
}

// ==========================================
// CLASE VACACIONES PROPORCIONALES
// ==========================================
class VacacionesProporcionales {
    private Empleado empleado;
    private Antiguedad antiguedad;

    // Constructor que recibe un empleado y su antigüedad para calcular las vacaciones proporcionales
    public VacacionesProporcionales(Empleado empleado, Antiguedad antiguedad) {
        this.empleado = empleado;
        this.antiguedad = antiguedad;
    }

    // Método para determinar los días base de vacaciones por ley según la antigüedad del empleado
    public int diasBasePorLey() {
        int anios = empleado.getAntiguedad().AntiguedadEnAnios();
        if (anios <= 5){
            return 14;
        } else if (anios <= 10) {
            return 21;
        } else if (anios <= 20) {
            return 28;
        } else {
            return 35;
        }
    }

    // Método para calcular los días proporcionales de vacaciones basados en los días trabajados en el año del egreso
    public double calcularDiasProporcionales() {
        LocalDate egreso = empleado.getFechaEgreso();
        LocalDate inicioAnio = LocalDate.of(egreso.getYear(), 1, 1);
        
        long diasTrabajadosAnio = ChronoUnit.DAYS.between(inicioAnio, egreso) + 1;
        int totalDiasAnio = java.time.Year.of(egreso.getYear()).isLeap() ? 366 : 365;

        // Cálculo proporcional de días de vacaciones basado en los días trabajados en el año y los días base por ley según la antigüedad
        return ((double) diasTrabajadosAnio * diasBasePorLey()) / totalDiasAnio;
    }

    // Método para calcular el monto proporcional de las vacaciones
    public double calcularMontoProporcional() {
        double valorDiaVacacional = empleado.getSueldoBruto() / 25.0;// Valor del día vacacional según la legislación vigente (1/25 del sueldo bruto)
        return calcularDiasProporcionales() * valorDiaVacacional;
    }
}

// ==========================================
// CLASE SAC PROPORCIONAL (AGUINALDO)
// ==========================================
class SACProporcional {
    private Empleado empleado;

    // Constructor que recibe un empleado para calcular su SAC proporcional
    public SACProporcional(Empleado empleado) {
        this.empleado = empleado;
    }

    // Método para determinar el inicio del semestre en curso según la fecha de egreso del empleado
    public LocalDate calcularInicioSemestre() {
        LocalDate egreso = empleado.getFechaEgreso();
        // Determina el inicio del semestre en curso según la fecha de egreso
        if (egreso.getMonthValue() <= 6) {
            return LocalDate.of(egreso.getYear(), 1, 1);
        } else {
            return LocalDate.of(egreso.getYear(), 7, 1);
        }
    }

    /**
     * Cuenta los días transcurridos dentro del semestre en curso.
     */
    public long calcularDiasTrabajadosEnSemestre() {
        return ChronoUnit.DAYS.between(calcularInicioSemestre(), empleado.getFechaEgreso()) + 1;
    }

    /**
     * Aplica la fórmula tradicional sobre la base de 180 días.
     */
    public double calcularMontoProporcional() {
        long diasSemestre = calcularDiasTrabajadosEnSemestre();
        double medioAguinaldoBase = empleado.getSueldoBruto() / 2.0;
        return (medioAguinaldoBase * diasSemestre) / 180.0;
    }
}

// ==========================================
// CLASE ABSTRACTA PADRE: INDEMNIZACIÓN
// ==========================================
// Clase abstracta que define la estructura para calcular indemnizaciones
abstract class Indemnizacion {
    protected Empleado empleado;
    protected Preaviso preaviso;
    protected VacacionesProporcionales vacacionesProporcionales;
    protected SACProporcional sacProporcional;

    // Constructor que recibe un empleado para calcular su indemnización
    public Indemnizacion(Empleado empleado, Preaviso preaviso, VacacionesProporcionales vacacionesProporcionales, SACProporcional sacProporcional) {
        this.empleado = empleado;
        this.preaviso = preaviso;
        this.vacacionesProporcionales = vacacionesProporcionales;
        this.sacProporcional = sacProporcional;
    }

    // Método abstracto que cada hijo resolverá según su marco normativo
    public abstract double calcularIndemnizacion();
}

// ==========================================================
// CLASE HIJA: INDEMNIZACION CON REFORMA LABORAL 
// ==========================================================
class IndemnizacionConReforma extends Indemnizacion {

    public IndemnizacionConReforma(Empleado empleado, Preaviso preaviso, VacacionesProporcionales vacacionesProporcionales, SACProporcional sacProporcional) {
        super(empleado, preaviso, vacacionesProporcionales, sacProporcional);
    }

    @Override
    public double calcularIndemnizacion() {
        double baseSalarialConReforma = empleado.getSueldoBruto() * empleado.getAntiguedad().calcularAniosLiquidacion();
        return baseSalarialConReforma + preaviso.calcularMontoPreaviso() + vacacionesProporcionales.calcularMontoProporcional() + sacProporcional.calcularMontoProporcional();
    }       
}

// ==========================================================
// CLASE HIJA: INDEMNIZACION SIN REFORMA LABORAL (LCT Art. 245)
// ==========================================================
// Implementación de la indemnización bajo el régimen tradicional
class IndemnizacionSinReforma extends Indemnizacion {
    public IndemnizacionSinReforma(Empleado empleado, Preaviso preaviso, VacacionesProporcionales vacacionesProporcionales, SACProporcional sacProporcional) {
        super(empleado, preaviso, vacacionesProporcionales, sacProporcional);
    }

    @Override
    public double calcularIndemnizacion() {
        double baseSalarialSinReforma = empleado.getSueldoBruto() + preaviso.calcularMontoPreaviso() + vacacionesProporcionales.calcularMontoProporcional() + sacProporcional.calcularMontoProporcional();
        return baseSalarialSinReforma + preaviso.calcularMontoPreaviso() + vacacionesProporcionales.calcularMontoProporcional() + sacProporcional.calcularMontoProporcional();
    }
}

// ==========================================
// 5. CLASE PRINCIPAL / EJECUCIÓN
// ==========================================
public class SistemaLiquidacion {
    public static void main(String[] args) {
        
        // 1. Creamos al empleado con sus datos históricos y salariales
        Empleado emp = new Empleado(
            "Lionel Messi", 
            1200000.00, 
            LocalDate.of(2022, 5, 10), // Fecha Ingreso
            LocalDate.of(2025, 11, 20)  // Fecha Despido (~3 años y 6 meses)
        );

        // 2. Polimorfismo: Evaluamos al mismo empleado bajo ambos sistemas normativos
        Indemnizacion calculoTradicional = new IndemnizacionSinReforma(emp, new Preaviso(8.0));
        Indemnizacion calculoModerno = new IndemnizacionConReforma(emp, new Preaviso(8.0));

        // 3. Imprimir el reporte comparativo por pantalla
        System.out.println("==========================================================");
        System.out.println("     CALCULADORA DE INDEMNIZACIONES LABORALES ARGENTINA   ");
        System.out.println("==========================================================");
        System.out.printf("Empleado: %s%n", emp.getNombre());
        System.out.printf("Mejor Sueldo (Base): $%.2f%n", emp.getMejorRemuneracion());
        System.out.printf("Tiempo de servicio: %d días%n", emp.calcularDiasTrabajados());
        System.out.println("----------------------------------------------------------");

        // Bloque Sin Reforma
        IndemnizacionSinReforma sinRef = (IndemnizacionSinReforma) calculoTradicional;
        System.out.println(">>> RÉGIMEN TRADICIONAL (LCT Art. 245):");
        System.out.printf("    Años liquidados: %d%n", sinRef.calcularAniosAntiguedad());
        System.out.printf("    Liquidación final: $%.2f%n", calculoTradicional.calcularIndemnizacion());
        
        System.out.println("----------------------------------------------------------");

        // Bloque Con Reforma
        IndemnizacionConReforma conRef = (IndemnizacionConReforma) calculoModerno;
        System.out.println(">>> RÉGIMEN REFORMA LABORAL (Fondo de Cese):");
        System.out.printf("    Aporte mensual del empleador: %.1f%%%n", conRef.getPorcentajeAporteMensual());
        System.out.printf("    Fondo acumulado disponible: $%.2f%n", calculoModerno.calcularIndemnizacion());
        System.out.println("==========================================================");
    }
}