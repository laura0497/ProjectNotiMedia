package com.delarosa.notimedia.model.Entitys;

import org.json.JSONObject;

public class ServiceRest {

    private JSONObject jsonParams;
    private String metodo;
    private boolean isAsync;
    private int type_request;
    private String url;
    private String message;
    private int id;

    public ServiceRest() {
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean async) {
        isAsync = async;
    }

    public JSONObject getJsonParams() {
        return jsonParams;
    }

    public void setJsonParams(JSONObject jsonParams) {
        this.jsonParams = jsonParams;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public int getType_request() {
        return type_request;
    }

    public void setType_request(int type_request) {
        this.type_request = type_request;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
