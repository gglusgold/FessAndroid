package nofuemagia.fess.actividades;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import nofuemagia.fess.Aplicacion;
import nofuemagia.fess.R;
import nofuemagia.fess.otros.ComunicacionClient;

/**
 * Creado por los trabajadores del Ministerio de Agroindustria
 */

public class DetalleProducto extends AppCompatActivity {


    private ImageView ivProducto;
    private TextView tvTitulo;
    private TextView tvDescripcion;
    private RecyclerView rvComentarios;
    private RatingBar rbEstrellas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);



        ivProducto = (ImageView) findViewById(R.id.iv_producto_detalle);
        tvTitulo = (TextView) findViewById(R.id.tv_titulo_detalle);
        tvDescripcion = (TextView) findViewById(R.id.tv_descripcion_detalle);
        rvComentarios = (RecyclerView) findViewById(R.id.rv_comentarios_detalle);
        rbEstrellas = (RatingBar) findViewById(R.id.rb_producto_detalle);

        int idProducto = getIntent().getIntExtra("id", -1);
        if ( idProducto == -1 )
            Aplicacion.mostrarSnack(ivProducto, "No se encontro información del producto", null);
        else
        {
            final String url = ComunicacionClient.URL_IMAGNES + idProducto + ".jpg";

            RequestParams params = new RequestParams();
            params.put("id", idProducto);

            ComunicacionClient client = new ComunicacionClient();
            client.post(this, ComunicacionClient.DETALLE_PRODUCTO, params ,new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    System.out.println(response);
                    Glide.with(DetalleProducto.this).load(url).thumbnail(0.6f).centerCrop().placeholder(R.mipmap.logo_redondo).into(ivProducto);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Aplicacion.mostrarSnack(ivProducto, "No se encontro información del producto", null);
                }
            });
        }

    }
}
