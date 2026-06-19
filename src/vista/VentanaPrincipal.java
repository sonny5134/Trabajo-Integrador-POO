package vista;

import modelo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VentanaPrincipal extends JFrame {
    private JTextField txtRazonSocial, txtNombre, txtSueldo, txtIngreso, txtEgreso;
    private JCheckBox chkPreavisoOtorgado;
    private JComboBox<String> cmbRegimen;
    private JTextArea txtResultado;
    private Empresa empresa;

    public VentanaPrincipal() {
        empresa = new Empresa(""); 

        setTitle("Sistema de Gestión de Indemnizaciones");
        setSize(500, 550); // Aumentamos un poquito el alto para acomodar el nuevo campo
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());

        // Panel superior con el formulario (ahora configurado para 7 filas)
        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelFormulario.add(new JLabel("Razón Social de la Empresa:"));
        txtRazonSocial = new JTextField();
        panelFormulario.add(txtRazonSocial);

        panelFormulario.add(new JLabel("Nombre del Empleado:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Sueldo Bruto ($):"));
        txtSueldo = new JTextField();
        panelFormulario.add(txtSueldo);

        panelFormulario.add(new JLabel("Fecha Ingreso (DD/MM/AAAA):"));
        txtIngreso = new JTextField();
        panelFormulario.add(txtIngreso);

        panelFormulario.add(new JLabel("Fecha Egreso (DD/MM/AAAA):"));
        txtEgreso = new JTextField();
        panelFormulario.add(txtEgreso);

        panelFormulario.add(new JLabel("Régimen Aplicable:"));
        cmbRegimen = new JComboBox<>(new String[]{"Sin Reforma (LCT Art 245)", "Con Reforma Laboral"});
        panelFormulario.add(cmbRegimen);

        panelFormulario.add(new JLabel("¿Se otorgó preaviso?"));
        chkPreavisoOtorgado = new JCheckBox("Sí");
        panelFormulario.add(chkPreavisoOtorgado);

        add(panelFormulario, BorderLayout.NORTH);

        // Botón Calcular en el centro
        JButton btnCalcular = new JButton("Calcular Liquidación");
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCalcular);
        add(panelBoton, BorderLayout.CENTER);

        // Área de texto para mostrar el recibo abajo
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(txtResultado);
        scroll.setPreferredSize(new Dimension(480, 220));
        add(scroll, BorderLayout.SOUTH);

        // Acción del botón
        btnCalcular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calcularYMostrar();
            }
        });
    }

    private void calcularYMostrar() {
        try {
            // 1. Leer la razón social y actualizar el objeto Empresa
            String razonSocialInput = txtRazonSocial.getText();
            if (razonSocialInput.trim().isEmpty()) {
                razonSocialInput = "Empresa Sin Nombre S.A.";
            }
            empresa.setRazonSocial("R.S. " + razonSocialInput);

            // 2. Leer el resto de los datos del formulario
            String nombre = txtNombre.getText();
            double sueldo = Double.parseDouble(txtSueldo.getText());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ingreso = LocalDate.parse(txtIngreso.getText(), formatter);
            LocalDate egreso = LocalDate.parse(txtEgreso.getText(), formatter);
            boolean preavisoOtorgado = chkPreavisoOtorgado.isSelected();

            // 3. Instanciar los objetos necesarios para el cálculo
            Empleado emp = new Empleado(nombre, sueldo, ingreso, egreso, preavisoOtorgado);
            empresa.altaEmpleado(emp); 

            Preaviso pre = new Preaviso(emp);
            VacacionesProporcionales vac = new VacacionesProporcionales(emp, emp.getAntiguedad());
            SACProporcional sac = new SACProporcional(emp);

            Indemnizacion indemnizacion;
            String regimenSeleccionado = (String) cmbRegimen.getSelectedItem();

            // Aplicación de Polimorfismo según selección en la GUI
            if (regimenSeleccionado.contains("Sin Reforma")) {
                indemnizacion = new IndemnizacionSinReforma(emp, pre, vac, sac);
            } else {
                indemnizacion = new IndemnizacionConReforma(emp, pre, vac, sac);
            }

            // 4. Generar Reporte pasando el objeto empresa dinámico
            ReciboLiquidacion recibo = new ReciboLiquidacion(indemnizacion, regimenSeleccionado, empresa);
            txtResultado.setText(recibo.generarReporte());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en los datos ingresados. Revise las fechas (DD/MM/AAAA) y el sueldo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}