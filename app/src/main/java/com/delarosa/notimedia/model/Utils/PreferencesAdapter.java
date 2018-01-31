package com.delarosa.notimedia.model.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesAdapter {

    Context mContext;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public PreferencesAdapter(Context context) {

        mContext = context;
        preferences = mContext.getSharedPreferences("configuracion", Context.MODE_PRIVATE);
    }

    public String getPreferenceString(String key) {
        String valor = preferences.getString(key, "");
        return valor;

    }

    public int getPreferenceInt(String key) {
        int valor = preferences.getInt(key, 0);
        return valor;

    }

    public void setPreferenceString(String key, String valor) {
        editor = preferences.edit();
        editor.putString(key, valor);
        editor.commit();

    }

    public void setPreferenceInt(String key, int valor) {
        editor = preferences.edit();
        editor.putInt(key, valor);
        editor.commit();

    }

}
