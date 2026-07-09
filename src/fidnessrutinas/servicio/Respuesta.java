package fidnessrutinas.servicio;

import java.io.Serializable;

public class Respuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean exitosa;
    private String mensaje;
    private Object datos;

    private Respuesta(boolean exitosa, String mensaje, Object datos) {
        this.exitosa = exitosa;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public static Respuesta ok(String mensaje, Object datos) {
        return new Respuesta(true, mensaje, datos);
    }

    public static Respuesta error(String mensaje) {
        return new Respuesta(false, mensaje, null);
    }

    public boolean isExitosa() {
        return exitosa;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Object getDatos() {
        return datos;
    }
}
