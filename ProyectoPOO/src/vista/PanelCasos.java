package vista;

import modelo.*;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelCasos extends JPanel {
    private VentanaPrincipal ventana;
    private JPanel listaPanel;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PanelCasos(VentanaPrincipal v) {
        this.ventana = v;
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));
        construir();
    }

    private void construir() {
        removeAll();
        JPanel enc = new JPanel(new BorderLayout());
        enc.setOpaque(false);
        enc.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
        enc.add(UI.labelTitulo("\uD83D\uDCC2  Expedientes"), BorderLayout.WEST);
        UI.BtnPrimario btnNuevo = new UI.BtnPrimario("  + Nuevo Expediente", UI.VERDE, new Color(25,130,60));
        btnNuevo.addActionListener(e -> dialogoNuevoCaso());
        enc.add(btnNuevo, BorderLayout.EAST);
        add(enc, BorderLayout.NORTH);
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
        boolean hayCasos = false;
        for (Cliente cli : clientes) {
            for (Caso caso : cli.getCasos()) {
                hayCasos = true;
                listaPanel.add(tarjetaCaso(cli, caso));
                listaPanel.add(Box.createVerticalStrut(12));
            }
        }
        if (!hayCasos) {
            JLabel v = new JLabel("No hay expedientes. Cree un cliente primero y luego agregue un expediente.");
            v.setFont(new Font("SansSerif",Font.ITALIC,13)); v.setForeground(UI.GRIS_TEXTO);
            listaPanel.add(v);
        }
        listaPanel.revalidate(); listaPanel.repaint();
    }

    private JPanel tarjetaCaso(Cliente cli, Caso caso) {
        UI.Card card = new UI.Card();
        card.setLayout(new BorderLayout(10, 8));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // Encabezado de la tarjeta
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel tit = new JLabel("[" + caso.getTipoCaso() + "]  " + caso.getRazonSocialEmpleador());
        tit.setFont(new Font("SansSerif",Font.BOLD,14)); tit.setForeground(UI.AZUL_OSCURO);

        Color colEstado = colorEstado(caso.getEstado());
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        badgePanel.setOpaque(false);
        UI.Badge badge = new UI.Badge(caso.getEstado().getEtiqueta(), colEstado, true);
        badge.setPreferredSize(new Dimension(220, 22));
        badgePanel.add(badge);

        top.add(tit, BorderLayout.WEST); top.add(badgePanel, BorderLayout.EAST);

        // Info
        JPanel info = new JPanel(new GridLayout(1,3,10,0));
        info.setOpaque(false);
        info.add(infoItem("Cliente", cli.getNombreCompleto()));
        info.add(infoItem("Sueldo", String.format("$%,.2f", caso.getUltimoSueldo())));
        info.add(infoItem("Docs adjuntos", String.valueOf(caso.getDocumentos().size())));

        // Kanban buttons
        JPanel kanban = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        kanban.setOpaque(false);
        UI.BtnIcono btnAtras   = new UI.BtnIcono("◀ Retroceder", new Color(120,120,140), new Color(80,80,100));
        UI.BtnIcono btnAdelan  = new UI.BtnIcono("Avanzar ▶", UI.AZUL_ACENTO, new Color(31,97,141));
        UI.BtnIcono btnDetalle = new UI.BtnIcono("\uD83D\uDCC4 Detalle", UI.VERDE, new Color(25,130,60));
        UI.BtnIcono btnPDF     = new UI.BtnIcono("\uD83D\uDCCB PDF", new Color(150,60,180), new Color(110,30,140));

        btnAtras.addActionListener(e -> { caso.retrocederEstado(); guardarDatos(); refrescar(); });
        btnAdelan.addActionListener(e -> { caso.avanzarEstado(); guardarDatos(); refrescar(); });
        btnDetalle.addActionListener(e -> abrirDetalle(cli, caso));
        btnPDF.addActionListener(e -> generarResumenPDF(cli, caso));

        kanban.add(btnAtras); kanban.add(btnAdelan);
        kanban.add(Box.createHorizontalStrut(10));
        kanban.add(btnDetalle); kanban.add(btnPDF);

        card.add(top, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(kanban, BorderLayout.SOUTH);
        return card;
    }

    private JPanel infoItem(String label, String valor) {
        JPanel p = new JPanel(new BorderLayout(0,2)); p.setOpaque(false);
        p.add(UI.labelCampo(label), BorderLayout.NORTH);
        JLabel v = new JLabel(valor); v.setFont(new Font("SansSerif",Font.BOLD,12)); v.setForeground(UI.AZUL_OSCURO);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private Color colorEstado(EstadoCaso e) {
        switch(e) {
            case INTERCAMBIO_TELEGRAFICO: return new Color(41,128,185);
            case MEDIACION:               return new Color(211,84,0);
            case JUICIO:                  return new Color(192,57,43);
            case SENTENCIA:               return new Color(39,174,96);
            default: return UI.GRIS_TEXTO;
        }
    }

    // ==================== DIALOGO DETALLE ====================
    private void abrirDetalle(Cliente cli, Caso caso) {
        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),
            "Expediente — " + cli.getNombreCompleto(), true);
        dlg.setSize(750, 700);
        dlg.setLocationRelativeTo(this);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UI.F_SUBTITULO);
        tabs.addTab("Datos Generales", tabDatos(cli, caso));
        tabs.addTab("Bitacora", tabBitacora(caso));
        tabs.addTab("Testigos", tabTestigos(caso));
        tabs.addTab("Documentos", tabDocumentos(caso));
        tabs.addTab("Vencimientos", tabVencimientos(caso));

        dlg.add(tabs);
        dlg.setVisible(true);
        refrescar();
    }

    // ---- Tab Datos ----
    private JPanel tabDatos(Cliente cli, Caso caso) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        p.setBackground(UI.GRIS_FONDO);

        p.add(secTitulo("Datos del Cliente"));
        p.add(fila2("Nombre", cli.getNombreCompleto(), "DNI", cli.getDni()));
        p.add(Box.createVerticalStrut(8));
        p.add(fila2("CUIL", cli.getCuil(), "Telefono", cli.getTelefono()));
        p.add(Box.createVerticalStrut(8));
        p.add(fila2("Email", cli.getEmail(), "Domicilio", cli.getDomicilio()));
        p.add(Box.createVerticalStrut(16));

        p.add(secTitulo("Datos del Empleador"));
        UI.Campo cRazon  = campo(caso.getRazonSocialEmpleador());
        UI.Campo cCuit   = campo(caso.getCuitEmpleador());
        UI.Campo cDomEmp = campo(caso.getDomicilioEmpleador());
        UI.Campo cIngreso= campo(caso.getFechaIngreso() != null ? caso.getFechaIngreso().format(FMT) : "");
        UI.Campo cEgreso = campo(caso.getFechaEgreso()  != null ? caso.getFechaEgreso().format(FMT)  : "");
        UI.Campo cSueldo = campo(String.valueOf(caso.getUltimoSueldo()));
        UI.Campo cMonto  = campo(String.valueOf(caso.getMontoReclamado()));

        p.add(fila2c("Razon Social", cRazon, "CUIT Empleador", cCuit));
        p.add(Box.createVerticalStrut(8));
        p.add(filaFull("Domicilio Empleador", cDomEmp));
        p.add(Box.createVerticalStrut(8));
        p.add(fila2c("Fecha Ingreso (DD/MM/AAAA)", cIngreso, "Fecha Egreso (DD/MM/AAAA)", cEgreso));
        p.add(Box.createVerticalStrut(8));
        p.add(fila2c("Ultimo Sueldo ($)", cSueldo, "Monto Reclamado ($)", cMonto));
        p.add(Box.createVerticalStrut(8));

        JCheckBox chkPre = new JCheckBox("Preaviso otorgado"); chkPre.setOpaque(false);
        chkPre.setSelected(caso.isPreavisoOtorgado()); chkPre.setFont(UI.F_CAMPO);
        p.add(chkPre);
        p.add(Box.createVerticalStrut(12));

        p.add(UI.labelCampo("Observaciones Generales"));
        UI.Area areaObs = new UI.Area(3, 40);
        areaObs.setText(caso.getObservaciones());
        areaObs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        p.add(areaObs);
        p.add(Box.createVerticalStrut(14));

        UI.BtnPrimario btnGuardar = new UI.BtnPrimario("  Guardar Cambios", UI.VERDE, new Color(25,130,60));
        btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try {
                caso.setRazonSocialEmpleador(cRazon.getText().trim());
                caso.setCuitEmpleador(cCuit.getText().trim());
                caso.setDomicilioEmpleador(cDomEmp.getText().trim());
                caso.setFechaIngreso(LocalDate.parse(cIngreso.getText().trim(), FMT));
                caso.setFechaEgreso(LocalDate.parse(cEgreso.getText().trim(), FMT));
                caso.setUltimoSueldo(Double.parseDouble(cSueldo.getText().replace(",",".")));
                caso.setMontoReclamado(Double.parseDouble(cMonto.getText().replace(",",".")));
                caso.setPreavisoOtorgado(chkPre.isSelected());
                caso.setObservaciones(areaObs.getText());
                caso.agregarEntradaBitacora("NOVEDAD","Datos del expediente actualizados.");
                JOptionPane.showMessageDialog(null,"Guardado correctamente.","OK",JOptionPane.INFORMATION_MESSAGE);
                refrescar();
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(null,"Revise los datos (fechas DD/MM/AAAA, montos numericos).","Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        return p;
    }

    // ---- Tab Bitacora ----
    private JPanel tabBitacora(Caso caso) {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        p.setBackground(UI.GRIS_FONDO);

        // Agregar entrada
        JPanel formAdd = new JPanel(new BorderLayout(8,0));
        formAdd.setOpaque(false);
        UI.Combo cmbTipo = new UI.Combo(new String[]{"NOVEDAD","LLAMADA","AUDIENCIA","DOCUMENTO","OTRO"});
        UI.Campo txtDesc = new UI.Campo(); txtDesc.setToolTipText("Descripcion");
        UI.BtnIcono btnAdd = new UI.BtnIcono("+ Agregar", UI.VERDE, new Color(25,130,60));
        formAdd.add(cmbTipo, BorderLayout.WEST);
        formAdd.add(txtDesc, BorderLayout.CENTER);
        formAdd.add(btnAdd, BorderLayout.EAST);
        p.add(formAdd, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setEditable(false); area.setFont(UI.F_MONO);
        area.setBackground(UI.AZUL_OSCURO); area.setForeground(new Color(130,220,130));
        area.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));

        refrescarBitacora(area, caso);
        p.add(new JScrollPane(area), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            String desc = txtDesc.getText().trim();
            if (!desc.isEmpty()) {
                caso.agregarEntradaBitacora((String)cmbTipo.getSelectedItem(), desc);
                txtDesc.setText("");
                refrescarBitacora(area, caso);
            }
        });
        return p;
    }

    private void refrescarBitacora(JTextArea area, Caso caso) {
        StringBuilder sb = new StringBuilder();
        List<EntradaBitacora> bit = caso.getBitacora();
        for (int i = bit.size()-1; i >= 0; i--) sb.append(bit.get(i).toString()).append("\n");
        area.setText(sb.toString());
    }

    // ---- Tab Testigos ----
    private JPanel tabTestigos(Caso caso) {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        p.setBackground(UI.GRIS_FONDO);

        DefaultListModel<String> modelo = new DefaultListModel<>();
        JList<String> lista = new JList<>(modelo);
        lista.setFont(UI.F_CAMPO);
        refrescarTestigos(modelo, caso);

        JPanel form = new JPanel(new GridLayout(2,4,8,8));
        form.setOpaque(false);
        UI.Campo cNombre = campo(""); UI.Campo cDni  = campo("");
        UI.Campo cTel    = campo(""); UI.Campo cEmail = campo("");
        UI.Area cObs = new UI.Area(2,20);

        form.add(UI.filaCampo("Nombre Completo", cNombre));
        form.add(UI.filaCampo("DNI", cDni));
        form.add(UI.filaCampo("Telefono", cTel));
        form.add(UI.filaCampo("Email", cEmail));

        UI.BtnPrimario btnAdd = new UI.BtnPrimario("  + Agregar Testigo", UI.VERDE, new Color(25,130,60));
        UI.BtnIcono btnElim = new UI.BtnIcono("\uD83D\uDDD1 Quitar", UI.ROJO, new Color(140,30,20));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));
        botones.setOpaque(false); botones.add(btnAdd); botones.add(btnElim);

        JPanel norte = new JPanel(new BorderLayout(0,8));
        norte.setOpaque(false);
        norte.add(form, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.SOUTH);

        p.add(norte, BorderLayout.NORTH);
        p.add(new JScrollPane(lista), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            String n = cNombre.getText().trim();
            if (n.isEmpty()) { JOptionPane.showMessageDialog(null,"Ingrese el nombre del testigo.","Error",JOptionPane.ERROR_MESSAGE); return; }
            caso.getTestigos().add(new Testigo(n, cDni.getText().trim(), cTel.getText().trim(), cEmail.getText().trim(), ""));
            caso.agregarEntradaBitacora("NOVEDAD","Testigo agregado: "+n);
            cNombre.setText(""); cDni.setText(""); cTel.setText(""); cEmail.setText("");
            refrescarTestigos(modelo, caso);
        });

        btnElim.addActionListener(e -> {
            int idx = lista.getSelectedIndex();
            if (idx >= 0) {
                Testigo t = caso.getTestigos().get(idx);
                caso.getTestigos().remove(idx);
                caso.agregarEntradaBitacora("NOVEDAD","Testigo eliminado: "+t.getNombreCompleto());
                refrescarTestigos(modelo, caso);
            }
        });
        return p;
    }

    private void refrescarTestigos(DefaultListModel<String> m, Caso caso) {
        m.clear();
        for (Testigo t : caso.getTestigos()) m.addElement(t.toString());
    }

    // ---- Tab Documentos ----
    private JPanel tabDocumentos(Caso caso) {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        p.setBackground(UI.GRIS_FONDO);

        DefaultListModel<String> modelo = new DefaultListModel<>();
        JList<String> lista = new JList<>(modelo);
        lista.setFont(UI.F_CAMPO);
        refrescarDocs(modelo, caso);

        JPanel form = new JPanel(new GridLayout(1,3,8,0));
        form.setOpaque(false);
        UI.Campo cNombre = campo("");
        UI.Combo cTipo = new UI.Combo(new String[]{
            "RECIBO_SUELDO","TELEGRAMA","CONTRATO","WHATSAPP","FOTO","OTRO"});
        UI.Campo cDesc = campo("");

        form.add(UI.filaCampo("Nombre del Documento", cNombre));
        form.add(UI.filaCampo("Tipo", cTipo));
        form.add(UI.filaCampo("Descripcion", cDesc));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));
        botones.setOpaque(false);

        UI.BtnPrimario btnAdj = new UI.BtnPrimario("  \uD83D\uDCCE Adjuntar Archivo", UI.AZUL_ACENTO, new Color(31,97,141));
        UI.BtnIcono btnElim = new UI.BtnIcono("\uD83D\uDDD1 Quitar", UI.ROJO, new Color(140,30,20));
        botones.add(btnAdj); botones.add(btnElim);

        JPanel norte = new JPanel(new BorderLayout(0,8));
        norte.setOpaque(false); norte.add(form, BorderLayout.NORTH); norte.add(botones, BorderLayout.SOUTH);
        p.add(norte, BorderLayout.NORTH);
        p.add(new JScrollPane(lista), BorderLayout.CENTER);

        btnAdj.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Seleccionar archivo");
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                String nombre = cNombre.getText().trim().isEmpty() ? f.getName() : cNombre.getText().trim();
                DocumentoAdjunto.TipoDocumento tipo;
                try { tipo = DocumentoAdjunto.TipoDocumento.valueOf((String)cTipo.getSelectedItem()); }
                catch(Exception ex) { tipo = DocumentoAdjunto.TipoDocumento.OTRO; }
                caso.getDocumentos().add(new DocumentoAdjunto(nombre, tipo, f.getAbsolutePath(), cDesc.getText().trim()));
                caso.agregarEntradaBitacora("DOCUMENTO","Archivo adjuntado: "+nombre);
                cNombre.setText(""); cDesc.setText("");
                refrescarDocs(modelo, caso);
            }
        });

        btnElim.addActionListener(e -> {
            int idx = lista.getSelectedIndex();
            if (idx >= 0) {
                DocumentoAdjunto d = caso.getDocumentos().get(idx);
                caso.getDocumentos().remove(idx);
                caso.agregarEntradaBitacora("DOCUMENTO","Documento eliminado: "+d.getNombre());
                refrescarDocs(modelo, caso);
            }
        });
        return p;
    }

    private void refrescarDocs(DefaultListModel<String> m, Caso caso) {
        m.clear();
        for (DocumentoAdjunto d : caso.getDocumentos()) m.addElement(d.toString());
    }

    // ---- Tab Vencimientos ----
    private JPanel tabVencimientos(Caso caso) {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        p.setBackground(UI.GRIS_FONDO);

        DefaultListModel<String> modelo = new DefaultListModel<>();
        JList<String> lista = new JList<>(modelo);
        lista.setFont(UI.F_MONO);
        refrescarVenc(modelo, caso);

        JPanel form = new JPanel(new GridLayout(1,2,10,0));
        form.setOpaque(false);
        UI.Campo cDesc  = campo("");
        UI.Campo cFecha = campo("DD/MM/AAAA");

        form.add(UI.filaCampo("Descripcion del Vencimiento", cDesc));
        form.add(UI.filaCampo("Fecha (DD/MM/AAAA)", cFecha));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));
        botones.setOpaque(false);
        UI.BtnPrimario btnAdd = new UI.BtnPrimario("  + Agregar", UI.NARANJA, new Color(160,60,0));
        UI.BtnIcono btnCump = new UI.BtnIcono("\u2713 Cumplido", UI.VERDE, new Color(25,130,60));
        UI.BtnIcono btnElim = new UI.BtnIcono("\uD83D\uDDD1 Quitar", UI.ROJO, new Color(140,30,20));
        botones.add(btnAdd); botones.add(btnCump); botones.add(btnElim);

        JPanel norte = new JPanel(new BorderLayout(0,8));
        norte.setOpaque(false); norte.add(form, BorderLayout.NORTH); norte.add(botones, BorderLayout.SOUTH);
        p.add(norte, BorderLayout.NORTH);
        p.add(new JScrollPane(lista), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            try {
                String desc = cDesc.getText().trim();
                LocalDate fecha = LocalDate.parse(cFecha.getText().trim(), FMT);
                caso.getVencimientos().add(new Vencimiento(desc, fecha));
                caso.agregarEntradaBitacora("NOVEDAD","Vencimiento agregado: "+desc+" — "+cFecha.getText().trim());
                cDesc.setText(""); cFecha.setText("DD/MM/AAAA");
                refrescarVenc(modelo, caso);
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(null,"Fecha en formato DD/MM/AAAA.","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCump.addActionListener(e -> {
            int idx = lista.getSelectedIndex();
            if (idx >= 0) { caso.getVencimientos().get(idx).setCumplido(true); refrescarVenc(modelo, caso); }
        });

        btnElim.addActionListener(e -> {
            int idx = lista.getSelectedIndex();
            if (idx >= 0) { caso.getVencimientos().remove(idx); refrescarVenc(modelo, caso); }
        });
        return p;
    }

    private void refrescarVenc(DefaultListModel<String> m, Caso caso) {
        m.clear();
        for (Vencimiento v : caso.getVencimientos()) m.addElement(v.toString());
    }

    // ==================== DIALOGO NUEVO CASO ====================
    private void guardarDatos() {
        modelo.Repositorio repo = modelo.Repositorio.getInstance();
        if (repo.getAbogadoActual() != null) {
            persistencia.PersistenciaDatos.guardar(
                repo.getAbogadoActual().getDni(), repo.getClientes());
        }
    }

    private void dialogoNuevoCaso() {
        List<Cliente> clientes = Repositorio.getInstance().getClientes();
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Primero registre un cliente.","Sin clientes",JOptionPane.WARNING_MESSAGE);
            ventana.irAClientes(); return;
        }

        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Nuevo Expediente",true);
        dlg.setSize(520, 560); dlg.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20,24,20,24));
        panel.setBackground(UI.GRIS_FONDO);

        String[] nombresClientes = clientes.stream().map(Cliente::toString).toArray(String[]::new);
        UI.Combo cmbCliente = new UI.Combo(nombresClientes);
        UI.Combo cmbTipo    = new UI.Combo(new String[]{"DESPIDO","ART","OTRO"});
        UI.Campo cRazon     = campo(""); UI.Campo cCuit = campo("");
        UI.Campo cDomEmp    = campo(""); UI.Campo cIngreso = campo("DD/MM/AAAA");
        UI.Campo cEgreso    = campo("DD/MM/AAAA"); UI.Campo cSueldo = campo("");

        cmbCliente.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        cmbTipo.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));

        String[][] filas = {
            {"Razon Social Empleador","CUIT Empleador"},
            {"Domicilio Empleador",""},
            {"Fecha Ingreso (DD/MM/AAAA)","Fecha Egreso (DD/MM/AAAA)"},
            {"Ultimo Sueldo ($)",""}
        };
        UI.Campo[][] camposFila = {
            {cRazon,cCuit},{cDomEmp,null},{cIngreso,cEgreso},{cSueldo,null}
        };

        panel.add(UI.labelCampo("Cliente")); panel.add(Box.createVerticalStrut(4)); panel.add(cmbCliente);
        panel.add(Box.createVerticalStrut(10));
        panel.add(UI.labelCampo("Tipo de Caso")); panel.add(Box.createVerticalStrut(4)); panel.add(cmbTipo);
        panel.add(Box.createVerticalStrut(10));

        for (int i = 0; i < filas.length; i++) {
            if (camposFila[i][1] != null) {
                JPanel fila = new JPanel(new GridLayout(1,2,10,0)); fila.setOpaque(false);
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE,60));
                fila.add(UI.filaCampo(filas[i][0], camposFila[i][0]));
                fila.add(UI.filaCampo(filas[i][1], camposFila[i][1]));
                panel.add(fila);
            } else {
                camposFila[i][0].setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
                panel.add(UI.filaCampo(filas[i][0], camposFila[i][0]));
            }
            panel.add(Box.createVerticalStrut(10));
        }

        UI.BtnPrimario btnGuardar = new UI.BtnPrimario("  Crear Expediente", UI.VERDE, new Color(25,130,60));
        btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        panel.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try {
                Cliente cli = clientes.get(cmbCliente.getSelectedIndex());
                LocalDate ing = LocalDate.parse(cIngreso.getText().trim(), FMT);
                LocalDate eg  = LocalDate.parse(cEgreso.getText().trim(), FMT);
                double sueldo = Double.parseDouble(cSueldo.getText().replace(",","."));
                Repositorio.getInstance().agregarCaso(
                    cli, (String)cmbTipo.getSelectedItem(),
                    cRazon.getText().trim(), cCuit.getText().trim(),
                    cDomEmp.getText().trim(), ing, eg, sueldo
                );
                dlg.dispose(); guardarDatos(); refrescar();
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(dlg,"Revise los datos.","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.add(new JScrollPane(panel)); dlg.setVisible(true);
    }

    // ==================== PDF RESUMEN ====================
    private void generarResumenPDF(Cliente cli, Caso caso) {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("        FICHA RESUMEN DE EXPEDIENTE\n");
        sb.append("==========================================\n");
        sb.append("CLIENTE\n");
        sb.append("  Nombre   : ").append(cli.getNombreCompleto()).append("\n");
        sb.append("  DNI      : ").append(cli.getDni()).append("\n");
        sb.append("  CUIL     : ").append(cli.getCuil()).append("\n");
        sb.append("  Tel      : ").append(cli.getTelefono()).append("\n");
        sb.append("  Email    : ").append(cli.getEmail()).append("\n");
        sb.append("  Domicilio: ").append(cli.getDomicilio()).append("\n");
        sb.append("------------------------------------------\n");
        sb.append("EMPLEADOR\n");
        sb.append("  Razon Social: ").append(caso.getRazonSocialEmpleador()).append("\n");
        sb.append("  CUIT        : ").append(caso.getCuitEmpleador()).append("\n");
        sb.append("  Domicilio   : ").append(caso.getDomicilioEmpleador()).append("\n");
        sb.append("------------------------------------------\n");
        sb.append("DATOS LABORALES\n");
        sb.append("  Tipo de caso: ").append(caso.getTipoCaso()).append("\n");
        sb.append("  Ingreso     : ").append(caso.getFechaIngreso() != null ? caso.getFechaIngreso().format(FMT) : "-").append("\n");
        sb.append("  Egreso      : ").append(caso.getFechaEgreso()  != null ? caso.getFechaEgreso().format(FMT)  : "-").append("\n");
        sb.append(String.format("  Ult. Sueldo : $%,.2f\n", caso.getUltimoSueldo()));
        sb.append(String.format("  Monto recl. : $%,.2f\n", caso.getMontoReclamado()));
        sb.append("------------------------------------------\n");
        sb.append("ESTADO PROCESAL: ").append(caso.getEstado().getEtiqueta()).append("\n");
        sb.append("------------------------------------------\n");
        sb.append("TESTIGOS (").append(caso.getTestigos().size()).append(")\n");
        for (Testigo t : caso.getTestigos())
            sb.append("  - ").append(t.getNombreCompleto()).append(" | ").append(t.getTelefono()).append("\n");
        sb.append("------------------------------------------\n");
        sb.append("DOCUMENTOS ADJUNTOS (").append(caso.getDocumentos().size()).append(")\n");
        for (DocumentoAdjunto d : caso.getDocumentos())
            sb.append("  - ").append(d.toString()).append("\n");
        sb.append("------------------------------------------\n");
        sb.append("VENCIMIENTOS\n");
        for (Vencimiento v : caso.getVencimientos())
            sb.append("  ").append(v.toString()).append("\n");
        sb.append("==========================================\n");
        if (caso.getObservaciones() != null && !caso.getObservaciones().isEmpty()) {
            sb.append("OBSERVACIONES\n").append(caso.getObservaciones()).append("\n");
            sb.append("==========================================\n");
        }

        JDialog dlg = new JDialog((Frame)SwingUtilities.getWindowAncestor(this),"Ficha Resumen — " + cli.getNombreCompleto(),true);
        dlg.setSize(640, 620); dlg.setLocationRelativeTo(this);
        JTextArea area = new JTextArea(sb.toString());
        area.setFont(UI.F_MONO); area.setEditable(false);
        area.setBackground(UI.AZUL_OSCURO); area.setForeground(new Color(200,230,200));
        area.setBorder(BorderFactory.createEmptyBorder(12,14,12,14));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(UI.GRIS_FONDO);
        UI.BtnPrimario btnGuardar = new UI.BtnPrimario("  Guardar como TXT", UI.VERDE, new Color(25,130,60));
        btnGuardar.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("Resumen_" + cli.getDni() + ".txt"));
            if (fc.showSaveDialog(dlg) == JFileChooser.APPROVE_OPTION) {
                try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
                    pw.print(sb.toString());
                    JOptionPane.showMessageDialog(dlg,"Guardado correctamente.","OK",JOptionPane.INFORMATION_MESSAGE);
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(dlg,"Error al guardar.","Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btns.add(btnGuardar);

        dlg.add(new JScrollPane(area), BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ==================== HELPERS ====================
    private UI.Campo campo(String val) {
        UI.Campo c = new UI.Campo(val);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        return c;
    }

    private JPanel secTitulo(String txt) {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
        JLabel l = UI.labelSub(txt); l.setForeground(UI.AZUL_OSCURO);
        p.add(l, BorderLayout.WEST); p.add(UI.separador(), BorderLayout.SOUTH);
        return p;
    }

    private JPanel fila2(String l1, String v1, String l2, String v2) {
        JPanel p = new JPanel(new GridLayout(1,2,14,0)); p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        p.add(datoRO(l1,v1)); p.add(datoRO(l2,v2)); return p;
    }

    private JPanel fila2c(String l1, JComponent c1, String l2, JComponent c2) {
        JPanel p = new JPanel(new GridLayout(1,2,14,0)); p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        p.add(UI.filaCampo(l1,c1)); p.add(UI.filaCampo(l2,c2)); return p;
    }

    private JPanel filaFull(String label, JComponent c) {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        p.add(UI.filaCampo(label,c)); return p;
    }

    private JPanel datoRO(String label, String valor) {
        JPanel p = new JPanel(new BorderLayout(0,2)); p.setOpaque(false);
        p.add(UI.labelCampo(label), BorderLayout.NORTH);
        JLabel v = new JLabel(valor); v.setFont(new Font("SansSerif",Font.BOLD,12));
        v.setForeground(UI.AZUL_OSCURO); p.add(v, BorderLayout.CENTER);
        return p;
    }
}