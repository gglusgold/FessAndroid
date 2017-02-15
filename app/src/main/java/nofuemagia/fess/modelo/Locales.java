package nofuemagia.fess.modelo;

/**
 * Created by Tano on 22/1/2017.
 */

public class Locales {

    private int idLocal;
    private String nombre;
    private String direccion;
    private String horario;
    private int comuna;
    private String barrio;

    private boolean seleccionado;

    public int getIdLocal() {
        return idLocal;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getComuna() {
        return comuna;
    }

    public String getBarrio() {
        return barrio;
    }

    public String getHorario() {
        return horario;
    }

    public boolean seleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }
}
