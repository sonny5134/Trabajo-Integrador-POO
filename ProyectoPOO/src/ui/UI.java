package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class UI {

    // ===================== COLORES =====================
    public static final Color AZUL_OSCURO  = new Color(13, 27, 52);
    public static final Color AZUL_MEDIO   = new Color(22, 48, 95);
    public static final Color AZUL_ACENTO  = new Color(41, 128, 185);
    public static final Color VERDE        = new Color(39, 174, 96);
    public static final Color NARANJA      = new Color(211, 84, 0);
    public static final Color ROJO         = new Color(192, 57, 43);
    public static final Color AMARILLO     = new Color(241, 196, 15);
    public static final Color BLANCO       = Color.WHITE;
    public static final Color GRIS_FONDO   = new Color(245, 247, 250);
    public static final Color GRIS_CLARO   = new Color(236, 240, 244);
    public static final Color GRIS_TEXTO   = new Color(90, 105, 125);
    public static final Color FONDO_CAMPO  = new Color(250, 252, 255);
    public static final Color BORDE_CAMPO  = new Color(190, 205, 225);
    public static final Color CARD_BG      = new Color(255, 255, 255);

    // ===================== FUENTES =====================
    public static final Font F_TITULO    = new Font("SansSerif", Font.BOLD, 22);
    public static final Font F_SUBTITULO = new Font("SansSerif", Font.BOLD, 14);
    public static final Font F_LABEL     = new Font("SansSerif", Font.BOLD, 11);
    public static final Font F_CAMPO     = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font F_BOTON     = new Font("SansSerif", Font.BOLD, 13);
    public static final Font F_NAV       = new Font("SansSerif", Font.BOLD, 13);
    public static final Font F_MONO      = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font F_BADGE     = new Font("SansSerif", Font.BOLD, 11);

    // ===================== PANEL GRADIENTE =====================
    public static class PanelGrad extends JPanel {
        private Color c1, c2;
        public PanelGrad(Color c1, Color c2) { this.c1=c1; this.c2=c2; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0,0,c1,0,getHeight(),c2));
            g2.fillRect(0,0,getWidth(),getHeight());
            g2.dispose(); super.paintComponent(g);
        }
    }

    // ===================== CAMPO DE TEXTO =====================
    public static class Campo extends JTextField {
        public Campo() { init(); }
        public Campo(String txt) { super(txt); init(); }
        private void init() {
            setFont(F_CAMPO); setBackground(FONDO_CAMPO); setForeground(AZUL_OSCURO);
            setCaretColor(AZUL_ACENTO);
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDE_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(7, 11, 7, 11)));
            setPreferredSize(new Dimension(0, 36));
        }
    }

    // ===================== AREA DE TEXTO =====================
    public static class Area extends JTextArea {
        public Area(int rows, int cols) {
            super(rows, cols);
            setFont(F_CAMPO); setBackground(FONDO_CAMPO); setForeground(AZUL_OSCURO);
            setLineWrap(true); setWrapStyleWord(true);
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDE_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        }
    }

    // ===================== COMBO =====================
    public static class Combo extends JComboBox<String> {
        public Combo(String[] items) {
            super(items);
            setFont(F_CAMPO); setBackground(FONDO_CAMPO); setForeground(AZUL_OSCURO);
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDE_CAMPO, 1, true),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
            setPreferredSize(new Dimension(0, 36));
        }
    }

    // ===================== BOTON PRIMARIO =====================
    public static class BtnPrimario extends JButton {
        private Color base, hover; private boolean over=false;
        public BtnPrimario(String txt, Color base, Color hover) {
            super(txt); this.base=base; this.hover=hover;
            setFont(F_BOTON); setForeground(BLANCO); setFocusPainted(false);
            setBorderPainted(false); setContentAreaFilled(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(180, 38));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { over=true; repaint(); }
                public void mouseExited(MouseEvent e)  { over=false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(over?hover:base);
            g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
            g2.dispose(); super.paintComponent(g);
        }
    }

    // ===================== BOTON ICONO =====================
    public static class BtnIcono extends JButton {
        private Color base, hover; private boolean over=false;
        public BtnIcono(String txt, Color base, Color hover) {
            super(txt); this.base=base; this.hover=hover;
            setFont(new Font("SansSerif",Font.BOLD,12)); setForeground(BLANCO);
            setFocusPainted(false); setBorderPainted(false);
            setContentAreaFilled(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(130, 34));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { over=true; repaint(); }
                public void mouseExited(MouseEvent e)  { over=false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(over?hover:base);
            g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),6,6));
            g2.dispose(); super.paintComponent(g);
        }
    }

    // ===================== BOTON NAV SIDEBAR =====================
    public static class BtnNav extends JToggleButton {
        public BtnNav(String txt) {
            super(txt); setFont(F_NAV); setForeground(new Color(170,195,230));
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false); setBorderPainted(false);
            setContentAreaFilled(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(220, 46));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            if (isSelected()) {
                g2.setColor(new Color(255,255,255,25));
                g2.fill(new RoundRectangle2D.Float(6,3,getWidth()-12,getHeight()-6,8,8));
                setForeground(BLANCO);
                g2.setColor(AZUL_ACENTO);
                g2.fill(new RoundRectangle2D.Float(0,6,4,getHeight()-12,4,4));
            } else { setForeground(new Color(155,180,215)); }
            g2.dispose(); super.paintComponent(g);
        }
    }

    // ===================== BOTON NAV SUBMENU =====================
    public static class BtnSubNav extends JToggleButton {
        public BtnSubNav(String txt) {
            super("    " + txt); setFont(new Font("SansSerif",Font.PLAIN,12));
            setForeground(new Color(140,170,210));
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false); setBorderPainted(false);
            setContentAreaFilled(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(220, 36));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            if (isSelected()) {
                g2.setColor(new Color(255,255,255,15));
                g2.fillRoundRect(6,2,getWidth()-12,getHeight()-4,6,6);
                setForeground(new Color(130,200,255));
            } else { setForeground(new Color(130,160,200)); }
            g2.dispose(); super.paintComponent(g);
        }
    }

    // ===================== CARD PANEL =====================
    public static class Card extends JPanel {
        public Card() {
            setBackground(CARD_BG); setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220,228,238),1,true),
                BorderFactory.createEmptyBorder(16,18,16,18)));
        }
    }

    // ===================== BADGE DE ESTADO =====================
    public static class Badge extends JLabel {
        public Badge(String txt, Color bg) {
            super(txt); setFont(F_BADGE); setForeground(BLANCO);
            setOpaque(false); setHorizontalAlignment(SwingConstants.CENTER);
        }
        private Color bg;
        public Badge(String txt, Color bg, boolean dummy) {
            super(" " + txt + " "); this.bg=bg;
            setFont(F_BADGE); setForeground(BLANCO); setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg != null ? bg : AZUL_ACENTO);
            g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),12,12));
            g2.dispose(); super.paintComponent(g);
        }
    }

    // ===================== SEPARADOR =====================
    public static JSeparator separador() {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(220,228,238));
        return s;
    }

    // ===================== LABEL TITULO =====================
    public static JLabel labelTitulo(String txt) {
        JLabel l = new JLabel(txt); l.setFont(F_TITULO); l.setForeground(AZUL_OSCURO); return l;
    }
    public static JLabel labelSub(String txt) {
        JLabel l = new JLabel(txt); l.setFont(F_SUBTITULO); l.setForeground(GRIS_TEXTO); return l;
    }
    public static JLabel labelCampo(String txt) {
        JLabel l = new JLabel(txt); l.setFont(F_LABEL); l.setForeground(GRIS_TEXTO); return l;
    }

    // ===================== FILA CAMPO =====================
    public static JPanel filaCampo(String label, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0,4)); p.setOpaque(false);
        p.add(labelCampo(label), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    // ===================== SCROLL RESULTADO =====================
    public static JScrollPane scrollResultado(JTextArea area) {
        JScrollPane s = new JScrollPane(area);
        s.setBorder(new LineBorder(new Color(40,70,120),1,true));
        return s;
    }

    // ===================== PANEL SECCION =====================
    public static JPanel seccion(String titulo) {
        JPanel p = new JPanel(new BorderLayout(0,14)); p.setOpaque(false);
        JLabel lbl = new JLabel(titulo); lbl.setFont(F_SUBTITULO); lbl.setForeground(AZUL_OSCURO);
        lbl.setBorder(BorderFactory.createEmptyBorder(0,0,6,0));
        p.add(lbl, BorderLayout.NORTH);
        return p;
    }
}
