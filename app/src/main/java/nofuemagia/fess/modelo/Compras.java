package nofuemagia.fess.modelo;

import java.util.List;

/**
 * Created by Tano on 12/2/2017.
 * No Fue Magia
 */

public class Compras {
    private int idCompra;
    private String estado;
    private String fecha;
    private int idLocal;
    private String local;
    private String barrio;
    private int comuna;

    private boolean editar;
    private boolean comentar;
    private boolean comentado;

    private List<Productos> productos;

    public Compras() {
    }

    public int getIdCompra() {
        return idCompra;
    }

    public String getEstado() {
        return estado;
    }

    public String getFecha() {
        return fecha;
    }

    public int getIdLocal() {
        return idLocal;
    }

    public String getLocal() {
        return local;
    }

    public String getBarrio() {
        return barrio;
    }

    public int getComuna() {
        return comuna;
    }

    public boolean editar() {
        return editar;
    }

    public boolean comentar() {
        return comentar;
    }

    public boolean comentado() {
        return comentado;
    }

    public List<Productos> getProductos() {
        return productos;
    }


}
