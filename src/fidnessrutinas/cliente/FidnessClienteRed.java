package fidnessrutinas.cliente;

import fidnessrutinas.enums.CategoriaEjercicio;
import fidnessrutinas.excepciones.DatoInvalidoException;
import fidnessrutinas.modelo.Ejercicio;
import fidnessrutinas.modelo.Rutina;
import fidnessrutinas.modelo.Usuario;
import fidnessrutinas.servidor.MainServer;
import fidnessrutinas.servicio.Operacion;
import fidnessrutinas.servicio.Respuesta;
import fidnessrutinas.servicio.Solicitud;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class FidnessClienteRed {

    private static final String HOST = "127.0.0.1";

    public Usuario login(String usuario, String contrasena) throws DatoInvalidoException {
        Solicitud solicitud = new Solicitud(Operacion.LOGIN)
                .agregarDato("usuario", usuario)
                .agregarDato("contrasena", contrasena);
        return (Usuario) enviar(solicitud).getDatos();
    }

    @SuppressWarnings("unchecked")
    public List<Ejercicio> listarEjercicios() throws DatoInvalidoException {
        return (List<Ejercicio>) enviar(new Solicitud(Operacion.LISTAR_EJERCICIOS)).getDatos();
    }

    @SuppressWarnings("unchecked")
    public List<Ejercicio> filtrarEjercicios(CategoriaEjercicio categoria) throws DatoInvalidoException {
        Solicitud solicitud = new Solicitud(Operacion.FILTRAR_EJERCICIOS)
                .agregarDato("categoria", categoria);
        return (List<Ejercicio>) enviar(solicitud).getDatos();
    }

    public Rutina agregarARutina(String usuario, int idEjercicio) throws DatoInvalidoException {
        Solicitud solicitud = new Solicitud(Operacion.AGREGAR_A_RUTINA)
                .agregarDato("usuario", usuario)
                .agregarDato("idEjercicio", idEjercicio);
        return (Rutina) enviar(solicitud).getDatos();
    }

    public Rutina obtenerRutina(String usuario) throws DatoInvalidoException {
        Solicitud solicitud = new Solicitud(Operacion.OBTENER_RUTINA)
                .agregarDato("usuario", usuario);
        return (Rutina) enviar(solicitud).getDatos();
    }

    private Respuesta enviar(Solicitud solicitud) throws DatoInvalidoException {
        try (Socket socket = new Socket(HOST, MainServer.PUERTO);
                ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {

            salida.flush();
            salida.writeObject(solicitud);
            salida.flush();

            Respuesta respuesta = (Respuesta) entrada.readObject();
            if (!respuesta.isExitosa()) {
                throw new DatoInvalidoException(respuesta.getMensaje());
            }
            return respuesta;
        } catch (IOException | ClassNotFoundException ex) {
            throw new DatoInvalidoException("No fue posible comunicarse con el servidor Fidness.");
        }
    }
}
