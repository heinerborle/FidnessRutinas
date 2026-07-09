package fidnessrutinas;

import fidnessrutinas.servidor.MainServer;
import fidnessrutinas.vista.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        MainServer.iniciarEnSegundoPlano();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
