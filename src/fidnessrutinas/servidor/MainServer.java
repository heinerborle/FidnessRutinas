package fidnessrutinas.servidor;

import fidnessrutinas.servicio.GestionRutinas;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    public static final int PUERTO = 5050;
    private static final GestionRutinas GESTION_RUTINAS = new GestionRutinas();
    private static boolean iniciado = false;

    public static synchronized void iniciarEnSegundoPlano() {
        if (iniciado) {
            return;
        }
        Thread hiloServidor = new Thread(new Runnable() {
            @Override
            public void run() {
                iniciar();
            }
        }, "fidness-servidor");
        hiloServidor.setDaemon(true);
        hiloServidor.start();
        iniciado = true;
    }

    public static void main(String[] args) {
        iniciar();
    }

    private static void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor Fidness escuchando en puerto " + PUERTO);
            while (true) {
                Socket socketCliente = serverSocket.accept();
                Thread hiloCliente = new Thread(
                        new ClientHandler(socketCliente, GESTION_RUTINAS),
                        "fidness-cliente-" + socketCliente.getPort());
                hiloCliente.start();
            }
        } catch (BindException ex) {
            System.out.println("El servidor Fidness ya estaba activo en puerto " + PUERTO);
        } catch (IOException ex) {
            System.out.println("No se pudo iniciar el servidor: " + ex.getMessage());
        }
    }
}
