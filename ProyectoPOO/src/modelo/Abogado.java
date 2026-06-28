package modelo;

public class Abogado {

    private String nombreCompleto;
    private String dni;
    private String matricula;
    private String cuit;
    private String password;

    public Abogado(String nombreCompleto, String dni, String matricula, String cuit, String password) {
        this.nombreCompleto = nombreCompleto;
        this.dni            = dni;
        this.matricula      = matricula;
        this.cuit           = cuit;
        this.password       = password;
    }

    // ---- Getters ----
    public String getNombreCompleto() { return nombreCompleto; }
    public String getDni()            { return dni; }
    public String getMatricula()      { return matricula; }
    public String getCuit()           { return cuit; }
    public String getPassword()       { return password; }

    // ---- Setters ----
    public void setNombreCompleto(String n) { this.nombreCompleto = n; }
    public void setDni(String d)            { this.dni = d; }
    public void setMatricula(String m)      { this.matricula = m; }
    public void setCuit(String c)           { this.cuit = c; }
    public void setPassword(String p)       { this.password = p; }

    // ---- Serialización JSON manual ----
    public String toJson() {
        return "{"
            + "\"nombreCompleto\":\"" + nombreCompleto + "\","
            + "\"dni\":\""            + dni            + "\","
            + "\"matricula\":\""      + matricula      + "\","
            + "\"cuit\":\""           + cuit           + "\","
            + "\"password\":\""       + password       + "\""
            + "}";
    }

    public static Abogado fromJson(String json) {
        String n = extraer(json, "nombreCompleto");
        String d = extraer(json, "dni");
        String m = extraer(json, "matricula");
        String c = extraer(json, "cuit");
        String p = extraer(json, "password");
        return new Abogado(n, d, m, c, p);
    }

    private static String extraer(String json, String clave) {
        String buscar = "\"" + clave + "\":\"";
        int ini = json.indexOf(buscar);
        if (ini < 0) return "";
        ini += buscar.length();
        int fin = json.indexOf("\"", ini);
        if (fin < 0) return "";
        return json.substring(ini, fin);
    }

    @Override
    public String toString() {
        return nombreCompleto + " (DNI: " + dni + " | Mat: " + matricula + ")";
    }
}
