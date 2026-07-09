package fidnessrutinas.servicio;

import fidnessrutinas.enums.CategoriaEjercicio;
import fidnessrutinas.enums.NivelDificultad;
import fidnessrutinas.excepciones.DatoInvalidoException;
import fidnessrutinas.modelo.Ejercicio;
import fidnessrutinas.modelo.EjercicioCardio;
import fidnessrutinas.modelo.EjercicioFuerza;
import fidnessrutinas.modelo.Rutina;
import fidnessrutinas.modelo.Usuario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionRutinas {

    private final List<Ejercicio> ejerciciosDisponibles;
    private final Map<String, Usuario> usuarios;
    private final Map<String, Rutina> rutinasPorUsuario;

    public GestionRutinas() {
        ejerciciosDisponibles = new ArrayList<>();
        usuarios = new HashMap<>();
        rutinasPorUsuario = new HashMap<>();
        cargarDatosIniciales();
    }

    public synchronized Usuario validarLogin(String nombreUsuario, String contrasena)
            throws DatoInvalidoException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()
                || contrasena == null || contrasena.trim().isEmpty()) {
            throw new DatoInvalidoException("Digite usuario y contrasena.");
        }

        Usuario usuario = usuarios.get(nombreUsuario.trim().toLowerCase());
        if (usuario == null || !usuario.getContrasena().equals(contrasena)) {
            throw new DatoInvalidoException("Credenciales incorrectas.");
        }
        return usuario;
    }

    public synchronized List<Ejercicio> listarEjercicios() {
        return new ArrayList<>(ejerciciosDisponibles);
    }

    public synchronized List<Ejercicio> buscarPorCategoria(CategoriaEjercicio categoria) {
        if (categoria == null) {
            return listarEjercicios();
        }

        List<Ejercicio> filtrados = new ArrayList<>();
        for (Ejercicio ejercicio : ejerciciosDisponibles) {
            if (ejercicio.getCategoria() == categoria) {
                filtrados.add(ejercicio);
            }
        }
        return filtrados;
    }

    public synchronized Rutina agregarEjercicioARutina(String nombreUsuario, int idEjercicio)
            throws DatoInvalidoException {
        Usuario usuario = usuarios.get(nombreUsuario.toLowerCase());
        if (usuario == null) {
            throw new DatoInvalidoException("Usuario no encontrado.");
        }

        Ejercicio ejercicio = buscarEjercicioPorId(idEjercicio);
        if (ejercicio == null) {
            throw new DatoInvalidoException("Ejercicio no encontrado.");
        }

        Rutina rutina = rutinasPorUsuario.get(nombreUsuario.toLowerCase());
        if (rutina == null) {
            rutina = new Rutina(1, "Rutina personal", usuario);
            rutinasPorUsuario.put(nombreUsuario.toLowerCase(), rutina);
        }

        rutina.agregarEjercicio(ejercicio);
        return rutina;
    }

    public synchronized Rutina obtenerRutina(String nombreUsuario) throws DatoInvalidoException {
        Usuario usuario = usuarios.get(nombreUsuario.toLowerCase());
        if (usuario == null) {
            throw new DatoInvalidoException("Usuario no encontrado.");
        }

        Rutina rutina = rutinasPorUsuario.get(nombreUsuario.toLowerCase());
        if (rutina == null) {
            rutina = new Rutina(1, "Rutina personal", usuario);
            rutinasPorUsuario.put(nombreUsuario.toLowerCase(), rutina);
        }
        return rutina;
    }

    public synchronized void registrarEjercicio(Ejercicio ejercicio) throws DatoInvalidoException {
        if (ejercicio == null) {
            throw new DatoInvalidoException("El ejercicio no puede ser nulo.");
        }
        ejerciciosDisponibles.add(ejercicio);
    }

    private Ejercicio buscarEjercicioPorId(int idEjercicio) {
        for (Ejercicio ejercicio : ejerciciosDisponibles) {
            if (ejercicio.getId() == idEjercicio) {
                return ejercicio;
            }
        }
        return null;
    }

    private void cargarDatosIniciales() {
        usuarios.put("heiner", new Usuario(1, "Heiner Borbon", "heiner", "1234"));
        usuarios.put("admin", new Usuario(2, "Administrador Fidness", "admin", "admin"));

        ejerciciosDisponibles.add(new EjercicioFuerza(1, "Sentadilla",
                CategoriaEjercicio.PIERNA, NivelDificultad.INTERMEDIO,
                "Mantenga la espalda recta, baje controladamente y empuje con los talones.",
                4, 12));
        ejerciciosDisponibles.add(new EjercicioFuerza(2, "Press de banca",
                CategoriaEjercicio.PECHO, NivelDificultad.INTERMEDIO,
                "Acuestese en el banco, baje la barra al pecho y suba sin bloquear los codos.",
                4, 10));
        ejerciciosDisponibles.add(new EjercicioFuerza(3, "Curl de biceps",
                CategoriaEjercicio.BRAZO, NivelDificultad.PRINCIPIANTE,
                "Mantenga los codos cerca del torso y suba la mancuerna sin balancear el cuerpo.",
                3, 12));
        ejerciciosDisponibles.add(new EjercicioFuerza(4, "Remo con mancuerna",
                CategoriaEjercicio.ESPALDA, NivelDificultad.INTERMEDIO,
                "Apoye una rodilla en el banco y lleve la mancuerna hacia la cintura.",
                4, 10));
        ejerciciosDisponibles.add(new EjercicioFuerza(5, "Plancha abdominal",
                CategoriaEjercicio.ABDOMEN, NivelDificultad.PRINCIPIANTE,
                "Mantenga el abdomen contraido y el cuerpo alineado de pies a cabeza.",
                3, 30));
        ejerciciosDisponibles.add(new EjercicioCardio(6, "Caminadora",
                CategoriaEjercicio.CARDIO, NivelDificultad.PRINCIPIANTE,
                "Caminar o trotar con ritmo constante, cuidando la respiracion.",
                20));
        ejerciciosDisponibles.add(new EjercicioCardio(7, "Bicicleta estatica",
                CategoriaEjercicio.CARDIO, NivelDificultad.INTERMEDIO,
                "Ajuste la resistencia y mantenga cadencia estable durante la sesion.",
                25));
    }
}
