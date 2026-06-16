package modelo;
import java.util.ArrayList;
import java.util.List;

public class Empresa {
    private String razonSocial;
    private List<Empleado> empleados;

    public Empresa(String razonSocial) {
        this.razonSocial = razonSocial;
        this.empleados = new ArrayList<>();
    }

    public void altaEmpleado(Empleado emp) {
        this.empleados.add(emp);
    }

    public List<Empleado> obtenerTodosLosEmpleados() {
        return this.empleados;
    }
}