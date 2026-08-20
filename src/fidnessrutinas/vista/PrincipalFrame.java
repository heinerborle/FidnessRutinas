package fidnessrutinas.vista;

import fidnessrutinas.cliente.FidnessClienteRed;
import fidnessrutinas.enums.CategoriaEjercicio;
import fidnessrutinas.excepciones.DatoInvalidoException;
import fidnessrutinas.modelo.Ejercicio;
import fidnessrutinas.modelo.Rutina;
import fidnessrutinas.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class PrincipalFrame extends JFrame {

    private final Usuario usuario;
    private final FidnessClienteRed clienteRed;
    private final DefaultTableModel modeloTabla;
    private final JTable tablaEjercicios;
    private final JTextArea txtDetalle;
    private final JTextArea txtRutina;
    private final JComboBox<String> cboCategoria;
    private final DefaultListModel<Ejercicio> modeloRutina;
    private final JList<Ejercicio> listaRutina;
    private List<Ejercicio> ejerciciosActuales;

    public PrincipalFrame(Usuario usuario) {
        this.usuario = usuario;
        this.clienteRed = new FidnessClienteRed();
        this.ejerciciosActuales = new ArrayList<>();
        this.modeloTabla = new DefaultTableModel(
                new String[]{"Id", "Nombre", "Categoria", "Dificultad", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tablaEjercicios = new JTable(modeloTabla);
        this.txtDetalle = new JTextArea();
        this.txtRutina = new JTextArea();
        this.cboCategoria = new JComboBox<>();
        this.modeloRutina = new DefaultListModel<>();
        this.listaRutina = new JList<>(modeloRutina);

        configurarVentana();
        cargarEjercicios();
        cargarRutina();
    }

    private void configurarVentana() {
        setTitle("FidnessRutinas - " + usuario.getNombreCompleto());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        setSize(1000, 620);
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Categoria"));
        cboCategoria.addItem("TODAS");
        for (CategoriaEjercicio categoria : CategoriaEjercicio.values()) {
            cboCategoria.addItem(categoria.name());
        }

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(new FiltrarListener());
        JButton btnTodos = new JButton("Ver todos");
        btnTodos.addActionListener(new VerTodosListener());
        JButton btnAgregar = new JButton("Agregar a rutina");
        btnAgregar.addActionListener(new AgregarRutinaListener());

        panel.add(cboCategoria);
        panel.add(btnFiltrar);
        panel.add(btnTodos);
        panel.add(btnAgregar);

        if (usuario.isAdministrador()) {
            JButton btnRegistrar = new JButton("Registrar ejercicio");
            btnRegistrar.addActionListener(new RegistrarEjercicioListener());
            panel.add(btnRegistrar);
        }
        return panel;
    }

    private JSplitPane crearPanelCentral() {
        tablaEjercicios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEjercicios.getSelectionModel().addListSelectionListener(
                new SeleccionEjercicioListener());
        JScrollPane scrollTabla = new JScrollPane(tablaEjercicios);
        scrollTabla.setPreferredSize(new Dimension(580, 460));

        txtDetalle.setEditable(false);
        txtDetalle.setLineWrap(true);
        txtDetalle.setWrapStyleWord(true);
        txtRutina.setEditable(false);
        txtRutina.setLineWrap(true);
        txtRutina.setWrapStyleWord(true);
        txtRutina.setRows(4);

        JSplitPane panelDerecho = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(txtDetalle), crearPanelRutina());
        panelDerecho.setResizeWeight(0.45);
        panelDerecho.setPreferredSize(new Dimension(400, 460));

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, scrollTabla, panelDerecho);
        split.setResizeWeight(0.60);
        return split;
    }

    private JPanel crearPanelRutina() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Ejercicios de la rutina"), BorderLayout.NORTH);
        listaRutina.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(listaRutina), BorderLayout.CENTER);

        JButton btnEliminar = new JButton("Eliminar seleccionado");
        btnEliminar.addActionListener(new EliminarRutinaListener());
        JButton btnExportar = new JButton("Exportar rutina");
        btnExportar.addActionListener(new ExportarRutinaListener());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnEliminar);
        acciones.add(btnExportar);

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.add(acciones, BorderLayout.NORTH);
        inferior.add(new JScrollPane(txtRutina), BorderLayout.CENTER);
        panel.add(inferior, BorderLayout.SOUTH);
        return panel;
    }

    private void cargarEjercicios() {
        try {
            ejerciciosActuales = clienteRed.listarEjercicios();
            llenarTabla();
        } catch (DatoInvalidoException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void filtrar() {
        try {
            String seleccion = (String) cboCategoria.getSelectedItem();
            if ("TODAS".equals(seleccion)) {
                cargarEjercicios();
                return;
            }
            ejerciciosActuales = clienteRed.filtrarEjercicios(
                    CategoriaEjercicio.valueOf(seleccion));
            llenarTabla();
        } catch (DatoInvalidoException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void llenarTabla() {
        modeloTabla.setRowCount(0);
        for (Ejercicio ejercicio : ejerciciosActuales) {
            modeloTabla.addRow(new Object[]{
                ejercicio.getId(),
                ejercicio.getNombre(),
                ejercicio.getCategoria(),
                ejercicio.getNivelDificultad(),
                ejercicio.getClass().getSimpleName()
            });
        }
        txtDetalle.setText("");
    }

    private void mostrarDetalleSeleccionado() {
        int fila = tablaEjercicios.getSelectedRow();
        if (fila >= 0 && fila < ejerciciosActuales.size()) {
            Ejercicio ejercicio = ejerciciosActuales.get(fila);
            txtDetalle.setText(ejercicio.mostrarDetalle());
            txtDetalle.setCaretPosition(0);
        }
    }

    private void agregarSeleccionARutina() {
        int fila = tablaEjercicios.getSelectedRow();
        if (fila < 0 || fila >= ejerciciosActuales.size()) {
            mostrarError("Seleccione un ejercicio de la tabla.");
            return;
        }

        try {
            Ejercicio ejercicio = ejerciciosActuales.get(fila);
            Rutina rutina = clienteRed.agregarARutina(
                    usuario.getNombreUsuario(), ejercicio.getId());
            mostrarRutina(rutina);
        } catch (DatoInvalidoException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarSeleccionDeRutina() {
        Ejercicio ejercicio = listaRutina.getSelectedValue();
        if (ejercicio == null) {
            mostrarError("Seleccione un ejercicio de la rutina.");
            return;
        }
        try {
            Rutina rutina = clienteRed.eliminarDeRutina(
                    usuario.getNombreUsuario(), ejercicio.getId());
            mostrarRutina(rutina);
        } catch (DatoInvalidoException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cargarRutina() {
        try {
            mostrarRutina(clienteRed.obtenerRutina(usuario.getNombreUsuario()));
        } catch (DatoInvalidoException ex) {
            txtRutina.setText("No se pudo cargar la rutina.");
        }
    }

    private void mostrarRutina(Rutina rutina) {
        modeloRutina.clear();
        for (Ejercicio ejercicio : rutina.getEjercicios()) {
            modeloRutina.addElement(ejercicio);
        }
        txtRutina.setText(rutina.generarResumen());
        txtRutina.setCaretPosition(0);
    }

    private void abrirRegistroEjercicio() {
        RegistroEjercicioDialog dialogo = new RegistroEjercicioDialog(
                this, usuario, clienteRed);
        dialogo.setVisible(true);
        if (dialogo.isEjercicioRegistrado()) {
            cargarEjercicios();
        }
    }

    private void exportarRutina() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Exportar rutina");
        selector.setSelectedFile(new File("rutina-fidness.txt"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".txt")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".txt");
        }
        try {
            Files.writeString(archivo.toPath(), txtRutina.getText(),
                    StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(this,
                    "Rutina exportada en:\n" + archivo.getAbsolutePath(),
                    "FidnessRutinas", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            mostrarError("No fue posible exportar la rutina.");
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "FidnessRutinas",
                JOptionPane.WARNING_MESSAGE);
    }

    private class FiltrarListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            filtrar();
        }
    }

    private class VerTodosListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            cboCategoria.setSelectedItem("TODAS");
            cargarEjercicios();
        }
    }

    private class AgregarRutinaListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            agregarSeleccionARutina();
        }
    }

    private class EliminarRutinaListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            eliminarSeleccionDeRutina();
        }
    }

    private class RegistrarEjercicioListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            abrirRegistroEjercicio();
        }
    }

    private class ExportarRutinaListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            exportarRutina();
        }
    }

    private class SeleccionEjercicioListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (!event.getValueIsAdjusting()) {
                mostrarDetalleSeleccionado();
            }
        }
    }
}
