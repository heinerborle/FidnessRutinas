package fidnessrutinas.servicio;

import fidnessrutinas.datos.ConexionBD;
import fidnessrutinas.datos.InicializadorBD;
import fidnessrutinas.enums.CategoriaEjercicio;
import fidnessrutinas.enums.NivelDificultad;
import fidnessrutinas.excepciones.DatoInvalidoException;
import fidnessrutinas.modelo.Ejercicio;
import fidnessrutinas.modelo.EjercicioCardio;
import fidnessrutinas.modelo.EjercicioFuerza;
import fidnessrutinas.modelo.Rutina;
import fidnessrutinas.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class GestionRutinas {

    public GestionRutinas() {
        try {
            InicializadorBD.inicializar();
            System.out.println("Base de datos FidnessRutinas preparada.");
        } catch (SQLException | IllegalStateException ex) {
            throw new IllegalStateException(
                    "No se pudo preparar la base de datos FidnessRutinas.", ex);
        }
    }

    public synchronized Usuario validarLogin(String nombreUsuario, String contrasena)
            throws DatoInvalidoException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()
                || contrasena == null || contrasena.trim().isEmpty()) {
            throw new DatoInvalidoException("Digite usuario y contrasena.");
        }

        String sql = "SELECT id, nombre_completo, nombre_usuario, administrador "
                + "FROM usuario WHERE LOWER(nombre_usuario) = ? AND contrasena = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nombreUsuario.trim().toLowerCase());
            sentencia.setString(2, contrasena);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    throw new DatoInvalidoException("Credenciales incorrectas.");
                }
                return new Usuario(
                        resultado.getInt("id"),
                        resultado.getString("nombre_completo"),
                        resultado.getString("nombre_usuario"),
                        "",
                        resultado.getBoolean("administrador"));
            }
        } catch (SQLException ex) {
            throw errorBaseDatos("validar el ingreso", ex);
        }
    }

    public synchronized List<Ejercicio> listarEjercicios() throws DatoInvalidoException {
        String sql = "SELECT * FROM ejercicio ORDER BY id";
        try (Connection conexion = ConexionBD.obtenerConexion();
                Statement sentencia = conexion.createStatement();
                ResultSet resultado = sentencia.executeQuery(sql)) {
            return leerEjercicios(resultado);
        } catch (SQLException ex) {
            throw errorBaseDatos("consultar los ejercicios", ex);
        }
    }

    public synchronized List<Ejercicio> buscarPorCategoria(CategoriaEjercicio categoria)
            throws DatoInvalidoException {
        if (categoria == null) {
            return listarEjercicios();
        }
        String sql = "SELECT * FROM ejercicio WHERE categoria = ? ORDER BY id";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, categoria.name());
            try (ResultSet resultado = sentencia.executeQuery()) {
                return leerEjercicios(resultado);
            }
        } catch (SQLException ex) {
            throw errorBaseDatos("filtrar los ejercicios", ex);
        }
    }

    public synchronized Rutina agregarEjercicioARutina(String nombreUsuario, int idEjercicio)
            throws DatoInvalidoException {
        Usuario usuario = buscarUsuario(nombreUsuario);
        buscarEjercicioPorId(idEjercicio);
        int idRutina = obtenerOCrearRutina(usuario);

        String sql = "INSERT INTO rutina_ejercicio (rutina_id, ejercicio_id) VALUES (?, ?)";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idRutina);
            sentencia.setInt(2, idEjercicio);
            sentencia.executeUpdate();
            return obtenerRutina(nombreUsuario);
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                throw new DatoInvalidoException(
                        "El ejercicio ya fue agregado a la rutina.");
            }
            throw errorBaseDatos("agregar el ejercicio a la rutina", ex);
        }
    }

    public synchronized Rutina eliminarEjercicioDeRutina(String nombreUsuario, int idEjercicio)
            throws DatoInvalidoException {
        Usuario usuario = buscarUsuario(nombreUsuario);
        int idRutina = obtenerOCrearRutina(usuario);
        String sql = "DELETE FROM rutina_ejercicio "
                + "WHERE rutina_id = ? AND ejercicio_id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idRutina);
            sentencia.setInt(2, idEjercicio);
            int eliminados = sentencia.executeUpdate();
            if (eliminados == 0) {
                throw new DatoInvalidoException(
                        "El ejercicio no pertenece a la rutina.");
            }
            return obtenerRutina(nombreUsuario);
        } catch (SQLException ex) {
            throw errorBaseDatos("eliminar el ejercicio de la rutina", ex);
        }
    }

    public synchronized Rutina obtenerRutina(String nombreUsuario)
            throws DatoInvalidoException {
        Usuario usuario = buscarUsuario(nombreUsuario);
        int idRutina = obtenerOCrearRutina(usuario);
        Rutina rutina = new Rutina(idRutina, "Rutina personal", usuario);

        String sql = "SELECT e.* FROM ejercicio e "
                + "INNER JOIN rutina_ejercicio re ON re.ejercicio_id = e.id "
                + "WHERE re.rutina_id = ? ORDER BY e.id";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idRutina);
            try (ResultSet resultado = sentencia.executeQuery()) {
                for (Ejercicio ejercicio : leerEjercicios(resultado)) {
                    rutina.agregarEjercicio(ejercicio);
                }
            }
            return rutina;
        } catch (SQLException ex) {
            throw errorBaseDatos("obtener la rutina", ex);
        }
    }

    public synchronized void registrarEjercicio(String nombreAdministrador,
            Ejercicio ejercicio) throws DatoInvalidoException {
        Usuario administrador = buscarUsuario(nombreAdministrador);
        if (!administrador.isAdministrador()) {
            throw new DatoInvalidoException(
                    "Solo el administrador puede registrar ejercicios.");
        }
        if (ejercicio == null) {
            throw new DatoInvalidoException("El ejercicio no puede ser nulo.");
        }
        if (ejercicio.getNombre() == null || ejercicio.getNombre().trim().isEmpty()
                || ejercicio.getCategoria() == null
                || ejercicio.getNivelDificultad() == null
                || ejercicio.getDescripcion() == null
                || ejercicio.getDescripcion().trim().isEmpty()) {
            throw new DatoInvalidoException(
                    "Complete los datos obligatorios del ejercicio.");
        }
        if (ejercicio instanceof EjercicioFuerza
                && ejercicio.getCategoria() == CategoriaEjercicio.CARDIO) {
            throw new DatoInvalidoException(
                    "Un ejercicio de fuerza debe usar una categoria muscular.");
        }
        if (ejercicio instanceof EjercicioCardio
                && ejercicio.getCategoria() != CategoriaEjercicio.CARDIO) {
            throw new DatoInvalidoException(
                    "Un ejercicio cardiovascular debe usar la categoria CARDIO.");
        }

        String sql = "INSERT INTO ejercicio "
                + "(nombre, categoria, nivel_dificultad, descripcion, tipo, "
                + "series, repeticiones, minutos) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, ejercicio.getNombre());
            sentencia.setString(2, ejercicio.getCategoria().name());
            sentencia.setString(3, ejercicio.getNivelDificultad().name());
            sentencia.setString(4, ejercicio.getDescripcion());
            if (ejercicio instanceof EjercicioFuerza) {
                EjercicioFuerza fuerza = (EjercicioFuerza) ejercicio;
                sentencia.setString(5, "FUERZA");
                sentencia.setInt(6, fuerza.getSeries());
                sentencia.setInt(7, fuerza.getRepeticiones());
                sentencia.setNull(8, Types.INTEGER);
            } else if (ejercicio instanceof EjercicioCardio) {
                EjercicioCardio cardio = (EjercicioCardio) ejercicio;
                sentencia.setString(5, "CARDIO");
                sentencia.setNull(6, Types.INTEGER);
                sentencia.setNull(7, Types.INTEGER);
                sentencia.setInt(8, cardio.getMinutos());
            } else {
                throw new DatoInvalidoException("Tipo de ejercicio no soportado.");
            }
            sentencia.executeUpdate();
        } catch (SQLException ex) {
            throw errorBaseDatos("registrar el ejercicio", ex);
        }
    }

    private Usuario buscarUsuario(String nombreUsuario) throws DatoInvalidoException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new DatoInvalidoException("Usuario no encontrado.");
        }
        String sql = "SELECT id, nombre_completo, nombre_usuario, administrador "
                + "FROM usuario WHERE LOWER(nombre_usuario) = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nombreUsuario.trim().toLowerCase());
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    throw new DatoInvalidoException("Usuario no encontrado.");
                }
                return new Usuario(
                        resultado.getInt("id"),
                        resultado.getString("nombre_completo"),
                        resultado.getString("nombre_usuario"),
                        "",
                        resultado.getBoolean("administrador"));
            }
        } catch (SQLException ex) {
            throw errorBaseDatos("buscar el usuario", ex);
        }
    }

    private Ejercicio buscarEjercicioPorId(int idEjercicio)
            throws DatoInvalidoException {
        String sql = "SELECT * FROM ejercicio WHERE id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idEjercicio);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    throw new DatoInvalidoException("Ejercicio no encontrado.");
                }
                return crearEjercicio(resultado);
            }
        } catch (SQLException ex) {
            throw errorBaseDatos("buscar el ejercicio", ex);
        }
    }

    private int obtenerOCrearRutina(Usuario usuario) throws DatoInvalidoException {
        String consulta = "SELECT id FROM rutina WHERE usuario_id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, usuario.getId());
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("id");
                }
            }
        } catch (SQLException ex) {
            throw errorBaseDatos("buscar la rutina", ex);
        }

        String insercion = "INSERT INTO rutina (nombre, usuario_id) VALUES (?, ?)";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(
                        insercion, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, "Rutina personal");
            sentencia.setInt(2, usuario.getId());
            sentencia.executeUpdate();
            try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                if (llaves.next()) {
                    return llaves.getInt(1);
                }
            }
            throw new DatoInvalidoException("No se pudo crear la rutina.");
        } catch (SQLException ex) {
            throw errorBaseDatos("crear la rutina", ex);
        }
    }

    private List<Ejercicio> leerEjercicios(ResultSet resultado) throws SQLException {
        List<Ejercicio> ejercicios = new ArrayList<>();
        while (resultado.next()) {
            ejercicios.add(crearEjercicio(resultado));
        }
        return ejercicios;
    }

    private Ejercicio crearEjercicio(ResultSet resultado) throws SQLException {
        int id = resultado.getInt("id");
        String nombre = resultado.getString("nombre");
        CategoriaEjercicio categoria = CategoriaEjercicio.valueOf(
                resultado.getString("categoria"));
        NivelDificultad nivel = NivelDificultad.valueOf(
                resultado.getString("nivel_dificultad"));
        String descripcion = resultado.getString("descripcion");
        String tipo = resultado.getString("tipo");
        if ("FUERZA".equals(tipo)) {
            return new EjercicioFuerza(id, nombre, categoria, nivel, descripcion,
                    resultado.getInt("series"), resultado.getInt("repeticiones"));
        }
        return new EjercicioCardio(id, nombre, categoria, nivel, descripcion,
                resultado.getInt("minutos"));
    }

    private DatoInvalidoException errorBaseDatos(String accion, SQLException ex) {
        System.out.println("Error al " + accion + ": " + ex.getMessage());
        return new DatoInvalidoException(
                "No fue posible " + accion + " en la base de datos.");
    }
}
