package vista;

import modelo.*;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class PanelVencimientos extends JPanel {
    private VentanaPrincipal ventana;
    private JPanel listaPanel;

    public PanelVencimientos(VentanaPrincipal v) {
        this.ventana = v;
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        JPanel enc = new JPanel(new BorderLayout(0, 6));
        enc.setOpaque(false);
        enc.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        enc.add(UI.labelTitulo("\uD83D\uDCC5  Control de Vencimientos"), BorderLayout.WEST);
        enc.add(UI.labelSub("Alertas activas de todos los expedientes"), BorderLayout.SOUTH);
        add(enc, BorderLayout.NORTH);

        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setOpaque(false);
        add(listaPanel, BorderLayout.CENTER);

        refrescar();
    }

    public void refrescar() {
        listaPanel.removeAll();
        Repositorio repo = Repositorio.getInstance();

        // Sección: VENCIDOS
        boolean hayVencidos = false;
        for (Cliente cli : repo.getClientes())
            for (Caso caso : cli.getCasos())
                for (Vencimiento v : caso.getVencimientos())
                    if (v.estaVencido()) hayVencidos = true;

        if (hayVencidos) {
            listaPanel.add(encabezadoSeccion("\uD83D\uDD34  VENCIDOS", UI.ROJO));
            listaPanel.add(Box.createVerticalStrut(8));
            for (Cliente cli : repo.getClientes()) {
                for (Caso caso : cli.getCasos()) {
                    for (Vencimiento v : caso.getVencimientos()) {
                        if (v.estaVencido()) {
                            listaPanel.add(tarjetaVencimiento(cli, caso, v, UI.ROJO));
                            listaPanel.add(Box.createVerticalStrut(6));
                        }
                    }
                }
            }
            listaPanel.add(Box.createVerticalStrut(16));
        }

        // Sección: PROXIMOS (7 días)
        boolean hayProximos = false;
        for (Cliente cli : repo.getClientes())
            for (Caso caso : cli.getCasos())
                for (Vencimiento v : caso.getVencimientos())
                    if (v.venceProximamente(7)) hayProximos = true;

        if (hayProximos) {
            listaPanel.add(encabezadoSeccion("\uD83D\uDFE1  PROXIMOS (7 dias)", UI.AMARILLO));
            listaPanel.add(Box.createVerticalStrut(8));
            for (Cliente cli : repo.getClientes()) {
                for (Caso caso : cli.getCasos()) {
                    for (Vencimiento v : caso.getVencimientos()) {
                        if (v.venceProximamente(7)) {
                            listaPanel.add(tarjetaVencimiento(cli, caso, v, UI.AMARILLO));
                            listaPanel.add(Box.createVerticalStrut(6));
                        }
                    }
                }
            }
            listaPanel.add(Box.createVerticalStrut(16));
        }

        // Sección: TODOS LOS VENCIMIENTOS
        listaPanel.add(encabezadoSeccion("\uD83D\uDCCB  TODOS LOS VENCIMIENTOS", UI.AZUL_ACENTO));
        listaPanel.add(Box.createVerticalStrut(8));

        boolean hayAlguno = false;
        for (Cliente cli : repo.getClientes()) {
            for (Caso caso : cli.getCasos()) {
                for (Vencimiento v : caso.getVencimientos()) {
                    if (!v.estaVencido() && !v.venceProximamente(7)) {
                        hayAlguno = true;
                        listaPanel.add(tarjetaVencimiento(cli, caso, v, UI.AZUL_ACENTO));
                        listaPanel.add(Box.createVerticalStrut(6));
                    }
                }
            }
        }

        if (!hayAlguno && !hayVencidos && !hayProximos) {
            JLabel sinVenc = new JLabel("No hay vencimientos registrados en ningun expediente.");
            sinVenc.setFont(new Font("SansSerif", Font.ITALIC, 13));
            sinVenc.setForeground(UI.GRIS_TEXTO);
            listaPanel.add(sinVenc);
        }

        listaPanel.revalidate();
        listaPanel.repaint();
    }

    private JPanel encabezadoSeccion(String texto, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(color);
        p.add(lbl, BorderLayout.WEST);
        JSeparator sep = UI.separador();
        p.add(sep, BorderLayout.SOUTH);
        return p;
    }

    private JPanel tarjetaVencimiento(Cliente cli, Caso caso, Vencimiento v, Color acento) {
        UI.Card card = new UI.Card();
        card.setLayout(new BorderLayout(12, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(acento, 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        // Info izquierda
        JPanel info = new JPanel(new GridLayout(3, 1, 0, 2));
        info.setOpaque(false);

        JLabel descLbl = new JLabel(v.getDescripcion());
        descLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        descLbl.setForeground(UI.AZUL_OSCURO);

        JLabel clienteLbl = new JLabel(cli.getNombreCompleto() + "  |  " + caso.getRazonSocialEmpleador());
        clienteLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        clienteLbl.setForeground(UI.GRIS_TEXTO);

        String textoEstado;
        Color colorEstado;
        if (v.isCumplido()) {
            textoEstado = "\u2713 Cumplido";
            colorEstado = UI.VERDE;
        } else if (v.estaVencido()) {
            textoEstado = "VENCIDO el " + v.getFechaFormateada();
            colorEstado = UI.ROJO;
        } else {
            textoEstado = v.diasRestantes() + " dias restantes — " + v.getFechaFormateada();
            colorEstado = v.diasRestantes() <= 7 ? UI.NARANJA : UI.AZUL_ACENTO;
        }

        JLabel estadoLbl = new JLabel(textoEstado);
        estadoLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        estadoLbl.setForeground(colorEstado);

        info.add(descLbl); info.add(clienteLbl); info.add(estadoLbl);
        card.add(info, BorderLayout.CENTER);

        // Botones derecha
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setOpaque(false);

        if (!v.isCumplido()) {
            UI.BtnIcono btnCump = new UI.BtnIcono("\u2713 Cumplido", UI.VERDE, new Color(25, 130, 60));
            btnCump.addActionListener(e -> {
                v.setCumplido(true);
                caso.agregarEntradaBitacora("NOVEDAD", "Vencimiento marcado como cumplido: " + v.getDescripcion());
                refrescar();
            });
            btns.add(btnCump);
        }

        card.add(btns, BorderLayout.EAST);
        return card;
    }
}