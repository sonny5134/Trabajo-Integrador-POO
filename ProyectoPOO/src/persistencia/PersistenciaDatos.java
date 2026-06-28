package persistencia;

import modelo.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Guarda y carga todos los clientes y casos de un abogado
 * en un archivo de texto plano con formato clave=valor.
 * Archivo: datos_<DNI>.txt
 */
public class PersistenciaDatos {

    private static String archivo(String dni) {
        return "datos_" + dni.trim() + ".txt";
    }

    // ================================================================
    //  GUARDAR
    // ================================================================
    public static void guardar(String dniAbogado, List<Cliente> clientes) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo(dniAbogado)))) {

            for (Cliente cli : clientes) {
                // ---- CLIENTE ----
                pw.println("CLIENTE_INI");
                pw.println("id=" + esc(cli.getId()));
                pw.println("nombre=" + esc(cli.getNombreCompleto()));
                pw.println("dni=" + esc(cli.getDni()));
                pw.println("cuil=" + esc(cli.getCuil()));
                pw.println("telefono=" + esc(cli.getTelefono()));
                pw.println("email=" + esc(cli.getEmail()));
                pw.println("domicilio=" + esc(cli.getDomicilio()));

                for (Caso caso : cli.getCasos()) {
                    // ---- CASO ----
                    pw.println("CASO_INI");
                    pw.println("cid=" + esc(caso.getId()));
                    pw.println("tipo=" + esc(caso.getTipoCaso()));
                    pw.println("estado=" + caso.getEstado().name());
                    pw.println("razon=" + esc(caso.getRazonSocialEmpleador()));
                    pw.println("cuit=" + esc(caso.getCuitEmpleador()));
                    pw.println("domEmp=" + esc(caso.getDomicilioEmpleador()));
                    pw.println("ingreso=" + (caso.getFechaIngreso() != null ? caso.getFechaIngreso() : ""));
                    pw.println("egreso=" + (caso.getFechaEgreso()  != null ? caso.getFechaEgreso()  : ""));
                    pw.println("sueldo=" + caso.getUltimoSueldo());
                    pw.println("preaviso=" + caso.isPreavisoOtorgado());
                    pw.println("monto=" + caso.getMontoReclamado());
                    pw.println("obs=" + esc(caso.getObservaciones()));

                    // ---- TESTIGOS ----
                    for (Testigo t : caso.getTestigos()) {
                        pw.println("TESTIGO_INI");
                        pw.println("tnombre=" + esc(t.getNombreCompleto()));
                        pw.println("tdni=" + esc(t.getDni()));
                        pw.println("ttel=" + esc(t.getTelefono()));
                        pw.println("temail=" + esc(t.getEmail()));
                        pw.println("tobs=" + esc(t.getObservaciones()));
                        pw.println("TESTIGO_FIN");
                    }

                    // ---- BITACORA ----
                    for (EntradaBitacora e : caso.getBitacora()) {
                        pw.println("BIT_INI");
                        pw.println("bfecha=" + e.getFechaHora().toString());
                        pw.println("btipo=" + esc(e.getTipo()));
                        pw.println("bdesc=" + esc(e.getDescripcion()));
                        pw.println("BIT_FIN");
                    }

                    // ---- VENCIMIENTOS ----
                    for (Vencimiento v : caso.getVencimientos()) {
                        pw.println("VENC_INI");
                        pw.println("vdesc=" + esc(v.getDescripcion()));
                        pw.println("vfecha=" + v.getFecha().toString());
                        pw.println("vcump=" + v.isCumplido());
                        pw.println("VENC_FIN");
                    }

                    // ---- DOCUMENTOS ----
                    for (DocumentoAdjunto d : caso.getDocumentos()) {
                        pw.println("DOC_INI");
                        pw.println("dnombre=" + esc(d.getNombre()));
                        pw.println("dtype=" + d.getTipo().name());
                        pw.println("druta=" + esc(d.getRutaArchivo()));
                        pw.println("ddesc=" + esc(d.getDescripcion()));
                        pw.println("DOC_FIN");
                    }

                    pw.println("CASO_FIN");
                }
                pw.println("CLIENTE_FIN");
            }

        } catch (IOException e) {
            System.err.println("Error al guardar datos: " + e.getMessage());
        }
    }

    // ================================================================
    //  CARGAR
    // ================================================================
    public static void cargar(String dniAbogado, Repositorio repo) {
        File f = new File(archivo(dniAbogado));
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            Cliente clienteActual = null;
            Caso casoActual = null;

            // Variables temporales para construir objetos
            String cid="",tipo="",razon="",cuit="",domEmp="",ingStr="",egStr="",
                   obsC="",estadoStr="";
            double sueldo=0, monto=0;
            boolean preaviso=false;

            String tnombre="",tdni="",ttel="",temail="",tobs="";
            String bfecha="",btipo="",bdesc="";
            String vdesc="",vfecha=""; boolean vcump=false;
            String dnombre="",dtype="",druta="",ddesc="";

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                // ---- CLIENTE ----
                if (linea.equals("CLIENTE_INI")) {
                    // leer campos del cliente
                    String id="",nombre="",dni="",cuil="",tel="",email="",dom="";
                    while (!(linea = br.readLine().trim()).equals("CASO_INI")
                           && !linea.equals("CLIENTE_FIN")) {
                        if (linea.startsWith("id="))        id     = val(linea);
                        else if (linea.startsWith("nombre="))nombre = val(linea);
                        else if (linea.startsWith("dni="))  dni    = val(linea);
                        else if (linea.startsWith("cuil=")) cuil   = val(linea);
                        else if (linea.startsWith("telefono=")) tel= val(linea);
                        else if (linea.startsWith("email="))email  = val(linea);
                        else if (linea.startsWith("domicilio=")) dom= val(linea);
                    }
                    clienteActual = new Cliente(id, nombre, dni, cuil, tel, email, dom);
                    repo.getClientes().add(clienteActual);

                    if (linea.equals("CLIENTE_FIN")) { clienteActual = null; continue; }
                    // si llegamos aqui linea == "CASO_INI", caemos al bloque CASO
                }

                if (linea.equals("CASO_INI")) {
                    // reset vars caso
                    cid=""; tipo=""; razon=""; cuit=""; domEmp=""; ingStr=""; egStr="";
                    obsC=""; estadoStr=""; sueldo=0; monto=0; preaviso=false;
                    casoActual = null;
                    continue;
                }

                if (linea.equals("CASO_FIN")) {
                    casoActual = null; continue;
                }

                if (linea.equals("CLIENTE_FIN")) {
                    clienteActual = null; continue;
                }

                // campos del caso (antes de crear el objeto)
                if (casoActual == null && clienteActual != null) {
                    if      (linea.startsWith("cid="))    cid       = val(linea);
                    else if (linea.startsWith("tipo="))   tipo      = val(linea);
                    else if (linea.startsWith("estado=")) estadoStr = val(linea);
                    else if (linea.startsWith("razon="))  razon     = val(linea);
                    else if (linea.startsWith("cuit="))   cuit      = val(linea);
                    else if (linea.startsWith("domEmp=")) domEmp    = val(linea);
                    else if (linea.startsWith("ingreso="))ingStr    = val(linea);
                    else if (linea.startsWith("egreso=")) egStr     = val(linea);
                    else if (linea.startsWith("sueldo=")) sueldo    = doble(linea);
                    else if (linea.startsWith("preaviso="))preaviso = bool(linea);
                    else if (linea.startsWith("monto="))  monto     = doble(linea);
                    else if (linea.startsWith("obs="))    obsC      = val(linea);

                    // cuando tenemos todos los campos basicos, construimos el Caso
                    else if (linea.equals("TESTIGO_INI") || linea.equals("BIT_INI")
                          || linea.equals("VENC_INI")   || linea.equals("DOC_INI")) {
                        if (casoActual == null) {
                            casoActual = construirCaso(cid, tipo, estadoStr, razon, cuit,
                                domEmp, ingStr, egStr, sueldo, preaviso, monto, obsC);
                            clienteActual.agregarCaso(casoActual);
                        }
                        procesarSubbloque(linea, casoActual, br);
                    }
                    continue;
                }

                // sub-bloques dentro de un caso ya creado
                if (casoActual != null) {
                    if (linea.equals("TESTIGO_INI") || linea.equals("BIT_INI")
                     || linea.equals("VENC_INI")   || linea.equals("DOC_INI")) {
                        procesarSubbloque(linea, casoActual, br);
                    }
                    // si llegamos a CASO_FIN sin sub-bloques (caso sin testigos/docs)
                }
            }

        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================================================================
    //  HELPERS PRIVADOS
    // ================================================================

    private static Caso construirCaso(String cid, String tipo, String estadoStr,
            String razon, String cuit, String domEmp, String ingStr, String egStr,
            double sueldo, boolean preaviso, double monto, String obs) {
        LocalDate ing = ingStr.isEmpty() ? null : LocalDate.parse(ingStr);
        LocalDate eg  = egStr.isEmpty()  ? null : LocalDate.parse(egStr);
        if (ing == null) ing = LocalDate.now().minusYears(1);
        if (eg  == null) eg  = LocalDate.now();

        Caso c = new Caso(cid, tipo, razon, cuit, domEmp, ing, eg, sueldo);
        c.setPreavisoOtorgado(preaviso);
        c.setMontoReclamado(monto);
        c.setObservaciones(obs);
        // estado
        try {
            EstadoCaso estado = EstadoCaso.valueOf(estadoStr);
            // set directo sin agregar a bitacora
            setEstadoSilencioso(c, estado);
        } catch (Exception ignored) {}
        // limpiar la bitacora autogenerada (se recargara desde archivo)
        c.getBitacora().clear();
        return c;
    }

    private static void setEstadoSilencioso(Caso c, EstadoCaso e) {
        // Avanzamos el estado sin agregar entradas en bitacora
        while (c.getEstado() != e) {
            EstadoCaso[] vals = EstadoCaso.values();
            int actual = c.getEstado().ordinal();
            int target = e.ordinal();
            if (target > actual) {
                c.getBitacora().clear(); // evitar acumulacion
                c.avanzarEstado();
                c.getBitacora().clear();
            } else {
                c.getBitacora().clear();
                c.retrocederEstado();
                c.getBitacora().clear();
            }
        }
    }

    private static void procesarSubbloque(String tipo, Caso caso, BufferedReader br)
            throws IOException {
        String linea;
        String tn="",td="",tt="",te="",to="";
        String bf="",bt="",bd="";
        String vd="",vf=""; boolean vc=false;
        String dn="",dy="",dr="",dd="";

        while ((linea = br.readLine()) != null) {
            linea = linea.trim();

            if (linea.equals("TESTIGO_FIN")) {
                caso.getTestigos().add(new Testigo(tn,td,tt,te,to)); return;
            }
            if (linea.equals("BIT_FIN")) {
                try {
                    LocalDateTime fh = LocalDateTime.parse(bf);
                    caso.getBitacora().add(new EntradaBitacora(fh, bt, bd));
                } catch (Exception ignored) {}
                return;
            }
            if (linea.equals("VENC_FIN")) {
                try {
                    Vencimiento v = new Vencimiento(vd, LocalDate.parse(vf));
                    v.setCumplido(vc);
                    caso.getVencimientos().add(v);
                } catch (Exception ignored) {}
                return;
            }
            if (linea.equals("DOC_FIN")) {
                try {
                    DocumentoAdjunto.TipoDocumento t =
                        DocumentoAdjunto.TipoDocumento.valueOf(dy);
                    caso.getDocumentos().add(new DocumentoAdjunto(dn, t, dr, dd));
                } catch (Exception ignored) {}
                return;
            }

            // campos
            if      (linea.startsWith("tnombre=")) tn = val(linea);
            else if (linea.startsWith("tdni="))    td = val(linea);
            else if (linea.startsWith("ttel="))    tt = val(linea);
            else if (linea.startsWith("temail="))  te = val(linea);
            else if (linea.startsWith("tobs="))    to = val(linea);

            else if (linea.startsWith("bfecha="))  bf = val(linea);
            else if (linea.startsWith("btipo="))   bt = val(linea);
            else if (linea.startsWith("bdesc="))   bd = val(linea);

            else if (linea.startsWith("vdesc="))   vd = val(linea);
            else if (linea.startsWith("vfecha="))  vf = val(linea);
            else if (linea.startsWith("vcump="))   vc = bool(linea);

            else if (linea.startsWith("dnombre=")) dn = val(linea);
            else if (linea.startsWith("dtype="))   dy = val(linea);
            else if (linea.startsWith("druta="))   dr = val(linea);
            else if (linea.startsWith("ddesc="))   dd = val(linea);
        }
    }

    // escapar saltos de linea en valores
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\n", "\\n").replace("\r", "");
    }

    // leer valor despues del primer '='
    private static String val(String linea) {
        int idx = linea.indexOf('=');
        if (idx < 0) return "";
        return linea.substring(idx + 1).replace("\\n", "\n");
    }

    private static double doble(String linea) {
        try { return Double.parseDouble(val(linea)); }
        catch (Exception e) { return 0; }
    }

    private static boolean bool(String linea) {
        return "true".equalsIgnoreCase(val(linea));
    }

    public static boolean existenDatos(String dniAbogado) {
        return new File(archivo(dniAbogado)).exists();
    }
}