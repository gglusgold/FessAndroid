package nofuemagia.fess;

import android.app.Application;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

/**
 * Created by Tano on 27/1/2017.
 * No Fue Magia
 */


public class Aplicacion extends Application {

    public static final String IDVECINO = "IDVECINO";
    public static final String NOMBRE = "NOMBRE";
    public static final String TELEFONO = "TELEFONO";
    public static final String COMUNA = "COMUNA";
    public static final String CORREO = "CORREO";
    public static final String LOGUEADO = "LOGUEADO";

    public static final String DONDE = "DONDE";
    public static final String MISCOMPRAS = "MISCOMPRAS";
    public static final String CARRITO = "CARRITO";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static void mostrarSnack(View v, String error, View.OnClickListener o) {
        final Snackbar snack = Snackbar.make(v, error, Snackbar.LENGTH_INDEFINITE);
        snack.setAction("Ok", o == null ? new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snack.dismiss();
            }
        } : o).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
