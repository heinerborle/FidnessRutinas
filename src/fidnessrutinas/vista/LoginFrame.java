package fidnessrutinas.vista;

import fidnessrutinas.cliente.FidnessClienteRed;
import fidnessrutinas.excepciones.DatoInvalidoException;
import fidnessrutinas.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame {

    private final JTextField txtUsuario;
    private final JPasswordField txtContrasena;
    private final FidnessClienteRed clienteRed;

    public LoginFrame() {
        clienteRed = new FidnessClienteRed();
        txtUsuario = new JTextField("heiner", 18);
        txtContrasena = new JPasswordField("1234", 18);

        setTitle("FidnessRutinas - Ingreso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        add(crearFormulario(), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Usuario"), gbc);
        gbc.gridx = 1;
        panel.add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Contrasena"), gbc);
        gbc.gridx = 1;
        panel.add(txtContrasena, gbc);

        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.addActionListener(event -> ingresar());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(btnIngresar, gbc);

        return panel;
    }

    private void ingresar() {
        try {
            String usuario = txtUsuario.getText();
            String contrasena = new String(txtContrasena.getPassword());
            Usuario usuarioValidado = clienteRed.login(usuario, contrasena);
            new PrincipalFrame(usuarioValidado).setVisible(true);
            dispose();
        } catch (DatoInvalidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo ingresar", JOptionPane.WARNING_MESSAGE);
        }
    }
}
