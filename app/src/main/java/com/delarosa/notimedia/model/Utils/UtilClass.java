package com.delarosa.notimedia.model.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class UtilClass {

    Context context;

    public UtilClass(Context mContext) {
        context = mContext;
    }

    public UtilClass() {
    }

    public String getDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateCalendar = sd.format(c.getTime());
        return dateCalendar;
    }

    public boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void AlertNoInternet(final Context contexto) {

        String titulo = "Error de conexión";
        String mensaje = "No se detectó conexión a Internet";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(mensaje)
                .setCancelable(false)
                .setTitle(titulo)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                contexto.startActivity(myIntent);
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}