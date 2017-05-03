package nofuemagia.fess.notificaciones;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.loopj.android.http.BlackholeHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import nofuemagia.fess.Aplicacion;
import nofuemagia.fess.otros.ComunicacionClient;
import nofuemagia.fess.R;

/**
 * Created by Tano on 17/2/2017.
 * No Fue Magia
 */

public class NotificacionesIds extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();

        SharedPreferences pref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        RequestParams params = new RequestParams();
        params.put("idVecino", pref.getInt(Aplicacion.IDVECINO, -1));
        params.put("token", token);

        SyncHttpClient client = new SyncHttpClient();
        client.post(this, ComunicacionClient.MANDAR_TOKEN, params, new BlackholeHttpResponseHandler());
    }
}
