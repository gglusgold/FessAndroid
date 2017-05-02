package nofuemagia.fess.notificaciones;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import nofuemagia.fess.Aplicacion;
import nofuemagia.fess.R;
import nofuemagia.fess.actividades.PantallaPrincipal;

/**
 * Created by Tano on 17/2/2017.
 * No Fue Magia
 */

public class NotificacionesMensaje extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mandarNotificacion(remoteMessage.getData());
    }

    private void mandarNotificacion(Map<String, String> data) {

        String titulo = data.get("titulo");
        String msg = data.get("mensaje");

        String usuario = "Frente de economia social y solidaria";

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(msg);
        bigText.setBigContentTitle(titulo);
        bigText.setSummaryText(usuario);

        Bundle args = new Bundle();
        args.putString(Aplicacion.DONDE, data.get("donde"));

        Intent i = new Intent(this, PantallaPrincipal.class);
        i.putExtras(args);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 223, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo_redondo))
                .setColor(ContextCompat.getColor(this, R.color.partido))
                .setContentTitle(titulo)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pendingIntent)
                .setContentText(msg)
                .setTicker(titulo)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(bigText)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0x123456, mBuilder.build());
    }
}
