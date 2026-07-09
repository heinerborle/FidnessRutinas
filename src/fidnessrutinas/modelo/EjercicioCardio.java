package fidnessrutinas.modelo;

import fidnessrutinas.enums.CategoriaEjercicio;
import fidnessrutinas.enums.NivelDificultad;

public class EjercicioCardio extends Ejercicio {

    private static final long serialVersionUID = 1L;

    private int minutos;

    public EjercicioCardio(int id, String nombre, CategoriaEjercicio categoria,
            NivelDificultad nivelDificultad, String descripcion, int minutos) {
        super(id, nombre, categoria, nivelDificultad, descripcion);
        this.minutos = minutos;
    }

    @Override
    public String mostrarDetalle() {
        return "Ejercicio cardiovascular\n"
                + "Nombre: " + getNombre() + "\n"
                + "Categoria: " + getCategoria() + "\n"
                + "Dificultad: " + getNivelDificultad() + "\n"
                + "Minutos sugeridos: " + minutos + "\n\n"
                + getDescripcion();
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }
}
