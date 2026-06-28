package main;

import vista.PantallaLogin;
import javax.swing.*;

public class LiquidaLab {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            PantallaLogin login = new PantallaLogin();
            login.setVisible(true);
        });
    }
}
