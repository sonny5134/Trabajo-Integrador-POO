package modelo;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private String id;
    private String nombreCompleto;
    private String dni;
    private String cuil;
    private String telefono;
    private String email;
    private String domicilio;
    private List<Caso> casos;

    public Cliente(String id, String nombreCompleto, String dni, String cuil,
                   String telefono, String email, String domicilio) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.cuil = cuil;
        this.telefono = telefono;
        this.email = email;
        this.domicilio = domicilio;
        this.casos = new ArrayList<>();
    }

    public void agregarCaso(Caso c) { casos.add(c); }
    public void eliminarCaso(Caso c) { casos.remove(c); }

    public String getId()              { return id; }
    public String getNombreCompleto()  { return nombreCompleto; }
    public String getDni()             { return dni; }
    public String getCuil()            { return cuil; }
    public String getTelefono()        { return telefono; }
    public String getEmail()           { return email; }
    public String getDomicilio()       { return domicilio; }
    public List<Caso> getCasos()       { return casos; }

    public void setNombreCompleto(String n) { this.nombreCompleto = n; }
    public void setDni(String d)            { this.dni = d; }
    public void setCuil(String c)           { this.cuil = c; }
    public void setTelefono(String t)       { this.telefono = t; }
    public void setEmail(String e)          { this.email = e; }
    public void setDomicilio(String d)      { this.domicilio = d; }

    @Override
    public String toString() { return nombreCompleto + " (DNI: " + dni + ")"; }
}
