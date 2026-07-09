package fidnessrutinas.modelo;

import fidnessrutinas.excepciones.DatoInvalidoException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rutina implements Exportable, Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private Usuario usuario;
    private List<Ejercicio> ejercicios;

    public Rutina(int id, String nombre, Usuario usuario) throws DatoInvalidoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("El nombre de la rutina es obligatorio.");
        }
        if (usuario == null) {
            throw new DatoInvalidoException("La rutina debe estar asociada a un usuario.");
        }
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.ejercicios = new ArrayList<>();
    }

    public void agregarEjercicio(Ejercicio ejercicio) throws DatoInvalidoException {
        if (ejercicio == null) {
            throw new DatoInvalidoException("Debe seleccionar un ejercicio.");
        }
        for (Ejercicio actual : ejercicios) {
            if (actual.getId() == ejercicio.getId()) {
                throw new DatoInvalidoException("El ejercicio ya fue agregado a la rutina.");
            }
        }
        ejercicios.add(ejercicio);
    }

    public void eliminarEjercicio(int idEjercicio) {
        for (int i = 0; i < ejercicios.size(); i++) {
            if (ejercicios.get(i).getId() == idEjercicio) {
                ejercicios.remove(i);
                return;
            }
        }
    }

    @Override
    public String generarResumen() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("Rutina: ").append(nombre).append("\n");
        resumen.append("Usuario: ").append(usuario.getNombreCompleto()).append("\n");
        resumen.append("Ejercicios seleccionados: ").append(ejercicios.size()).append("\n\n");
        for (Ejercicio ejercicio : ejercicios) {
            resumen.append("- ")
                    .append(ejercicio.getNombre())
                    .append(" | ")
                    .append(ejercicio.getCategoria())
                    .append(" | ")
                    .append(ejercicio.getNivelDificultad())
                    .append("\n");
        }
        return resumen.toString();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<Ejercicio> getEjercicios() {
        return Collections.unmodifiableList(ejercicios);
    }
}
