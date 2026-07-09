package fidnessrutinas.modelo;

import fidnessrutinas.enums.CategoriaEjercicio;
import fidnessrutinas.enums.NivelDificultad;
import java.io.Serializable;

public abstract class Ejercicio implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private CategoriaEjercicio categoria;
    private NivelDificultad nivelDificultad;
    private String descripcion;

    public Ejercicio(int id, String nombre, CategoriaEjercicio categoria,
            NivelDificultad nivelDificultad, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.nivelDificultad = nivelDificultad;
        this.descripcion = descripcion;
    }

    public abstract String mostrarDetalle();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaEjercicio getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaEjercicio categoria) {
        this.categoria = categoria;
    }

    public NivelDificultad getNivelDificultad() {
        return nivelDificultad;
    }

    public void setNivelDificultad(NivelDificultad nivelDificultad) {
        this.nivelDificultad = nivelDificultad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
