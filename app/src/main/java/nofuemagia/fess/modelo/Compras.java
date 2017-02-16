package nofuemagia.fess.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Tano on 12/2/2017.
 * No Fue Magia
 */

public class Compras implements Parcelable {
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

    protected Compras(Parcel in) {
        idCompra = in.readInt();
        estado = in.readString();
        fecha = in.readString();
        idLocal = in.readInt();
        local = in.readString();
        barrio = in.readString();
        comuna = in.readInt();
        editar = in.readByte() != 0;
        comentar = in.readByte() != 0;
        comentado = in.readByte() != 0;
    }

    public static final Creator<Compras> CREATOR = new Creator<Compras>() {
        @Override
        public Compras createFromParcel(Parcel in) {
            return new Compras(in);
        }

        @Override
        public Compras[] newArray(int size) {
            return new Compras[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(idCompra);
        parcel.writeString(estado);
        parcel.writeString(fecha);
        parcel.writeInt(idLocal);
        parcel.writeString(local);
        parcel.writeString(barrio);
        parcel.writeInt(comuna);
        parcel.writeByte((byte) (editar ? 1 : 0));
        parcel.writeByte((byte) (comentar ? 1 : 0));
        parcel.writeByte((byte) (comentado ? 1 : 0));
    }
}
