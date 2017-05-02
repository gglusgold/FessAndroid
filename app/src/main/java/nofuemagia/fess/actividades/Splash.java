package nofuemagia.fess.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import nofuemagia.fess.R;

/**
 * Creado por los trabajadores del Ministerio de Agroindustria
 */
public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemClock.sleep(1000);

        Class abrirDonde = PantallaPrincipal.class;
        SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        if (preferences.getBoolean("Ayuda", true))
            abrirDonde = Ayuda.class;

        Intent intent = new Intent(this, abrirDonde);
        startActivity(intent);
        finish();
    }
}
