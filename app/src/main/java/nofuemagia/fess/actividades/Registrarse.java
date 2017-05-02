package nofuemagia.fess.actividades;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import nofuemagia.fess.Aplicacion;
import nofuemagia.fess.R;
import nofuemagia.fess.otros.ComunicacionClient;

/**
 * Created by Tano on 17/2/2017.
 * No Fue Magia
 */

public class Registrarse extends AppCompatActivity {

    private TextInputLayout tilCorreo;
    private TextInputLayout tilNombres;
    private TextInputLayout tilTelefono;
    private TextInputLayout tilContraseña;
    private TextInputLayout tilContraseñaConfirmar;

    private EditText etCorreo;
    private EditText etNombres;
    private EditText etTelefono;
    private EditText etContraseña;
    private EditText etContraseñaRepetir;
    private Spinner spComunas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_registrarse);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
//            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Registrarse");
        toolbar.setSubtitle(getString(R.string.sub_titulo));

        tilCorreo = (TextInputLayout) findViewById(R.id.til_correo);
        tilNombres = (TextInputLayout) findViewById(R.id.til_nombres);
        tilTelefono = (TextInputLayout) findViewById(R.id.til_telefono);
        tilContraseña = (TextInputLayout) findViewById(R.id.til_contraseña_registrar);
        tilContraseñaConfirmar = (TextInputLayout) findViewById(R.id.til_contraseña_confirmar_registrar);

        etCorreo = (EditText) findViewById(R.id.et_correo);
        etNombres = (EditText) findViewById(R.id.et_nombres);
        etTelefono = (EditText) findViewById(R.id.et_telefono);
        spComunas = (Spinner) findViewById(R.id.sp_comunas);
        etContraseña = (EditText) findViewById(R.id.et_contraseña_registrar);
        etContraseñaRepetir = (EditText) findViewById(R.id.et_contraseña_confirmar_registrar);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_confirmar:
                validar();
        }
        return super.onOptionsItemSelected(item);
    }

    public void validar() {

        tilCorreo.setError(null);
        tilNombres.setError(null);
        tilTelefono.setError(null);
        tilContraseña.setError(null);
        tilContraseñaConfirmar.setError(null);

        String correo = etCorreo.getText().toString();
        String nombres = etNombres.getText().toString();
        String telefono = etTelefono.getText().toString();
        int comuna = spComunas.getSelectedItemPosition();
        String contraseña = etContraseña.getText().toString();
        String contraseña2 = etContraseñaRepetir.getText().toString();

        boolean validado = true;

        if (TextUtils.isEmpty(nombres)) {
            tilNombres.setError(getString(R.string.campo_obligatorio));
            validado = false;
        }

        if (TextUtils.isEmpty(correo)) {
            tilCorreo.setError(getString(R.string.campo_obligatorio));
            validado = false;
        } else if (!correo.contains("@")) {
            tilCorreo.setError(getString(R.string.mail_invalido));
            validado = false;
        }

        if (TextUtils.isEmpty(telefono)) {
            tilTelefono.setError(getString(R.string.campo_obligatorio));
            validado = false;
        }

        if (TextUtils.isEmpty(contraseña)) {
            tilContraseña.setError(getString(R.string.campo_obligatorio));
            validado = false;
        }

        if (TextUtils.isEmpty(contraseña2)) {
            tilContraseñaConfirmar.setError(getString(R.string.campo_obligatorio));
            validado = false;
        }

        if (comuna == 0) {
            Snackbar.make(etCorreo, "Elegí una comuna", Snackbar.LENGTH_LONG).show();
            validado = false;
        }

        if (!TextUtils.isEmpty(contraseña) && !contraseña.equals(contraseña2)) {
            tilContraseña.setError(getString(R.string.deben_coincidir));
            tilContraseñaConfirmar.setError(getString(R.string.deben_coincidir));
            validado = false;
        }

        if (validado) {
            registrarse(correo, nombres, telefono, comuna, contraseña);
        }
    }

    private void registrarse(String correo, String nombres, String telefono, int comuna, String contraseña) {

        RequestParams params = new RequestParams();
        params.put("mail", correo);
        params.put("nombres", nombres);
        params.put("telefono", telefono);
        params.put("password", contraseña);
        params.put("comuna", comuna);
        params.put("token", FirebaseInstanceId.getInstance().getToken());

        ComunicacionClient client = new ComunicacionClient();
        client.post(ComunicacionClient.REGISTRARSE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response.has("error")) {
                    Aplicacion.mostrarSnack(etContraseña, response.optString("error"), null);
                } else {
                    Aplicacion.mostrarSnack(etContraseña, "Te enviamos un mail para la confirmación", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Aplicacion.mostrarSnack(etContraseña, "Ocurrió un error", null);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.registrarse, menu);

        return true;
    }
}
