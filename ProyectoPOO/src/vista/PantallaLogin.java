package vista;

import modelo.Abogado;
import modelo.Repositorio;
import persistencia.PersistenciaAbogado;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PantallaLogin extends JFrame {

    private CardLayout cards = new CardLayout();
    private JPanel contenido = new JPanel(cards);

    public PantallaLogin() {
        setTitle("LiquidaLab — Acceso");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Resetear sesion al volver al login (logout)
        Repositorio.resetearSesion();

        contenido.setBackground(UI.GRIS_FONDO);
        contenido.add(panelLogin(),    "login");
        contenido.add(panelRegistro(), "registro");
        add(contenido);
        cards.show(contenido, "login");
    }

    // ======================== LOGIN ========================
    private JPanel panelLogin() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UI.GRIS_FONDO);

        UI.PanelGrad card = new UI.PanelGrad(UI.AZUL_OSCURO, UI.AZUL_MEDIO);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        card.setPreferredSize(new Dimension(430, 530));

        // Logo
        JLabel ico = new JLabel("\u2696", SwingConstants.CENTER);
        ico.setFont(new Font("SansSerif", Font.PLAIN, 56));
        ico.setForeground(UI.AZUL_ACENTO);
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("LiquidaLab", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(UI.BLANCO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sistema de Gestion para Abogados Laboralistas", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(new Color(140, 165, 200));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campos
        UI.Campo txtDni = new UI.Campo();
        txtDni.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPasswordField passField = crearPasswordField();

        // Botones
        UI.BtnPrimario btnLogin = new UI.BtnPrimario("  Ingresar", UI.AZUL_ACENTO, new Color(31, 97, 141));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnIrRegistro = linkBtn("¿No tiene cuenta? Registrarse");

        // Layout
        card.add(ico);
        card.add(Box.createVerticalStrut(8));
        card.add(titulo);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(34));
        card.add(UI.labelCampo("DNI del Abogado"));
        card.add(Box.createVerticalStrut(5));
        card.add(txtDni);
        card.add(Box.createVerticalStrut(16));
        card.add(UI.labelCampo("Contrasena"));
        card.add(Box.createVerticalStrut(5));
        card.add(passField);
        card.add(Box.createVerticalStrut(26));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(14));
        card.add(btnIrRegistro);

        outer.add(card);

        // Accion login
        btnLogin.addActionListener(e -> {
            String dni  = txtDni.getText().trim();
            String pass = new String(passField.getPassword());

            if (dni.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete DNI y contrasena.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar abogado por DNI en su archivo propio
            Abogado a = PersistenciaAbogado.cargarPorDni(dni);

            if (a == null) {
                JOptionPane.showMessageDialog(this,
                    "No existe un abogado registrado con ese DNI.\nCree una cuenta primero.",
                    "Usuario no encontrado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!a.getPassword().equals(pass)) {
                JOptionPane.showMessageDialog(this,
                    "Contrasena incorrecta.",
                    "Error de acceso", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Login exitoso — cargar sesion limpia para este abogado
            Repositorio.getInstance().setAbogadoActual(a);
            abrirVentanaPrincipal();
        });

        btnIrRegistro.addActionListener(e -> cards.show(contenido, "registro"));

        return outer;
    }

    // ======================== REGISTRO ========================
    private JPanel panelRegistro() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UI.GRIS_FONDO);

        UI.PanelGrad card = new UI.PanelGrad(UI.AZUL_OSCURO, UI.AZUL_MEDIO);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(35, 50, 35, 50));
        card.setPreferredSize(new Dimension(460, 640));

        JLabel titulo = new JLabel("Registro de Abogado", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(UI.BLANCO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Complete sus datos profesionales", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(140, 165, 200));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        UI.Campo txtNombre    = campo();
        UI.Campo txtDni       = campo();
        UI.Campo txtMatricula = campo();
        UI.Campo txtCuit      = campo();
        JPasswordField txtPass  = crearPasswordField();
        JPasswordField txtPass2 = crearPasswordField();

        UI.BtnPrimario btnReg = new UI.BtnPrimario("  Registrarme", UI.VERDE, new Color(25, 130, 60));
        btnReg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnReg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnVolver = linkBtn("Volver al login");

        card.add(titulo); card.add(Box.createVerticalStrut(4)); card.add(sub);
        card.add(Box.createVerticalStrut(24));
        fila(card, "Nombre y Apellido Completo", txtNombre);
        card.add(Box.createVerticalStrut(12));
        fila(card, "DNI", txtDni);
        card.add(Box.createVerticalStrut(12));
        fila(card, "Numero de Matricula", txtMatricula);
        card.add(Box.createVerticalStrut(12));
        fila(card, "CUIT", txtCuit);
        card.add(Box.createVerticalStrut(12));
        fila(card, "Contrasena", txtPass);
        card.add(Box.createVerticalStrut(12));
        fila(card, "Repetir Contrasena", txtPass2);
        card.add(Box.createVerticalStrut(24));
        card.add(btnReg);
        card.add(Box.createVerticalStrut(14));
        card.add(btnVolver);

        outer.add(card);

        btnReg.addActionListener(e -> {
            String nombre    = txtNombre.getText().trim();
            String dni       = txtDni.getText().trim();
            String matricula = txtMatricula.getText().trim();
            String cuit      = txtCuit.getText().trim();
            String pass      = new String(txtPass.getPassword());
            String pass2     = new String(txtPass2.getPassword());

            if (nombre.isEmpty() || dni.isEmpty() || matricula.isEmpty() || cuit.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!pass.equals(pass2)) {
                JOptionPane.showMessageDialog(this, "Las contrasenyas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (PersistenciaAbogado.existeAbogado(dni)) {
                JOptionPane.showMessageDialog(this,
                    "Ya existe un abogado registrado con ese DNI.\nInicie sesion con su cuenta.",
                    "DNI ya registrado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Abogado a = new Abogado(nombre, dni, matricula, cuit, pass);
            PersistenciaAbogado.guardar(a);
            Repositorio.getInstance().setAbogadoActual(a);

            JOptionPane.showMessageDialog(this,
                "Cuenta creada exitosamente.\nBienvenido/a, " + nombre + "!",
                "Registro OK", JOptionPane.INFORMATION_MESSAGE);
            abrirVentanaPrincipal();
        });

        btnVolver.addActionListener(e -> cards.show(contenido, "login"));

        return outer;
    }

    // ======================== HELPERS ========================
    private UI.Campo campo() {
        UI.Campo c = new UI.Campo();
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return c;
    }

    private JPasswordField crearPasswordField() {
        JPasswordField p = new JPasswordField();
        p.setFont(UI.F_CAMPO);
        p.setBackground(UI.FONDO_CAMPO);
        p.setForeground(UI.AZUL_OSCURO);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UI.BORDE_CAMPO, 1, true),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return p;
    }

    private JButton linkBtn(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setForeground(new Color(130, 180, 230));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    private void fila(JPanel card, String label, JComponent campo) {
        JLabel lbl = UI.labelCampo(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        card.add(campo);
    }

    private void abrirVentanaPrincipal() {
        VentanaPrincipal vp = new VentanaPrincipal();
        vp.setVisible(true);
        dispose();
    }
}