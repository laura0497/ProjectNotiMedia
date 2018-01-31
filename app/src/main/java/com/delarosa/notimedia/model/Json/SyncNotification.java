package com.delarosa.notimedia.model.Json;

import android.content.Context;

import com.delarosa.notimedia.model.DataBase.DbNotification;
import com.delarosa.notimedia.model.Entitys.NotificationManagerEntity;
import com.delarosa.notimedia.model.Services.JSONServices;
import com.delarosa.notimedia.model.Utils.PreferencesAdapter;
import com.delarosa.notimedia.model.Utils.UtilClass;

import org.json.JSONException;
import org.json.JSONObject;


public class SyncNotification {
    static DbNotification dbNotification;
    static PreferencesAdapter preferencesAdapter;
   static Context contex;
    UtilClass utilClass;

    public SyncNotification(Context contex) {
        this.contex = contex;
        dbNotification = new DbNotification(contex);
        preferencesAdapter = new PreferencesAdapter(contex);
        utilClass = new UtilClass(contex);
    }

   //sync with server
    public void SaveNotification(NotificationManagerEntity notificacion) {
        String method = "POST";

        if (utilClass.checkInternet(contex)) {

            JSONObject jsonObjectNotification = createJson(notificacion);
            sendToServer(jsonObjectNotification, method, 1, "");
        }else{
            utilClass.AlertNoInternet(contex);
        }


    }

    public void updateNotification(NotificationManagerEntity notificacion) {
        String method = "PUT";

        if (utilClass.checkInternet(contex)) {


            JSONObject jsonUpdateNotification = createJson(notificacion);

            try {
                jsonUpdateNotification.put("Duration", notificacion.getDuration());
                jsonUpdateNotification.put("Date", notificacion.getDate());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            sendToServer(jsonUpdateNotification, method, 2, String.valueOf(notificacion.getId()));
            preferencesAdapter.setPreferenceInt("idNotificationtoUpdtade", 0);
        }else{
            utilClass.AlertNoInternet(contex);
        }


    }

    public void deleteNotification(int id) {
        String method = "DELETE";

        if (utilClass.checkInternet(contex)) {
            JSONObject jsonObjectContact = new JSONObject();
            sendToServer(jsonObjectContact, method, 3, String.valueOf(id));
        }else{
            utilClass.AlertNoInternet(contex);
        }


    }

    //create the jsonObtect
    protected JSONObject createJson(NotificationManagerEntity notification) {
        JSONObject jsonObjectContact = new JSONObject();

        try {

            jsonObjectContact.put("NotificationId", notification.getId());
            jsonObjectContact.put("Date", notification.getDate());
            jsonObjectContact.put("Duration", notification.getDuration());

        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
        }
        return jsonObjectContact;
    }

    // Success responses
    public static void responseJsonPost(JSONObject response) throws JSONException {

        NotificationManagerEntity notificacion = new NotificationManagerEntity();
        notificacion.setId(response.getInt("NotificationId"));
        notificacion.setDate(response.getString("Date"));
        notificacion.setDuration(response.getInt("Duration"));
        //status 1 = start download
        notificacion.setStatus(1);
        //save data into local db
        dbNotification.saveNotification(notificacion);
       //save the id locally
        preferencesAdapter.setPreferenceInt("idNotificationtoUpdtade", response.getInt("NotificationId"));


    }

    public static void responseJsonPut(JSONObject response) throws JSONException {

       NotificationManagerEntity notificacion = new NotificationManagerEntity();
        notificacion.setId(response.getInt("NotificationId"));
        notificacion.setDate(response.getString("Date"));
        notificacion.setDuration(response.getInt("Duration"));
        //status 2 = finish download
        notificacion.setStatus(2);
        //update date and duration into db
        dbNotification.updateNotification(notificacion);
    }

    public static void responseJsonDelete(JSONObject response) throws JSONException {
        //onSuccess deleted
    }

    //send data to httpClient
    public void sendToServer(JSONObject jsonObject, String method, int action, String id) {

        JSONServices Json = new JSONServices(contex);
        try {
            if (action==2) {
                Json.update(jsonObject, method, id);
            } else if(action==1) {
                Json.insert(jsonObject, method);
            }else if(action==3) {
                Json.delete(jsonObject, method, id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
