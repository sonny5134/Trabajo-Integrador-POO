package vista;

import modelo.*;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class PanelDashboard extends JPanel {
    private VentanaPrincipal ventana;
    private JPanel contenido;

    public PanelDashboard(VentanaPrincipal v) {
        this.ventana = v;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));
        contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        add(contenido, BorderLayout.NORTH);
        refrescar();
    }

    public void refrescar() {
        contenido.removeAll();
        Repositorio repo = Repositorio.getInstance();
        Abogado ab = repo.getAbogadoActual();

        // Titulo
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        JLabel titulo = UI.labelTitulo("Bienvenido" + (ab != null ? ", Dr/a. " + ab.getNombreCompleto() : "") + "  \uD83D\uDC4B");
        JLabel sub = UI.labelSub("Resumen general del estudio");
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(sub, BorderLayout.CENTER);
        contenido.add(encabezado);
        contenido.add(Box.createVerticalStrut(24));

        // Tarjetas de estadisticas
        int totalClientes = repo.getClientes().size();
        int totalCasos    = repo.getTotalCasos();
        List<String> alertas = repo.getAlertasVencimiento(10);

        JPanel tarjetas = new JPanel(new GridLayout(1, 3, 16, 0));
        tarjetas.setOpaque(false);
        tarjetas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        tarjetas.add(tarjeta("\uD83D\uDC64 Clientes", String.valueOf(totalClientes), "registrados", UI.AZUL_ACENTO));
        tarjetas.add(tarjeta("\uD83D\uDCC2 Expedientes", String.valueOf(totalCasos), "activos", UI.VERDE));
        tarjetas.add(tarjeta("\u26A0 Vencimientos", String.valueOf(alertas.size()), "proximos o vencidos", alertas.isEmpty() ? UI.VERDE : UI.ROJO));
        contenido.add(tarjetas);
        contenido.add(Box.createVerticalStrut(28));

        // Alertas
        if (!alertas.isEmpty()) {
            JLabel tituloAlertas = UI.labelSub("\u26A0  Alertas de Vencimiento (proximos 10 dias)");
            tituloAlertas.setForeground(UI.ROJO);
            contenido.add(tituloAlertas);
            contenido.add(Box.createVerticalStrut(8));

            for (String a : alertas) {
                JPanel fila = new UI.Card();
                fila.setLayout(new BorderLayout());
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
                boolean vencido = a.startsWith("VENCIDO");
                JLabel lbl = new JLabel("  " + (vencido ? "\uD83D\uDD34" : "\uD83D\uDFE1") + "  " + a);
                lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                lbl.setForeground(vencido ? UI.ROJO : new Color(180, 120, 0));
                fila.add(lbl, BorderLayout.CENTER);
                contenido.add(fila);
                contenido.add(Box.createVerticalStrut(5));
            }
            contenido.add(Box.createVerticalStrut(20));
        }

        // Ultimos clientes
        List<Cliente> clientes = repo.getClientes();
        if (!clientes.isEmpty()) {
            contenido.add(UI.labelSub("\uD83D\uDC64  Ultimos Clientes Registrados"));
            contenido.add(Box.createVerticalStrut(10));
            int mostrar = Math.min(clientes.size(), 5);
            for (int i = clientes.size() - 1; i >= clientes.size() - mostrar; i--) {
                Cliente c = clientes.get(i);
                JPanel fila = new UI.Card();
                fila.setLayout(new BorderLayout(12, 0));
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

                JLabel nombreLbl = new JLabel(c.getNombreCompleto());
                nombreLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
                nombreLbl.setForeground(UI.AZUL_OSCURO);

                JLabel info = new JLabel("DNI: " + c.getDni() + "   |   " + c.getCasos().size() + " caso(s)");
                info.setFont(new Font("SansSerif", Font.PLAIN, 11));
                info.setForeground(UI.GRIS_TEXTO);

                JPanel texto = new JPanel(new GridLayout(2,1,0,2));
                texto.setOpaque(false);
                texto.add(nombreLbl); texto.add(info);
                fila.add(texto, BorderLayout.CENTER);

                UI.BtnIcono btnVer = new UI.BtnIcono("Ver expedientes", UI.AZUL_ACENTO, new Color(31,97,141));
                btnVer.addActionListener(e -> ventana.irACasos());
                fila.add(btnVer, BorderLayout.EAST);

                contenido.add(fila);
                contenido.add(Box.createVerticalStrut(6));
            }
        }

        if (ab != null) {
            contenido.add(Box.createVerticalStrut(20));
            UI.Card cardAbogado = new UI.Card();
            cardAbogado.setLayout(new GridLayout(2,2,10,6));
            cardAbogado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            cardAbogado.add(dato("Matricula", ab.getMatricula()));
            cardAbogado.add(dato("CUIT", ab.getCuit()));
            cardAbogado.add(dato("DNI", ab.getDni()));
            cardAbogado.add(dato("Sistema", "LiquidaLab v1.0"));
            contenido.add(UI.labelSub("\uD83D\uDCBC  Datos del Estudio"));
            contenido.add(Box.createVerticalStrut(8));
            contenido.add(cardAbogado);
        }

        contenido.revalidate();
        contenido.repaint();
    }

    private JPanel tarjeta(String titulo, String valor, String desc, Color color) {
        UI.Card p = new UI.Card();
        p.setLayout(new BorderLayout(0, 6));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color.brighter(), 1, true),
            BorderFactory.createEmptyBorder(16,18,16,18)));

        JLabel tit = new JLabel(titulo);
        tit.setFont(new Font("SansSerif",Font.BOLD,12));
        tit.setForeground(UI.GRIS_TEXTO);

        JLabel val = new JLabel(valor);
        val.setFont(new Font("SansSerif",Font.BOLD,32));
        val.setForeground(color);

        JLabel d = new JLabel(desc);
        d.setFont(new Font("SansSerif",Font.PLAIN,11));
        d.setForeground(UI.GRIS_TEXTO);

        p.add(tit, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        p.add(d, BorderLayout.SOUTH);
        return p;
    }

    private JPanel dato(String label, String valor) {
        JPanel p = new JPanel(new BorderLayout(0,2)); p.setOpaque(false);
        p.add(UI.labelCampo(label), BorderLayout.NORTH);
        JLabel v = new JLabel(valor); v.setFont(new Font("SansSerif",Font.BOLD,12)); v.setForeground(UI.AZUL_OSCURO);
        p.add(v, BorderLayout.CENTER);
        return p;
    }
}
