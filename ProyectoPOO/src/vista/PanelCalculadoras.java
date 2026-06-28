package vista;

import modelo.FabricaLiquidacion;
import ui.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PanelCalculadoras extends JPanel {

    private CardLayout cards = new CardLayout();
    private JPanel contenido = new JPanel(cards);

    public PanelCalculadoras() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        // Titulo
        JPanel enc = new JPanel(new BorderLayout(0, 6));
        enc.setOpaque(false);
        enc.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        enc.add(UI.labelTitulo("\uD83E\uDDEE  Calculadoras de Liquidacion"), BorderLayout.WEST);
        enc.add(UI.labelSub("Despido sin causa (LCT) y Accidente de Trabajo (ART)"), BorderLayout.SOUTH);
        add(enc, BorderLayout.NORTH);

        // Selector de calculadora
        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        selector.setOpaque(false);
        selector.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        ButtonGroup bg = new ButtonGroup();
        UI.BtnNav btnDespido = new UI.BtnNav("  \uD83D\uDCCB  Despido sin Causa");
        UI.BtnNav btnART     = new UI.BtnNav("  \uD83C\uDFE5  Accidente de Trabajo (ART)");
        bg.add(btnDespido); bg.add(btnART);

        selector.add(btnDespido);
        selector.add(Box.createHorizontalStrut(8));
        selector.add(btnART);

        contenido.setOpaque(false);
        contenido.add(panelDespido(), "despido");
        contenido.add(panelART(),     "art");

        btnDespido.addActionListener(e -> cards.show(contenido, "despido"));
        btnART.addActionListener(e     -> cards.show(contenido, "art"));
        btnDespido.setSelected(true);
        cards.show(contenido, "despido");

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(selector, BorderLayout.NORTH);
        centro.add(contenido, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    // ===================== CALCULADORA DESPIDO =====================
    private JPanel panelDespido() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);

        // Formulario
        UI.Campo cNombre  = new UI.Campo();
        UI.Campo cRazon   = new UI.Campo();
        UI.Campo cSueldo  = new UI.Campo();
        UI.Campo cIngreso = new UI.Campo(); cIngreso.setText("DD/MM/AAAA");
        UI.Campo cEgreso  = new UI.Campo(); cEgreso.setText("DD/MM/AAAA");
        UI.Combo cRegimen = new UI.Combo(new String[]{"Sin Reforma (LCT Art 245)", "Con Reforma Laboral"});
        JCheckBox chkPre  = new JCheckBox("Preaviso otorgado");
        chkPre.setFont(UI.F_CAMPO); chkPre.setOpaque(false); chkPre.setForeground(UI.AZUL_OSCURO);

        UI.Card formCard = new UI.Card();
        formCard.setLayout(new GridLayout(4, 3, 14, 12));
        formCard.add(UI.filaCampo("Nombre del Empleado", cNombre));
        formCard.add(UI.filaCampo("Razon Social Empleador", cRazon));
        formCard.add(UI.filaCampo("Sueldo Bruto ($)", cSueldo));
        formCard.add(UI.filaCampo("Fecha Ingreso (DD/MM/AAAA)", cIngreso));
        formCard.add(UI.filaCampo("Fecha Egreso (DD/MM/AAAA)", cEgreso));
        formCard.add(UI.filaCampo("Regimen", cRegimen));
        JPanel wrapCheck = new JPanel(new BorderLayout()); wrapCheck.setOpaque(false);
        wrapCheck.add(chkPre, BorderLayout.SOUTH);
        formCard.add(wrapCheck);
        formCard.add(new JPanel() {{ setOpaque(false); }});
        formCard.add(new JPanel() {{ setOpaque(false); }});

        // Botones
        UI.BtnPrimario btnCalc = new UI.BtnPrimario("  Calcular Liquidacion", UI.AZUL_ACENTO, new Color(31, 97, 141));
        UI.BtnIcono btnLimpiar = new UI.BtnIcono("Limpiar", new Color(120, 120, 140), new Color(80, 80, 100));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false); btnPanel.add(btnCalc); btnPanel.add(btnLimpiar);

        // Resultado
        JTextArea resultado = new JTextArea(14, 50);
        resultado.setEditable(false); resultado.setFont(UI.F_MONO);
        resultado.setBackground(UI.AZUL_OSCURO); resultado.setForeground(new Color(130, 220, 130));
        resultado.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        resultado.setText("  Complete los datos y presione Calcular...");

        btnCalc.addActionListener(e -> {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String nombre  = cNombre.getText().trim();
                double sueldo  = Double.parseDouble(cSueldo.getText().replace(",", "."));
                LocalDate ing  = LocalDate.parse(cIngreso.getText().trim(), fmt);
                LocalDate eg   = LocalDate.parse(cEgreso.getText().trim(), fmt);
                boolean conRef = ((String) cRegimen.getSelectedItem()).contains("Con Reforma");
                boolean pre    = chkPre.isSelected();
                String reg     = (String) cRegimen.getSelectedItem();

                String res = "  ==========================================\n"
                           + "       RECIBO DE LIQUIDACION FINAL\n"
                           + "  ==========================================\n"
                           + "  Empleador: " + cRazon.getText().trim() + "\n"
                           + "  ------------------------------------------\n"
                           + FabricaLiquidacion.calcularDespido(nombre, sueldo, ing, eg, pre, conRef)
                           + "  ==========================================\n";
                resultado.setText(res);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Revise los datos.\nFechas en formato DD/MM/AAAA y sueldo numerico.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLimpiar.addActionListener(e -> {
            cNombre.setText(""); cRazon.setText(""); cSueldo.setText("");
            cIngreso.setText("DD/MM/AAAA"); cEgreso.setText("DD/MM/AAAA");
            chkPre.setSelected(false);
            resultado.setText("  Complete los datos y presione Calcular...");
        });

        p.add(formCard, BorderLayout.NORTH);
        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(btnPanel, BorderLayout.NORTH);
        centro.add(UI.scrollResultado(resultado), BorderLayout.CENTER);
        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    // ===================== CALCULADORA ART =====================
    private JPanel panelART() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);

        UI.Campo cNombre = new UI.Campo();
        UI.Campo cIBM    = new UI.Campo();
        UI.Campo cEdad   = new UI.Campo();
        UI.Campo cIncap  = new UI.Campo();
        UI.Combo cTipo   = new UI.Combo(new String[]{
            "Accidente Laboral / Enfermedad Profesional",
            "Accidente In Itinere"
        });

        // Info formula
        JLabel infoForm = new JLabel("  Formula Ley 24.557:  53 x IBM x (%Incap / 100) x (65 / Edad)  —  se aplica el mayor entre formula y piso minimo vigente");
        infoForm.setFont(new Font("SansSerif", Font.ITALIC, 11));
        infoForm.setForeground(UI.GRIS_TEXTO);
        infoForm.setOpaque(true); infoForm.setBackground(new Color(240, 245, 255));
        infoForm.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UI.BORDE_CAMPO, 1, true),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)));

        UI.Card formCard = new UI.Card();
        formCard.setLayout(new BorderLayout(0, 12));
        JPanel grilla = new JPanel(new GridLayout(2, 3, 14, 12));
        grilla.setOpaque(false);
        grilla.add(UI.filaCampo("Nombre del Trabajador", cNombre));
        grilla.add(UI.filaCampo("Ingreso Base Mensual ($)", cIBM));
        grilla.add(UI.filaCampo("Edad al momento del accidente", cEdad));
        grilla.add(UI.filaCampo("% de Incapacidad  (ej: 15.5)", cIncap));
        grilla.add(UI.filaCampo("Tipo de Siniestro", cTipo));
        grilla.add(new JPanel() {{ setOpaque(false); }});
        formCard.add(grilla, BorderLayout.CENTER);
        formCard.add(infoForm, BorderLayout.SOUTH);

        // Botones
        UI.BtnPrimario btnCalc   = new UI.BtnPrimario("  Calcular ART", UI.NARANJA, new Color(160, 60, 0));
        UI.BtnIcono    btnLimpiar = new UI.BtnIcono("Limpiar", new Color(120, 120, 140), new Color(80, 80, 100));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false); btnPanel.add(btnCalc); btnPanel.add(btnLimpiar);

        // Resultado
        JTextArea resultado = new JTextArea(14, 50);
        resultado.setEditable(false); resultado.setFont(UI.F_MONO);
        resultado.setBackground(UI.AZUL_OSCURO); resultado.setForeground(new Color(255, 180, 100));
        resultado.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        resultado.setText("  Complete los datos y presione Calcular...");

        btnCalc.addActionListener(e -> {
            try {
                String nombre  = cNombre.getText().trim();
                if (nombre.isEmpty()) nombre = "Trabajador";
                double ibm     = Double.parseDouble(cIBM.getText().replace(",", "."));
                int edad       = Integer.parseInt(cEdad.getText().trim());
                double pct     = Double.parseDouble(cIncap.getText().replace(",", "."));
                boolean esLab  = cTipo.getSelectedIndex() == 0;

                if (edad <= 0 || edad >= 65) {
                    JOptionPane.showMessageDialog(this, "La edad debe estar entre 1 y 64.", "Error", JOptionPane.ERROR_MESSAGE); return;
                }
                if (pct <= 0 || pct > 66) {
                    JOptionPane.showMessageDialog(this, "El % de incapacidad debe ser entre 0.1 y 66.", "Error", JOptionPane.ERROR_MESSAGE); return;
                }

                String res = "  ==========================================\n"
                           + "     LIQUIDACION ART — LEY 24.557\n"
                           + "  ==========================================\n"
                           + FabricaLiquidacion.calcularART(nombre, ibm, edad, pct, esLab)
                           + "  ==========================================\n";
                resultado.setText(res);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Revise los datos numericos (IBM, edad, % incapacidad).",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLimpiar.addActionListener(e -> {
            cNombre.setText(""); cIBM.setText(""); cEdad.setText(""); cIncap.setText("");
            resultado.setText("  Complete los datos y presione Calcular...");
        });

        p.add(formCard, BorderLayout.NORTH);
        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(btnPanel, BorderLayout.NORTH);
        centro.add(UI.scrollResultado(resultado), BorderLayout.CENTER);
        p.add(centro, BorderLayout.CENTER);
        return p;
    }
}
