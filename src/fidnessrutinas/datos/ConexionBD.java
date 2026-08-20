package fidnessrutinas.datos;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION =
            "nbproject/private/database.properties";
    private static final Properties PROPIEDADES = cargarPropiedades();

    private ConexionBD() {
    }

    private static Properties cargarPropiedades() {
        Properties propiedades = new Properties();
        String ruta = System.getProperty("fidness.database.config", ARCHIVO_CONFIGURACION);
        try (FileInputStream entrada = new FileInputStream(ruta)) {
            propiedades.load(entrada);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "No se pudo leer la configuracion privada de base de datos: " + ruta, ex);
        }

        validarPropiedad(propiedades, "url");
        validarPropiedad(propiedades, "usuario");
        validarPropiedad(propiedades, "contrasena");
        return propiedades;
    }

    private static void validarPropiedad(Properties propiedades, String nombre) {
        String valor = propiedades.getProperty(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Falta la propiedad obligatoria de base de datos: " + nombre);
        }
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(
                PROPIEDADES.getProperty("url"),
                PROPIEDADES.getProperty("usuario"),
                PROPIEDADES.getProperty("contrasena"));
    }
}
