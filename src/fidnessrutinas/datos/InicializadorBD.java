package fidnessrutinas.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class InicializadorBD {

    private InicializadorBD() {
    }

    public static void inicializar() throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
                Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate("CREATE TABLE IF NOT EXISTS usuario ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nombre_completo VARCHAR(120) NOT NULL,"
                    + "nombre_usuario VARCHAR(50) NOT NULL UNIQUE,"
                    + "contrasena VARCHAR(120) NOT NULL,"
                    + "administrador BOOLEAN NOT NULL DEFAULT FALSE)");
            sentencia.executeUpdate("CREATE TABLE IF NOT EXISTS ejercicio ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nombre VARCHAR(120) NOT NULL,"
                    + "categoria VARCHAR(30) NOT NULL,"
                    + "nivel_dificultad VARCHAR(30) NOT NULL,"
                    + "descripcion VARCHAR(500) NOT NULL,"
                    + "tipo VARCHAR(20) NOT NULL,"
                    + "series INT NULL,"
                    + "repeticiones INT NULL,"
                    + "minutos INT NULL)");
            sentencia.executeUpdate("CREATE TABLE IF NOT EXISTS rutina ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nombre VARCHAR(120) NOT NULL,"
                    + "usuario_id INT NOT NULL UNIQUE,"
                    + "CONSTRAINT fk_rutina_usuario FOREIGN KEY (usuario_id) "
                    + "REFERENCES usuario(id))");
            sentencia.executeUpdate("CREATE TABLE IF NOT EXISTS rutina_ejercicio ("
                    + "rutina_id INT NOT NULL,"
                    + "ejercicio_id INT NOT NULL,"
                    + "PRIMARY KEY (rutina_id, ejercicio_id),"
                    + "CONSTRAINT fk_rutina_ejercicio_rutina FOREIGN KEY (rutina_id) "
                    + "REFERENCES rutina(id) ON DELETE CASCADE,"
                    + "CONSTRAINT fk_rutina_ejercicio_ejercicio FOREIGN KEY (ejercicio_id) "
                    + "REFERENCES ejercicio(id))");
        }
        cargarUsuariosIniciales();
        cargarEjerciciosIniciales();
    }

    private static void cargarUsuariosIniciales() throws SQLException {
        if (contar("usuario") > 0) {
            return;
        }
        String sql = "INSERT INTO usuario "
                + "(nombre_completo, nombre_usuario, contrasena, administrador) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            agregarUsuario(sentencia, "Heiner Borbon", "heiner", "1234", false);
            agregarUsuario(sentencia, "Administrador Fidness", "admin", "admin", true);
            sentencia.executeBatch();
        }
    }

    private static void agregarUsuario(PreparedStatement sentencia,
            String nombreCompleto, String nombreUsuario, String contrasena,
            boolean administrador) throws SQLException {
        sentencia.setString(1, nombreCompleto);
        sentencia.setString(2, nombreUsuario);
        sentencia.setString(3, contrasena);
        sentencia.setBoolean(4, administrador);
        sentencia.addBatch();
    }

    private static void cargarEjerciciosIniciales() throws SQLException {
        if (contar("ejercicio") > 0) {
            return;
        }
        String sql = "INSERT INTO ejercicio "
                + "(nombre, categoria, nivel_dificultad, descripcion, tipo, "
                + "series, repeticiones, minutos) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            agregarFuerza(sentencia, "Sentadilla", "PIERNA", "INTERMEDIO",
                    "Mantenga la espalda recta, baje controladamente y empuje con los talones.",
                    4, 12);
            agregarFuerza(sentencia, "Press de banca", "PECHO", "INTERMEDIO",
                    "Acuestese en el banco, baje la barra al pecho y suba sin bloquear los codos.",
                    4, 10);
            agregarFuerza(sentencia, "Curl de biceps", "BRAZO", "PRINCIPIANTE",
                    "Mantenga los codos cerca del torso y suba la mancuerna sin balancear el cuerpo.",
                    3, 12);
            agregarFuerza(sentencia, "Remo con mancuerna", "ESPALDA", "INTERMEDIO",
                    "Apoye una rodilla en el banco y lleve la mancuerna hacia la cintura.",
                    4, 10);
            agregarFuerza(sentencia, "Plancha abdominal", "ABDOMEN", "PRINCIPIANTE",
                    "Mantenga el abdomen contraido y el cuerpo alineado de pies a cabeza.",
                    3, 30);
            agregarCardio(sentencia, "Caminadora", "CARDIO", "PRINCIPIANTE",
                    "Caminar o trotar con ritmo constante, cuidando la respiracion.", 20);
            agregarCardio(sentencia, "Bicicleta estatica", "CARDIO", "INTERMEDIO",
                    "Ajuste la resistencia y mantenga cadencia estable durante la sesion.", 25);
            sentencia.executeBatch();
        }
    }

    private static void agregarFuerza(PreparedStatement sentencia, String nombre,
            String categoria, String nivel, String descripcion, int series,
            int repeticiones) throws SQLException {
        sentencia.setString(1, nombre);
        sentencia.setString(2, categoria);
        sentencia.setString(3, nivel);
        sentencia.setString(4, descripcion);
        sentencia.setString(5, "FUERZA");
        sentencia.setInt(6, series);
        sentencia.setInt(7, repeticiones);
        sentencia.setNull(8, java.sql.Types.INTEGER);
        sentencia.addBatch();
    }

    private static void agregarCardio(PreparedStatement sentencia, String nombre,
            String categoria, String nivel, String descripcion, int minutos)
            throws SQLException {
        sentencia.setString(1, nombre);
        sentencia.setString(2, categoria);
        sentencia.setString(3, nivel);
        sentencia.setString(4, descripcion);
        sentencia.setString(5, "CARDIO");
        sentencia.setNull(6, java.sql.Types.INTEGER);
        sentencia.setNull(7, java.sql.Types.INTEGER);
        sentencia.setInt(8, minutos);
        sentencia.addBatch();
    }

    private static int contar(String tabla) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM " + tabla;
        try (Connection conexion = ConexionBD.obtenerConexion();
                Statement sentencia = conexion.createStatement();
                ResultSet resultado = sentencia.executeQuery(sql)) {
            resultado.next();
            return resultado.getInt("total");
        }
    }
}
