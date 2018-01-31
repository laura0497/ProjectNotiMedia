package com.delarosa.notimedia.model.Services;

import android.content.Context;

import com.delarosa.notimedia.model.Json.SyncNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONServices implements HttpInterface {

    private Runnable runReadAndParseJSON;
    Context _context;
    HttpClient httpClient;


    public JSONServices(Context contexto) {
        _context = contexto;
    }

    //insert notification
    public void insert(final JSONObject jsonObject, final String method) throws JSONException {
        runReadAndParseJSON = new Runnable() {
            @Override
            public void run() {
                try {
                    submitInsert(jsonObject, method);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(null, runReadAndParseJSON, "bgreadJSONInsertar");
        thread.start();
    }

    private void submitInsert(JSONObject jsonParams, String method) {

        httpClient = new HttpClient(this, _context);

        try {
            httpClient.httpRequestJson(jsonParams, method, false, HttpClient.TypeHttpRequest.TYPE_POST, "");
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    //updte notification
    public void update(final JSONObject jsonObject, final String method, final String id) throws JSONException {
        runReadAndParseJSON = new Runnable() {
            @Override
            public void run() {
                try {
                    submitUpdate(jsonObject, method, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(null, runReadAndParseJSON, "bgreadJSONInsertar");
        thread.start();
    }

    private void submitUpdate(JSONObject jsonParams, String method, String id) {

        httpClient = new HttpClient(this, _context);

        try {
            httpClient.httpRequestJson(jsonParams, method, false, HttpClient.TypeHttpRequest.TYPE_PUT, id);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    //deleteNotification
    public void delete(final JSONObject jsonObject, final String method, final String id) throws JSONException {
        runReadAndParseJSON = new Runnable() {
            @Override
            public void run() {
                try {
                    submitDelete(jsonObject, method, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(null, runReadAndParseJSON, "JsonDelete");
        thread.start();
    }

    private void submitDelete(JSONObject jsonParams, String method, String id) {

        httpClient = new HttpClient(this, _context);

        try {
            httpClient.httpRequestJson(jsonParams, method, false, HttpClient.TypeHttpRequest.TYPE_DELETE, id);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    //override methods of httpInterface
    @Override
    public void onProgress(long progress) {

    }

    @Override
    public void onSuccess(String method, JSONObject response) {
        try {
            switch (method) {

                case "POST":
                    SyncNotification.responseJsonPost(response);
                    break;
                case "PUT":
                    SyncNotification.responseJsonPut(response);
                    break;
                case "DELETE":
                    SyncNotification.responseJsonDelete(response);
                    break;

            }
        } catch (JSONException jsonEx) {
            jsonEx.printStackTrace();
        }

    }

    @Override
    public void onSuccess(String method, JSONArray response) {

    }

    @Override
    public void onFailed(String method, JSONObject errorResponse) {
        System.out.println("ocurrio un error enviando los datos");

    }
}