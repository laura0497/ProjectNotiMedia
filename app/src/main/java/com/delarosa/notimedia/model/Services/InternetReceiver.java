package com.delarosa.notimedia.model.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.delarosa.notimedia.model.DataBase.DbNotification;
import com.delarosa.notimedia.model.Json.SyncNotification;
import com.delarosa.notimedia.model.Utils.PreferencesAdapter;
import com.delarosa.notimedia.model.Utils.UtilClass;


public class InternetReceiver extends BroadcastReceiver {

    DbNotification dbNotification;
    PreferencesAdapter preferencesAdapter;
    SyncNotification syncNotification;
    UtilClass utilClass;

    public InternetReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        dbNotification = new DbNotification(context);
        preferencesAdapter = new PreferencesAdapter(context);
        syncNotification = new SyncNotification(context);
        utilClass = new UtilClass(context);
        //get the id of the current download
        int id = preferencesAdapter.getPreferenceInt("idNotificationtoUpdtade");
        //if lose conection, delete the notification locally
        if (!isConnected(context)) {
            Toast.makeText(context, "Perdimos conexión!", Toast.LENGTH_LONG).show();
            if (id != 0)
                preferencesAdapter.setPreferenceString("idtoDelete", "todelete");
            dbNotification.deleteNotification(id);

            //if have internet, validates if have an id to delete in the server
        } else {
            String todelete = preferencesAdapter.getPreferenceString("idtoDelete");
            if (todelete.equals("todelete")) {
                if (id != 0) {

                    syncNotification.deleteNotification(id);
                }
                preferencesAdapter.setPreferenceString("idtoDelete", "");
                preferencesAdapter.setPreferenceInt("idNotificationtoUpdtade", 0);
            }

        }
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        return isConnected;
    }
}
