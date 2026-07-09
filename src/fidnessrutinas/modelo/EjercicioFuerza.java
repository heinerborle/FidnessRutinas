package fidnessrutinas.modelo;

import fidnessrutinas.enums.CategoriaEjercicio;
import fidnessrutinas.enums.NivelDificultad;

public class EjercicioFuerza extends Ejercicio {

    private static final long serialVersionUID = 1L;

    private int series;
    private int repeticiones;

    public EjercicioFuerza(int id, String nombre, CategoriaEjercicio categoria,
            NivelDificultad nivelDificultad, String descripcion, int series, int repeticiones) {
        super(id, nombre, categoria, nivelDificultad, descripcion);
        this.series = series;
        this.repeticiones = repeticiones;
    }

    @Override
    public String mostrarDetalle() {
        return "Ejercicio de fuerza\n"
                + "Nombre: " + getNombre() + "\n"
                + "Categoria: " + getCategoria() + "\n"
                + "Dificultad: " + getNivelDificultad() + "\n"
                + "Series: " + series + "\n"
                + "Repeticiones: " + repeticiones + "\n\n"
                + getDescripcion();
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }
}
