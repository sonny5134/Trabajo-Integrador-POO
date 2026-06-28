package persistencia;

import modelo.Abogado;
import java.io.*;
import java.nio.file.*;

public class PersistenciaAbogado {

    /**
     * Cada abogado se guarda en su propio archivo: abogado_<DNI>.json
     * Esto permite que multiples abogados usen el sistema en la misma PC
     * con datos completamente separados.
     */
    private static String nombreArchivo(String dni) {
        return "abogado_" + dni.trim() + ".json";
    }

    public static void guardar(Abogado a) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo(a.getDni())))) {
            pw.print(a.toJson());
        } catch (IOException e) {
            System.err.println("Error al guardar abogado: " + e.getMessage());
        }
    }

    public static Abogado cargarPorDni(String dni) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(nombreArchivo(dni))));
            return Abogado.fromJson(json);
        } catch (IOException e) {
            return null; // no existe ese DNI
        }
    }

    public static boolean existeAbogado(String dni) {
        return new File(nombreArchivo(dni)).exists();
    }
}