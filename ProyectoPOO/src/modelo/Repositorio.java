package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Repositorio {

    private static Repositorio instancia;

    private Abogado abogadoActual;
    private List<Cliente> clientes;

    private Repositorio() {
        clientes = new ArrayList<>();
    }

    public static Repositorio getInstance() {
        if (instancia == null) instancia = new Repositorio();
        return instancia;
    }

    /**
     * Resetea completamente la sesion actual.
     * Se llama al hacer logout o al cambiar de abogado.
     * Los datos del abogado nuevo se cargan desde archivo por PersistenciaAbogado.
     */
    public static void resetearSesion() {
        instancia = new Repositorio();
    }

    // ---- Abogado ----
    public Abogado getAbogadoActual()       { return abogadoActual; }
    public void setAbogadoActual(Abogado a) { abogadoActual = a; }
    public boolean hayAbogadoRegistrado()   { return abogadoActual != null; }

    // ---- Clientes ----
    public List<Cliente> getClientes() { return clientes; }

    public Cliente agregarCliente(String nombre, String dni, String cuil,
                                  String telefono, String email, String domicilio) {
        String id = "CLI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Cliente c = new Cliente(id, nombre, dni, cuil, telefono, email, domicilio);
        clientes.add(c);
        return c;
    }

    public void eliminarCliente(Cliente c) { clientes.remove(c); }

    public Cliente buscarClientePorDni(String dni) {
        for (Cliente c : clientes)
            if (c.getDni().equals(dni)) return c;
        return null;
    }

    // ---- Casos ----
    public Caso agregarCaso(Cliente cliente, String tipo, String razonSocial,
                            String cuit, String domicilioEmp,
                            java.time.LocalDate ingreso,
                            java.time.LocalDate egreso,
                            double sueldo) {
        String id = "CASO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Caso caso = new Caso(id, tipo, razonSocial, cuit, domicilioEmp, ingreso, egreso, sueldo);
        cliente.agregarCaso(caso);
        return caso;
    }

    // ---- Estadísticas ----
    public int getTotalCasos() {
        int total = 0;
        for (Cliente c : clientes) total += c.getCasos().size();
        return total;
    }

    // ---- Alertas de vencimiento globales ----
    public List<String> getAlertasVencimiento(int diasUmbral) {
        List<String> alertas = new ArrayList<>();
        for (Cliente cli : clientes) {
            for (Caso caso : cli.getCasos()) {
                for (Vencimiento v : caso.getVencimientosProximos(diasUmbral)) {
                    String alerta =
                        (v.estaVencido() ? "VENCIDO" : "PROXIMO")
                        + " | " + cli.getNombreCompleto()
                        + " | " + caso.getRazonSocialEmpleador()
                        + " | " + v.getDescripcion()
                        + " | " + v.getFechaFormateada();
                    alertas.add(alerta);
                }
            }
        }
        return alertas;
    }
}