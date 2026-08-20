package fidnessrutinas.servidor;

import fidnessrutinas.enums.CategoriaEjercicio;
import fidnessrutinas.excepciones.DatoInvalidoException;
import fidnessrutinas.servicio.GestionRutinas;
import fidnessrutinas.servicio.Operacion;
import fidnessrutinas.servicio.Respuesta;
import fidnessrutinas.servicio.Solicitud;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final GestionRutinas gestionRutinas;

    public ClientHandler(Socket socket, GestionRutinas gestionRutinas) {
        this.socket = socket;
        this.gestionRutinas = gestionRutinas;
    }

    @Override
    public void run() {
        try (Socket cliente = socket;
                ObjectOutputStream salida = new ObjectOutputStream(cliente.getOutputStream());
                ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream())) {

            salida.flush();
            Solicitud solicitud = (Solicitud) entrada.readObject();
            Respuesta respuesta = procesarSolicitud(solicitud);
            salida.writeObject(respuesta);
            salida.flush();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error atendiendo cliente: " + ex.getMessage());
        }
    }

    private Respuesta procesarSolicitud(Solicitud solicitud) {
        try {
            Operacion operacion = solicitud.getOperacion();
            switch (operacion) {
                case LOGIN:
                    return Respuesta.ok("Login correcto",
                            gestionRutinas.validarLogin(
                                    (String) solicitud.obtenerDato("usuario"),
                                    (String) solicitud.obtenerDato("contrasena")));
                case LISTAR_EJERCICIOS:
                    return Respuesta.ok("Ejercicios encontrados", gestionRutinas.listarEjercicios());
                case FILTRAR_EJERCICIOS:
                    return Respuesta.ok("Ejercicios filtrados",
                            gestionRutinas.buscarPorCategoria(
                                    (CategoriaEjercicio) solicitud.obtenerDato("categoria")));
                case AGREGAR_A_RUTINA:
                    return Respuesta.ok("Ejercicio agregado",
                            gestionRutinas.agregarEjercicioARutina(
                                    (String) solicitud.obtenerDato("usuario"),
                                    (Integer) solicitud.obtenerDato("idEjercicio")));
                case ELIMINAR_DE_RUTINA:
                    return Respuesta.ok("Ejercicio eliminado",
                            gestionRutinas.eliminarEjercicioDeRutina(
                                    (String) solicitud.obtenerDato("usuario"),
                                    (Integer) solicitud.obtenerDato("idEjercicio")));
                case OBTENER_RUTINA:
                    return Respuesta.ok("Rutina actual",
                            gestionRutinas.obtenerRutina((String) solicitud.obtenerDato("usuario")));
                case REGISTRAR_EJERCICIO:
                    gestionRutinas.registrarEjercicio(
                            (String) solicitud.obtenerDato("administrador"),
                            (fidnessrutinas.modelo.Ejercicio) solicitud.obtenerDato("ejercicio"));
                    return Respuesta.ok("Ejercicio registrado", null);
                default:
                    return Respuesta.error("Operacion no soportada.");
            }
        } catch (DatoInvalidoException | ClassCastException ex) {
            return Respuesta.error(ex.getMessage());
        }
    }
}
