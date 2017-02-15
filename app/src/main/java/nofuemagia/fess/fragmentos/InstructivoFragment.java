package nofuemagia.fess.fragmentos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import nofuemagia.fess.ComunicacionClient;
import nofuemagia.fess.R;
import nofuemagia.fess.modelo.Locales;

/**
 * Created by Tano on 22/1/2017.
 */
public class InstructivoFragment extends Fragment {

    private OnInstructivoListener mOnInstructivoListener;
    private TextView tvFecha;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_instructivo, container, false);

        tvFecha = (TextView) v.findViewById(R.id.tv_proxima_entrega);

        return v;
    }

    public void setOnInstructivoListener(OnInstructivoListener mOnInstructivoListener) {
        this.mOnInstructivoListener = mOnInstructivoListener;
    }

    public void traerLocales() {
        String url = ComunicacionClient.LOCALES;

        ComunicacionClient client = new ComunicacionClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Type listType = new TypeToken<List<Locales>>() {
                }.getType();
                List<Locales> yourList = new Gson().fromJson(String.valueOf(response.optJSONArray("Lista")), listType);

                Collections.sort(yourList, new Comparator<Locales>() {
                    public int compare(Locales s1, Locales s2) {
                        return s1.getComuna() < s2.getComuna() ? -1 : 1;
                    }
                });


                actualizarFecha(response.optString("Proxima"));
                mOnInstructivoListener.terminoLocales(yourList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Snackbar.make(tvFecha, R.string.error_comunicacion, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void actualizarFecha(String fecha) {
        if (tvFecha != null && fecha != null)
            tvFecha.setText("Tu pedido lo tenés que retirar el sábado " + fecha + " en el horario según el local que elijas");
    }

    public interface OnInstructivoListener {
        void terminoLocales(List<Locales> locales);
    }
}
