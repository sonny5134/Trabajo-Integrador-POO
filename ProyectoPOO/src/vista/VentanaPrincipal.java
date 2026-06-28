package vista;

import modelo.*;
import persistencia.PersistenciaAbogado;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private CardLayout cards = new CardLayout();
    private JPanel panelContenido = new JPanel(cards);
    private ButtonGroup grupoNav = new ButtonGroup();

    // Paneles lazy
    private PanelDashboard panelDashboard;
    private PanelClientes panelClientes;
    private PanelCasos panelCasos;
    private PanelCalculadoras panelCalc;
    private PanelVencimientos panelVenc;

    public VentanaPrincipal() {
        setTitle("LiquidaLab — Sistema de Gestion Laboral");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                modelo.Repositorio repo = modelo.Repositorio.getInstance();
                if (repo.getAbogadoActual() != null) {
                    persistencia.PersistenciaDatos.guardar(
                        repo.getAbogadoActual().getDni(), repo.getClientes());
                }
                System.exit(0);
            }
        });
        setLayout(new BorderLayout());

        panelDashboard = new PanelDashboard(this);
        panelClientes  = new PanelClientes(this);
        panelCasos     = new PanelCasos(this);
        panelCalc      = new PanelCalculadoras();
        panelVenc      = new PanelVencimientos(this);

        panelContenido.setBackground(UI.GRIS_FONDO);
        panelContenido.add(wrap(panelDashboard), "dashboard");
        panelContenido.add(wrap(panelClientes),  "clientes");
        panelContenido.add(wrap(panelCasos),     "casos");
        panelContenido.add(wrap(panelCalc),      "calculadoras");
        panelContenido.add(wrap(panelVenc),      "vencimientos");

        add(crearSidebar(), BorderLayout.WEST);
        add(panelContenido, BorderLayout.CENTER);

        mostrar("dashboard");
    }

    private JScrollPane wrap(JPanel p) {
        JScrollPane s = new JScrollPane(p);
        s.setBorder(null);
        s.getViewport().setBackground(UI.GRIS_FONDO);
        s.getVerticalScrollBar().setUnitIncrement(16);
        return s;
    }

    public void mostrar(String key) {
        cards.show(panelContenido, key);
        // Refrescar el panel que se muestra
        if ("dashboard".equals(key))     panelDashboard.refrescar();
        if ("clientes".equals(key))      panelClientes.refrescar();
        if ("casos".equals(key))         panelCasos.refrescar();
        if ("vencimientos".equals(key))  panelVenc.refrescar();
    }

    public void irAClientes() { mostrar("clientes"); }
    public void irACasos()    { mostrar("casos"); }

    // ======================== SIDEBAR ========================
    private JPanel crearSidebar() {
        UI.PanelGrad sidebar = new UI.PanelGrad(UI.AZUL_OSCURO, UI.AZUL_MEDIO);
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(235, 0));

        // Encabezado
        JPanel enc = new JPanel();
        enc.setLayout(new BoxLayout(enc, BoxLayout.Y_AXIS));
        enc.setOpaque(false);
        enc.setBorder(BorderFactory.createEmptyBorder(28, 18, 18, 18));

        JLabel icoLabel = new JLabel("\u2696", SwingConstants.CENTER);
        icoLabel.setFont(new Font("SansSerif", Font.PLAIN, 38));
        icoLabel.setForeground(UI.AZUL_ACENTO);
        icoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nombre = new JLabel("LiquidaLab", SwingConstants.CENTER);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 18));
        nombre.setForeground(UI.BLANCO);
        nombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        Abogado ab = Repositorio.getInstance().getAbogadoActual();
        JLabel abNombre = new JLabel(ab != null ? "Dr/a. " + ab.getNombreCompleto() : "", SwingConstants.CENTER);
        abNombre.setFont(new Font("SansSerif", Font.PLAIN, 11));
        abNombre.setForeground(new Color(130,165,210));
        abNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        enc.add(icoLabel);
        enc.add(Box.createVerticalStrut(4));
        enc.add(nombre);
        enc.add(Box.createVerticalStrut(3));
        enc.add(abNombre);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,30));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Nav
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(12, 10, 0, 10));

        UI.BtnNav btnDash   = navBtn("\uD83C\uDFE0  Dashboard");
        UI.BtnNav btnCli    = navBtn("\uD83D\uDC64  Clientes");
        UI.BtnNav btnCasos  = navBtn("\uD83D\uDCC2  Expedientes");
        UI.BtnNav btnCalc   = navBtn("\uD83E\uDDEE  Calculadoras");
        UI.BtnNav btnVenc   = navBtn("\uD83D\uDCC5  Vencimientos");

        grupoNav.add(btnDash); grupoNav.add(btnCli); grupoNav.add(btnCasos);
        grupoNav.add(btnCalc); grupoNav.add(btnVenc);

        nav.add(btnDash);        nav.add(Box.createVerticalStrut(4));
        nav.add(btnCli);         nav.add(Box.createVerticalStrut(4));
        nav.add(btnCasos);       nav.add(Box.createVerticalStrut(4));
        nav.add(btnCalc);        nav.add(Box.createVerticalStrut(4));
        nav.add(btnVenc);

        // Pie
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(0, 14, 18, 14));

        JButton btnSalir = new JButton("  Cerrar sesion");
        btnSalir.setFont(new Font("SansSerif",Font.PLAIN,11));
        btnSalir.setForeground(new Color(180,100,100));
        btnSalir.setContentAreaFilled(false); btnSalir.setBorderPainted(false);
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> {
            persistencia.PersistenciaDatos.guardar(
                        modelo.Repositorio.getInstance().getAbogadoActual().getDni(),
                        modelo.Repositorio.getInstance().getClientes());
                    new PantallaLogin().setVisible(true);
            dispose();
        });

        JLabel ver = new JLabel("v1.0 — 2026");
        ver.setFont(new Font("SansSerif",Font.PLAIN,10));
        ver.setForeground(new Color(90,120,165));

        pie.add(btnSalir, BorderLayout.NORTH);
        pie.add(ver, BorderLayout.SOUTH);

        // Alertas badge
        List<String> alertas = Repositorio.getInstance().getAlertasVencimiento(7);
        if (!alertas.isEmpty()) {
            JLabel badge = new JLabel("  ! " + alertas.size() + " alerta(s)");
            badge.setFont(new Font("SansSerif",Font.BOLD,11));
            badge.setForeground(UI.AMARILLO);
            nav.add(Box.createVerticalStrut(8));
            nav.add(badge);
        }

        // Acciones nav
        btnDash.addActionListener(e  -> mostrar("dashboard"));
        btnCli.addActionListener(e   -> mostrar("clientes"));
        btnCasos.addActionListener(e -> mostrar("casos"));
        btnCalc.addActionListener(e  -> mostrar("calculadoras"));
        btnVenc.addActionListener(e  -> mostrar("vencimientos"));

        btnDash.setSelected(true);

        JPanel navWrap = new JPanel(new BorderLayout());
        navWrap.setOpaque(false);
        navWrap.add(sep, BorderLayout.NORTH);
        navWrap.add(nav, BorderLayout.CENTER);

        sidebar.add(enc, BorderLayout.NORTH);
        sidebar.add(navWrap, BorderLayout.CENTER);
        sidebar.add(pie, BorderLayout.SOUTH);

        return sidebar;
    }

    private UI.BtnNav navBtn(String txt) {
        return new UI.BtnNav("  " + txt);
    }
}