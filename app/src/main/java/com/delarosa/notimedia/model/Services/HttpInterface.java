package com.delarosa.notimedia.model.Services;

import org.json.JSONArray;
import org.json.JSONObject;

public interface HttpInterface {

    void onProgress(long progress);

    void onSuccess(String method, JSONArray response);

    void onSuccess(String method, JSONObject response);

    void onFailed(String method, JSONObject errorResponse);

}