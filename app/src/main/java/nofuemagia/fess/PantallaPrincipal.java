package nofuemagia.fess;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nofuemagia.fess.fragmentos.ComprasFragment;
import nofuemagia.fess.fragmentos.MisComprasFragment;
import nofuemagia.fess.fragmentos.NoticiasFragment;
import nofuemagia.fess.modelo.Categorias;
import nofuemagia.fess.modelo.Compras;

public class PantallaPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean confirmar = false;
    SharedPreferences pref;
    private DrawerLayout drawerLayout;
    private ComprasFragment comprasFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);

        pref = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getString(R.string.titulo));
        toolbar.setSubtitle(getString(R.string.sub_titulo));
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null)
            comprasFragment = new ComprasFragment();

        mostrarNoticias();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_confirmar:
                pedir();
        }
        return super.onOptionsItemSelected(item);
    }

    private void pedir() {

        boolean logueado = pref.getBoolean(Aplicacion.USUARIO, false);

        if (!logueado) {
            mostrarIniciar();
        } else {
            confirmarPedido();
        }
    }

    private void confirmarPedido() {
        AlertDialog builder = new AlertDialog.Builder(this)
                .setIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_check_mark, null))
                .setTitle(R.string.confirmar)
                .setMessage(R.string.confirmacion)
                .setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ConfirmacionRemota();
                    }
                })
                .setNegativeButton(R.string.cancelar, null).create();
        builder.show();
    }


    private void mostrarIniciar() {
        final View v = View.inflate(this, R.layout.dialog_iniciar, null);
        AppCompatButton btnIngresar = (AppCompatButton) v.findViewById(R.id.btn_ingresar_login);

        final AlertDialog builder = new AlertDialog.Builder(this)
                .setIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_iniciarsesion, null))
                .setTitle(R.string.iniciar_sesion)
                .setView(v)
                .setNeutralButton(R.string.registrarse, null).create();

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


                TextInputLayout tilCorreo = (TextInputLayout) v.findViewById(R.id.til_mail);
                EditText etCorreo = (EditText) v.findViewById(R.id.et_mail);

                TextInputLayout tilContraseña = (TextInputLayout) v.findViewById(R.id.til_contraseña);
                EditText etContraseña = (EditText) v.findViewById(R.id.et_contraseña);

                tilCorreo.setError(null);
                tilContraseña.setError(null);

                if (TextUtils.isEmpty(etCorreo.getText())) {
                    tilCorreo.setError(getString(R.string.campo_obligatorio));
                    return;
                }

                if (TextUtils.isEmpty(etContraseña.getText())) {
                    tilContraseña.setError(getString(R.string.campo_obligatorio));
                    return;
                }

                IniciarSesion(etCorreo.getText().toString(), etContraseña.getText().toString(), builder, v);
            }
        });


        builder.show();
    }

    private void IniciarSesion(final String correo, String pass, final AlertDialog builder, final View v) {

        RequestParams params = new RequestParams();
        params.put("correo", correo);
        params.put("pass", pass);

        ComunicacionClient client = new ComunicacionClient();
        client.post(ComunicacionClient.INICIAR_SESION, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response.has("Error")) {
                    Snackbar.make(v, response.optString("Error"), Snackbar.LENGTH_LONG).show();
                } else {
                    JSONObject Vecino = response.optJSONObject("Vecino");

                    pref.edit()
                            .putInt(Aplicacion.IDVECINO, Vecino.optInt("idVecino"))
                            .putString(Aplicacion.NOMBRE, Vecino.optString("nombre"))
                            .putString(Aplicacion.TELEFONO, Vecino.optString("telefono"))
                            .putInt(Aplicacion.COMUNA, Vecino.optInt("comuna"))
                            .putString(Aplicacion.CORREO, correo)
                            .putBoolean(Aplicacion.USUARIO, true)
                            .apply();

                    Type listType = new TypeToken<List<Categorias>>() {
                    }.getType();
                    List<Compras> historico = new Gson().fromJson(response.optJSONArray("Historico").toString(), listType);

                    builder.dismiss();
                    confirmarPedido();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Aplicacion.mostrarSnack(findViewById(R.id.frag_container), getResources().getString(R.string.error_inicio), null);
            }
        });

    }

    private void ConfirmacionRemota() {

        ComprasPagerAdapter adapter = comprasFragment.getAdapter();

        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();

        RequestParams params = new RequestParams();
        params.put("idVecino", pref.getInt(Aplicacion.IDVECINO, -1));
        params.put("local", adapter.getIdLocal());
        params.put("productos", gson.toJson(adapter.listaProductos()));

        ComunicacionClient client = new ComunicacionClient();
        client.post(ComunicacionClient.PEDIR, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String error = response.optString("error");
                if (TextUtils.isEmpty(error)) {
                    Aplicacion.mostrarSnack(findViewById(R.id.frag_container), getResources().getString(R.string.muchas_gracias), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mostrarMisCompras();
                            invalidateOptionsMenu();
                        }
                    });
                } else
                    Aplicacion.mostrarSnack(findViewById(R.id.frag_container), error, null);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Aplicacion.mostrarSnack(findViewById(R.id.frag_container), errorResponse.optString("error"), null);
            }
        });
    }

    private void mostrarNoticias() {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frag_container, new NoticiasFragment())
                .commitNow();
    }

    private void mostrarComprar() {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frag_container, comprasFragment)
                .commitNow();
    }

    private void mostrarMisCompras() {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frag_container, new MisComprasFragment())
                .commitNow();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pantalla_principal, menu);

        Fragment fActual = getSupportFragmentManager().findFragmentById(R.id.frag_container);

        MenuItem miConfirmar = menu.findItem(R.id.action_confirmar);
        if (comprasFragment.getCurrentItem() == 3 && fActual instanceof ComprasFragment)
            miConfirmar.setVisible(confirmar);
        else
            miConfirmar.setVisible(false);

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                mostrarNoticias();
                drawerLayout.closeDrawer(Gravity.START);
                return true;

            case R.id.nav_productos:
                mostrarComprar();
                drawerLayout.closeDrawer(Gravity.START);
                return true;

            case R.id.nav_carrito:
                mostrarMisCompras();
                drawerLayout.closeDrawer(Gravity.START);
                return true;

            case R.id.nav_log_out:
                pref.edit().clear().apply();
                mostrarNoticias();
                drawerLayout.closeDrawer(Gravity.START);
                return true;
        }
        return false;
    }


}
