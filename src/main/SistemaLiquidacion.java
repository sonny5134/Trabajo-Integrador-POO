package main;
import vista.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class SistemaLiquidacion {
    public static void main(String[] args) {
        // Ejecutamos la GUI en el hilo correspondiente de Swing
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.setVisible(true); // Hace visible la ventana
            }
        });
    }
}