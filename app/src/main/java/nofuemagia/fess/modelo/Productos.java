package nofuemagia.fess.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Tano on 23/1/2017.
 * No Fue Magia
 */
public class Productos {

    @SerializedName("idProducto")
    @Expose
    private int idProducto;

    private String nombre;

    private String marca;

    private String presentacion;

    private double precio;

    private int stock;

    private int idCategoria;

    private String categoria;

    @SerializedName("pedidos")
    @Expose
    private int pedidos;

    private int cantidad;
    private boolean comentado;
    private double precioUnidad;

    public Productos(String prueba) {
        this.nombre = prueba;
        this.marca = "marca";
        this.presentacion = "pres";
        this.precio = 20;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getPedidos() {
        return pedidos;
    }

    public void setPedidos(int pedidos) {
        this.pedidos = pedidos;
    }

    public int getCantidad() {
        return cantidad;
    }

    public boolean isComentado() {
        return comentado;
    }

    public double getPrecioUnidad() {
        return precioUnidad;
    }
}
