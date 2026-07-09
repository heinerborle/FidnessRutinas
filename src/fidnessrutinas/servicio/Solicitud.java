package fidnessrutinas.servicio;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Solicitud implements Serializable {

    private static final long serialVersionUID = 1L;

    private Operacion operacion;
    private Map<String, Object> datos;

    public Solicitud(Operacion operacion) {
        this.operacion = operacion;
        this.datos = new HashMap<>();
    }

    public Solicitud agregarDato(String clave, Object valor) {
        datos.put(clave, valor);
        return this;
    }

    public Object obtenerDato(String clave) {
        return datos.get(clave);
    }

    public Operacion getOperacion() {
        return operacion;
    }
}
