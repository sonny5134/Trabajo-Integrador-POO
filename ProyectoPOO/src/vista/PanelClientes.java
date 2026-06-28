package vista;

import modelo.*;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class PanelClientes extends JPanel {
    private VentanaPrincipal ventana;
    private JPanel listaPanel;

    public PanelClientes(VentanaPrincipal v) {
        this.ventana = v;
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));
        construir();
    }

    private void construir() {
        removeAll();

        // Encabezado
        JPanel enc = new JPanel(new BorderLayout());
        enc.setOpaque(false);
        enc.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        enc.add(UI.labelTitulo("\uD83D\uDC64  Gestion de Clientes"), BorderLayout.WEST);
        UI.BtnPrimario btnNuevo = new UI.BtnPrimario("  + Nuevo Cliente", UI.VERDE, new Color(25,130,60));
        btnNuevo.addActionListener(e -> dialogoNuevoCliente());
        enc.add(btnNuevo, BorderLayout.EAST);
        add(enc, BorderLayout.NORTH);

        // Lista
        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setOpaque(false);
        add(listaPanel, BorderLayout.CENTER);

        refrescar();
    }

    public void refrescar() {
        if (listaPanel == null) return;
        listaPanel.removeAll();
        List<Cliente> clientes = Repositorio.getInstance().getClientes();

        if (clientes.isEmpty()) {
            JLabel vacio = new JLabel("No hay clientes registrados aun. Haga clic en '+ Nuevo Cliente'.");
            vacio.setFont(new Font("SansSerif",Font.ITALIC,13));
            vacio.setForeground(UI.GRIS_TEXTO);
            listaPanel.add(vacio);
        } else {
            for (Cliente c : clientes) {
                listaPanel.add(tarjetaCliente(c));
                listaPanel.add(Box.createVerticalStrut(10));
            }
        }
        listaPanel.revalidate();
        listaPanel.repaint();
    }

    private JPanel tarjetaCliente(Cliente c) {
        UI.Card card = new UI.Card();
        card.setLayout(new BorderLayout(12, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Info principal
        JPanel info = new JPanel(new GridLayout(3,1,0,2));
        info.setOpaque(false);

        JLabel nombre = new JLabel(c.getNombreCompleto());
        nombre.setFont(new Font("SansSerif",Font.BOLD,14));
        nombre.setForeground(UI.AZUL_OSCURO);

        JLabel datos = new JLabel("DNI: " + c.getDni() + "  |  CUIL: " + c.getCuil() + "  |  Tel: " + c.getTelefono());
        datos.setFont(new Font("SansSerif",Font.PLAIN,12));
        datos.setForeground(UI.GRIS_TEXTO);

        JLabel casos = new JLabel(c.getCasos().size() + " expediente(s)  |  " + c.getEmail());
        casos.setFont(new Font("SansSerif",Font.PLAIN,11));
        casos.setForeground(UI.AZUL_ACENTO);

        info.add(nombre); info.add(datos); info.add(casos);
        card.add(info, BorderLayout.CENTER);

        // Botones
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setOpaque(false);

        UI.BtnIcono btnCasos = new UI.BtnIcono("\uD83D\uDCC2 Expedientes", UI.AZUL_ACENTO, new Color(31,97,141));
        UI.BtnIcono btnEditar = new UI.BtnIcono("\u270F Editar", new Color(100,120,150), new Color(70,90,120));
        UI.BtnIcono btnElim  = new UI.BtnIcono("\uD83D\uDDD1 Eliminar", UI.ROJO, new Color(140,30,20));

        btnCasos.addActionListener(e -> {
            ventana.irACasos();
        });
        btnEditar.addActionListener(e -> dialogoEditarCliente(c));
        btnElim.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(this,
                "Eliminar cliente '" + c.getNombreCompleto() + "' y todos sus expedientes?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                Repositorio.getInstance().eliminarCliente(c);
                guardarDatos();
                refrescar();
            }
        });

        btns.add(btnCasos); btns.add(btnEditar); btns.add(btnElim);
        card.add(btns, BorderLayout.EAST);

        return card;
    }

    private void dialogoNuevoCliente() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Cliente", true);
        dlg.setSize(500, 480);
        dlg.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        panel.setBackground(UI.GRIS_FONDO);

        UI.Campo[] campos = new UI.Campo[6];
        String[] labels = {"Nombre y Apellido Completo","DNI","CUIL/CUIT","Telefono","Email","Domicilio"};
        for (int i = 0; i < 6; i++) {
            campos[i] = new UI.Campo();
            campos[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            panel.add(UI.labelCampo(labels[i]));
            panel.add(Box.createVerticalStrut(4));
            panel.add(campos[i]);
            panel.add(Box.createVerticalStrut(12));
        }

        UI.BtnPrimario btnGuardar = new UI.BtnPrimario("  Guardar Cliente", UI.VERDE, new Color(25,130,60));
        btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            String nombre = campos[0].getText().trim();
            String dni    = campos[1].getText().trim();
            if (nombre.isEmpty() || dni.isEmpty()) {
                JOptionPane.showMessageDialog(dlg,"Nombre y DNI son obligatorios.","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            Repositorio.getInstance().agregarCliente(
                nombre, dni, campos[2].getText().trim(),
                campos[3].getText().trim(), campos[4].getText().trim(), campos[5].getText().trim()
            );
            dlg.dispose();
            refrescar();
            guardarDatos();
        });

        dlg.add(new JScrollPane(panel));
        dlg.setVisible(true);
    }

    private void guardarDatos() {
        modelo.Repositorio repo = modelo.Repositorio.getInstance();
        if (repo.getAbogadoActual() != null) {
            persistencia.PersistenciaDatos.guardar(
                repo.getAbogadoActual().getDni(), repo.getClientes());
        }
    }

    private void dialogoEditarCliente(Cliente c) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Editar Cliente", true);
        dlg.setSize(500, 480);
        dlg.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        panel.setBackground(UI.GRIS_FONDO);

        String[] vals = {c.getNombreCompleto(),c.getDni(),c.getCuil(),c.getTelefono(),c.getEmail(),c.getDomicilio()};
        String[] labels = {"Nombre y Apellido Completo","DNI","CUIL/CUIT","Telefono","Email","Domicilio"};
        UI.Campo[] campos = new UI.Campo[6];
        for (int i = 0; i < 6; i++) {
            campos[i] = new UI.Campo(vals[i]);
            campos[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            panel.add(UI.labelCampo(labels[i]));
            panel.add(Box.createVerticalStrut(4));
            panel.add(campos[i]);
            panel.add(Box.createVerticalStrut(12));
        }

        UI.BtnPrimario btnGuardar = new UI.BtnPrimario("  Guardar Cambios", UI.AZUL_ACENTO, new Color(31,97,141));
        btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            c.setNombreCompleto(campos[0].getText().trim());
            c.setDni(campos[1].getText().trim());
            c.setCuil(campos[2].getText().trim());
            c.setTelefono(campos[3].getText().trim());
            c.setEmail(campos[4].getText().trim());
            c.setDomicilio(campos[5].getText().trim());
            dlg.dispose();
            refrescar();
        });

        dlg.add(new JScrollPane(panel));
        dlg.setVisible(true);
    }
}