package fidnessrutinas.vista;

import fidnessrutinas.cliente.FidnessClienteRed;
import fidnessrutinas.enums.CategoriaEjercicio;
import fidnessrutinas.enums.NivelDificultad;
import fidnessrutinas.excepciones.DatoInvalidoException;
import fidnessrutinas.modelo.Ejercicio;
import fidnessrutinas.modelo.EjercicioCardio;
import fidnessrutinas.modelo.EjercicioFuerza;
import fidnessrutinas.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

public class RegistroEjercicioDialog extends JDialog {

    private final Usuario administrador;
    private final FidnessClienteRed clienteRed;
    private final JTextField txtNombre;
    private final JComboBox<CategoriaEjercicio> cboCategoria;
    private final JComboBox<NivelDificultad> cboNivel;
    private final JComboBox<String> cboTipo;
    private final JTextArea txtDescripcion;
    private final JLabel lblValor1;
    private final JLabel lblValor2;
    private final JSpinner spnValor1;
    private final JSpinner spnValor2;
    private boolean ejercicioRegistrado;

    public RegistroEjercicioDialog(Frame propietario, Usuario administrador,
            FidnessClienteRed clienteRed) {
        super(propietario, "Registrar ejercicio", true);
        this.administrador = administrador;
        this.clienteRed = clienteRed;
        this.txtNombre = new JTextField(24);
        this.cboCategoria = new JComboBox<>(CategoriaEjercicio.values());
        this.cboNivel = new JComboBox<>(NivelDificultad.values());
        this.cboTipo = new JComboBox<>(new String[]{"FUERZA", "CARDIO"});
        this.txtDescripcion = new JTextArea(4, 24);
        this.lblValor1 = new JLabel("Series");
        this.lblValor2 = new JLabel("Repeticiones");
        this.spnValor1 = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        this.spnValor2 = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        this.ejercicioRegistrado = false;
        this.cboCategoria.setSelectedItem(CategoriaEjercicio.PIERNA);

        configurarVentana();
    }

    private void configurarVentana() {
        setLayout(new BorderLayout(8, 8));
        add(crearFormulario(), BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);
        cboTipo.addActionListener(new TipoListener());
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        pack();
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        agregarFila(panel, gbc, 0, "Nombre", txtNombre);
        agregarFila(panel, gbc, 1, "Categoria", cboCategoria);
        agregarFila(panel, gbc, 2, "Dificultad", cboNivel);
        agregarFila(panel, gbc, 3, "Tipo", cboTipo);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Descripcion"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(txtDescripcion), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(lblValor1, gbc);
        gbc.gridx = 1;
        panel.add(spnValor1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(lblValor2, gbc);
        gbc.gridx = 1;
        panel.add(spnValor2, gbc);
        return panel;
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila,
            String etiqueta, java.awt.Component componente) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panel.add(componente, gbc);
    }

    private JPanel crearBotones() {
        JPanel panel = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new GuardarListener());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(new CancelarListener());
        panel.add(btnGuardar);
        panel.add(btnCancelar);
        return panel;
    }

    private void actualizarCamposTipo() {
        boolean fuerza = "FUERZA".equals(cboTipo.getSelectedItem());
        if (fuerza && cboCategoria.getSelectedItem() == CategoriaEjercicio.CARDIO) {
            cboCategoria.setSelectedItem(CategoriaEjercicio.PIERNA);
        } else if (!fuerza) {
            cboCategoria.setSelectedItem(CategoriaEjercicio.CARDIO);
        }
        cboCategoria.setEnabled(fuerza);
        lblValor1.setText(fuerza ? "Series" : "Minutos");
        lblValor2.setVisible(fuerza);
        spnValor2.setVisible(fuerza);
        pack();
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        if (nombre.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre y descripcion son obligatorios.",
                    "FidnessRutinas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CategoriaEjercicio categoria =
                (CategoriaEjercicio) cboCategoria.getSelectedItem();
        NivelDificultad nivel =
                (NivelDificultad) cboNivel.getSelectedItem();
        int valor1 = (Integer) spnValor1.getValue();
        int valor2 = (Integer) spnValor2.getValue();

        Ejercicio ejercicio;
        if ("FUERZA".equals(cboTipo.getSelectedItem())) {
            ejercicio = new EjercicioFuerza(
                    0, nombre, categoria, nivel, descripcion, valor1, valor2);
        } else {
            ejercicio = new EjercicioCardio(
                    0, nombre, categoria, nivel, descripcion, valor1);
        }

        try {
            clienteRed.registrarEjercicio(
                    administrador.getNombreUsuario(), ejercicio);
            ejercicioRegistrado = true;
            JOptionPane.showMessageDialog(this,
                    "Ejercicio registrado correctamente.");
            dispose();
        } catch (DatoInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo registrar", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isEjercicioRegistrado() {
        return ejercicioRegistrado;
    }

    private class TipoListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            actualizarCamposTipo();
        }
    }

    private class GuardarListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            guardar();
        }
    }

    private class CancelarListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            dispose();
        }
    }
}
