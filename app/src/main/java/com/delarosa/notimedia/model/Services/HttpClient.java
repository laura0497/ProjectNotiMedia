package com.delarosa.notimedia.model.Services;

import android.content.Context;
import android.util.Log;

import com.delarosa.notimedia.R;
import com.delarosa.notimedia.model.Entitys.ServiceRest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class HttpClient {

    HttpInterface mHttpInterface;
    Context mContext;
    String mMethod;

    public HttpClient(HttpInterface httpInterface, Context context) {
        mHttpInterface = httpInterface;
        mContext = context;
    }

    public void httpRequestJson(JSONObject jsonParams, final String method, boolean isAsync, int type_request, String id) {

        Log.i("Depuracion", "RequestParams " + jsonParams.toString());

        mMethod = method;
        String url = mContext.getResources().getString(R.string.url) + id;
        Log.i("Url", "Metodo a sincronizar " + url);
        AsyncHttpClient client;
        if (isAsync) {
            client = new AsyncHttpClient();
        } else {
            client = new SyncHttpClient();
        }
        client.setTimeout(20000);
        client.addHeader("Authorization", "Basic 1017249698");
        StringEntity entity = null;
        StringEntity nullEntity = null;
        entity = new StringEntity(jsonParams.toString(), "UTF-8");

        switch (type_request) {
            case TypeHttpRequest.TYPE_POST:
                Log.i("Depuracion", "TYPE_POST");
                client.post(mContext, url, entity, "application/json", jsonHttpResponseHandler);
                break;
            case TypeHttpRequest.TYPE_GET:
                Log.i("Depuracion", "TYPE_GET");
                client.get(mContext, url, nullEntity, "application/json", jsonHttpResponseHandler);
                break;
            case TypeHttpRequest.TYPE_PUT:
                Log.i("Depuracion", "TYPE_PUT");
                client.put(mContext, url, entity, "application/json", jsonHttpResponseHandler);
                break;
            case TypeHttpRequest.TYPE_DELETE:
                Log.i("Depuracion", "TYPE_DELETE");
                client.delete(mContext, url, nullEntity, "application/json", jsonHttpResponseHandler);
                break;
            case TypeHttpRequest.TYPE_PATCH:
                Log.i("Depuracion", "TYPE_PATCH");
                client.patch(mContext, url, entity, "application/json", jsonHttpResponseHandler);
                break;
        }
    }

    public void httpRequestJson(ServiceRest serviceRest) {

        Log.i("Depuracion", "RequestParams " + serviceRest.getJsonParams().toString());

        mMethod = serviceRest.getMetodo();

        String url = serviceRest.getUrl();

        Log.i("Url", "Metodo a sincronizar " + url);

        AsyncHttpClient client;
        if (serviceRest.isAsync()) {
            client = new AsyncHttpClient();
        } else {
            client = new SyncHttpClient();
        }
        client.setTimeout(20000);
        client.addHeader("Authorization", "Basic 1017249698");
        StringEntity entity = null;
        StringEntity nullentity = null;
        entity = new StringEntity(serviceRest.getJsonParams().toString(), "UTF-8");

        switch (serviceRest.getType_request()) {
            case TypeHttpRequest.TYPE_POST:
                Log.i("Depuracion", "TYPE_POST");
                client.post(mContext, url, entity, "application/json", jsonHttpResponseHandler);
                break;
            case TypeHttpRequest.TYPE_GET:
                Log.i("Depuracion", "TYPE_GET");
                client.get(mContext, url, nullentity, "application/json", jsonHttpResponseHandler);
                break;
            case TypeHttpRequest.TYPE_PUT:
                Log.i("Depuracion", "TYPE_PUT");
                client.put(mContext, url, (HttpEntity) entity, "application/json", jsonHttpResponseHandler);
                break;
            case TypeHttpRequest.TYPE_DELETE:
                Log.i("Depuracion", "TYPE_DELETE");
                client.delete(mContext, url, (HttpEntity) entity, "application/json", jsonHttpResponseHandler);
                break;
            case TypeHttpRequest.TYPE_PATCH:
                Log.i("Depuracion", "TYPE_PATCH");
                client.patch(mContext, url, (HttpEntity) entity, "application/json", jsonHttpResponseHandler);
                break;
        }
    }

    JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            long progressPercentage = 100 * bytesWritten / totalSize;
            mHttpInterface.onProgress(progressPercentage);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.i("Depuracion", "errorResponse " + errorResponse + " throwable " + throwable);
            mHttpInterface.onFailed(mMethod, errorResponse);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.i("Depuracion", "response " + response);
            mHttpInterface.onSuccess(mMethod, response);
        }

        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i("Depuracion", "response " + response);
            mHttpInterface.onSuccess(mMethod, response);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            Log.i("Depuracion", "Error " + statusCode + " responseString " + responseString);
        }
    };

    public static abstract class TypeHttpRequest {
        public static final int TYPE_POST = 1;
        public static final int TYPE_GET = 2;
        public static final int TYPE_PUT = 3;
        public static final int TYPE_DELETE = 4;
        public static final int TYPE_PATCH = 5;
    }
}
