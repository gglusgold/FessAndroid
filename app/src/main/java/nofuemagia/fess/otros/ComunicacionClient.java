package nofuemagia.fess.otros;

import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by Tano on 22/1/2017.
 * No Fue Magia
 */
public class ComunicacionClient extends AsyncHttpClient {

    public enum Servidores {
        PRODUCCION,
        ECONO,
        DESA,
        TRABAJO,
        CASA
    }


    private static final int TIMEOUT = 20 * 10000;

    private static final boolean debug = false;
    private static Servidores actual = Servidores.PRODUCCION;

    public static final String URL_IMAGNES = getUrl() + "Imagenes/Producto-";

    public static final String LOCALES = getUrl() + "api/apiproductos/Locales";

    public static final String PRODUCTOS = getUrl() + "api/apiproductos/Productos";

    public static final String INICIAR_SESION = getUrl() + "api/apiproductos/Usuario";

    public static final String NOTICIAS = getUrl() + "api/apiproductos/Noticias";

    public static final String PEDIR = getUrl() + "api/apiproductos/Pedir";
    public static final String MIS_COMPRAS = getUrl() + "api/apiproductos/MisCompras";
    public static final String CANCELAR_PEDIDO = getUrl() + "api/apiproductos/CancelarPedido";

    public static final String MANDAR_TOKEN = getUrl() + "api/apiproductos/ActualizarToken";
    public static final String REGISTRARSE = getUrl() + "api/apiproductos/Registrarse";


    public ComunicacionClient() {
        super();

        setTimeout(TIMEOUT);
        setConnectTimeout(TIMEOUT);
        setResponseTimeout(TIMEOUT);
        setMaxRetriesAndTimeout(10, TIMEOUT);

        setLoggingEnabled(true);
    }

    private static String getUrl() {
        if (debug) {
            if (actual == Servidores.ECONO)
                return "http://econosocial.somee.com/";
            else if (actual == Servidores.DESA)
                return "http://magyp-iis-desa.magyp.ar:5012/";
            else if (actual == Servidores.TRABAJO)
                return "http://192.168.116.61:56693/";
            else if (actual == Servidores.PRODUCCION)
                return "http://economiasocial.somee.com/";
            else
                return "";
        } else
            return "http://economiasocial.somee.com/";
    }
}
