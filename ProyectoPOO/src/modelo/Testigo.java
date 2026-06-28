package modelo;

public class Testigo {
    private String nombreCompleto;
    private String dni;
    private String telefono;
    private String email;
    private String observaciones;

    public Testigo(String nombreCompleto, String dni, String telefono, String email, String observaciones) {
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.observaciones = observaciones;
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public String getDni()            { return dni; }
    public String getTelefono()       { return telefono; }
    public String getEmail()          { return email; }
    public String getObservaciones()  { return observaciones; }

    public void setNombreCompleto(String n) { this.nombreCompleto = n; }
    public void setDni(String d)            { this.dni = d; }
    public void setTelefono(String t)       { this.telefono = t; }
    public void setEmail(String e)          { this.email = e; }
    public void setObservaciones(String o)  { this.observaciones = o; }

    @Override
    public String toString() { return nombreCompleto + " - Tel: " + telefono; }
}