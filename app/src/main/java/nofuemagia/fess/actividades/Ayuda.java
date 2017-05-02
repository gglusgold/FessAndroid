package nofuemagia.fess.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import nofuemagia.fess.R;

/**
 * Creado por los trabajadores del Ministerio de Agroindustria
 */
public class Ayuda extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addSlide(AppIntroFragment.newInstance("¡¡Muchas gracias!!", "En las siguientes pantallas te vamos a enseñar a usar la aplicación", R.mipmap.splash, Color.parseColor("#55B3C2")));

        addSlide(AppIntroFragment.newInstance("Noticias", "La primer pantalla de la app! Acá vas a poder estar informado de las ultimas noticias de la economia socual y solidaria", R.mipmap.noticias, Color.parseColor("#64A4A4")));
        addSlide(AppIntroFragment.newInstance("Menu", "Desde el menu, podes hacer tus compras, acceder al historial y cerrar sesion", R.mipmap.menu, Color.parseColor("#64A4A4")));

        addSlide(AppIntroFragment.newInstance("Primer paso", "Es importante revisar el día en que se tiene a retirar el pedido", R.mipmap.unopaso, Color.parseColor("#66D7B9")));
        addSlide(AppIntroFragment.newInstance("Segundo paso", "En esta pantalla es necesario indicar el local por el cual se va a retirar el pedido", R.mipmap.dospaso, Color.parseColor("#66D7B9")));
        addSlide(AppIntroFragment.newInstance("Tecer paso", "Acá vas a encontrar las distitnas  categorias, y adentro los productos correspondientes, tocar el changuito para sumar un pedido", R.mipmap.trespaso, Color.parseColor("#66D7B9")));
        addSlide(AppIntroFragment.newInstance("Cuarto paso", "Por último, el listado de lo pedido, y el precio total, tocar Confirmar para seguir", R.mipmap.cuatropaso, Color.parseColor("#66D7B9")));
        addSlide(AppIntroFragment.newInstance("¡¡Listo!!", "Una vez chequeado, aparecerá el siguiente cartel", R.mipmap.confirmar, Color.parseColor("#66D7B9")));

        addSlide(AppIntroFragment.newInstance("Mis compras", "En esta pantalla aparece el historial de tus compras, haciendo click en el titulo, aparece un detalle. Además podes cancelar o modificar tu compra", R.mipmap.miscompras, Color.parseColor("#FDBC7D")));


        showSkipButton(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        avanzarPrincipal();
    }

    private void avanzarPrincipal() {

        SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        preferences.edit().putBoolean("Ayuda", false).apply();

        Toast.makeText(this, "Gracias!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PantallaPrincipal.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        avanzarPrincipal();
    }
}
