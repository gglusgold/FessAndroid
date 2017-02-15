package nofuemagia.fess.modelo;

import java.util.List;

/**
 * Created by Tano on 23/1/2017.
 * No Fue Magia
 */

public class Categorias {

    private String nombre;
    private List<Productos> productos;

    public Categorias(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Productos> getProductos() {
        return productos;
    }
}
