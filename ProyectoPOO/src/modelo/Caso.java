package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Caso {
    private String id;
    private String tipoCaso;          // "DESPIDO", "ART", "OTRO"
    private EstadoCaso estado;

    // Datos del empleador
    private String razonSocialEmpleador;
    private String cuitEmpleador;
    private String domicilioEmpleador;

    // Datos laborales
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso;
    private double ultimoSueldo;
    private boolean preavisoOtorgado;

    // Listas
    private List<Testigo> testigos;
    private List<EntradaBitacora> bitacora;
    private List<Vencimiento> vencimientos;
    private List<DocumentoAdjunto> documentos;

    // Montos calculados (se guardan para el resumen)
    private double montoReclamado;
    private String observacionesGenerales;

    public Caso(String id, String tipoCaso, String razonSocialEmpleador, String cuitEmpleador,
                String domicilioEmpleador, LocalDate fechaIngreso, LocalDate fechaEgreso, double ultimoSueldo) {
        this.id = id;
        this.tipoCaso = tipoCaso;
        this.razonSocialEmpleador = razonSocialEmpleador;
        this.cuitEmpleador = cuitEmpleador;
        this.domicilioEmpleador = domicilioEmpleador;
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
        this.ultimoSueldo = ultimoSueldo;
        this.estado = EstadoCaso.INTERCAMBIO_TELEGRAFICO;
        this.preavisoOtorgado = false;
        this.testigos = new ArrayList<>();
        this.bitacora = new ArrayList<>();
        this.vencimientos = new ArrayList<>();
        this.documentos = new ArrayList<>();
        this.montoReclamado = 0.0;
        this.observacionesGenerales = "";

        // Entrada inicial en bitacora
        agregarEntradaBitacora("NOVEDAD", "Caso creado. Estado inicial: " + estado.getEtiqueta());
    }

    // ---- Bitacora ----
    public void agregarEntradaBitacora(String tipo, String descripcion) {
        bitacora.add(new EntradaBitacora(tipo, descripcion));
    }

    // ---- Estado Kanban ----
    public void avanzarEstado() {
        EstadoCaso[] estados = EstadoCaso.values();
        int idx = estado.ordinal();
        if (idx < estados.length - 1) {
            estado = estados[idx + 1];
            agregarEntradaBitacora("NOVEDAD", "Estado cambiado a: " + estado.getEtiqueta());
        }
    }

    public void retrocederEstado() {
        EstadoCaso[] estados = EstadoCaso.values();
        int idx = estado.ordinal();
        if (idx > 0) {
            estado = estados[idx - 1];
            agregarEntradaBitacora("NOVEDAD", "Estado retrocedido a: " + estado.getEtiqueta());
        }
    }

    public void setEstado(EstadoCaso e) {
        this.estado = e;
        agregarEntradaBitacora("NOVEDAD", "Estado cambiado a: " + e.getEtiqueta());
    }

    // ---- Vencimientos proximos ----
    public List<Vencimiento> getVencimientosProximos(int dias) {
        List<Vencimiento> proximos = new ArrayList<>();
        for (Vencimiento v : vencimientos) {
            if (v.venceProximamente(dias) || v.estaVencido()) proximos.add(v);
        }
        return proximos;
    }

    // ---- Getters ----
    public String getId()                        { return id; }
    public String getTipoCaso()                  { return tipoCaso; }
    public EstadoCaso getEstado()                { return estado; }
    public String getRazonSocialEmpleador()      { return razonSocialEmpleador; }
    public String getCuitEmpleador()             { return cuitEmpleador; }
    public String getDomicilioEmpleador()        { return domicilioEmpleador; }
    public LocalDate getFechaIngreso()           { return fechaIngreso; }
    public LocalDate getFechaEgreso()            { return fechaEgreso; }
    public double getUltimoSueldo()              { return ultimoSueldo; }
    public boolean isPreavisoOtorgado()          { return preavisoOtorgado; }
    public List<Testigo> getTestigos()           { return testigos; }
    public List<EntradaBitacora> getBitacora()   { return bitacora; }
    public List<Vencimiento> getVencimientos()   { return vencimientos; }
    public List<DocumentoAdjunto> getDocumentos(){ return documentos; }
    public double getMontoReclamado()            { return montoReclamado; }
    public String getObservaciones()             { return observacionesGenerales; }

    // ---- Setters ----
    public void setRazonSocialEmpleador(String r){ this.razonSocialEmpleador = r; }
    public void setCuitEmpleador(String c)       { this.cuitEmpleador = c; }
    public void setDomicilioEmpleador(String d)  { this.domicilioEmpleador = d; }
    public void setFechaIngreso(LocalDate f)     { this.fechaIngreso = f; }
    public void setFechaEgreso(LocalDate f)      { this.fechaEgreso = f; }
    public void setUltimoSueldo(double s)        { this.ultimoSueldo = s; }
    public void setPreavisoOtorgado(boolean p)   { this.preavisoOtorgado = p; }
    public void setMontoReclamado(double m)      { this.montoReclamado = m; }
    public void setObservaciones(String o)       { this.observacionesGenerales = o; }
    public void setTipoCaso(String t)            { this.tipoCaso = t; }

    @Override
    public String toString() {
        return "[" + tipoCaso + "] " + razonSocialEmpleador + " — " + estado.getEtiqueta();
    }
}