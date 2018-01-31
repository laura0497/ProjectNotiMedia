package com.delarosa.notimedia.controller.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delarosa.notimedia.R;
import com.delarosa.notimedia.controller.adapters.NotificationManagerAdapter;
import com.delarosa.notimedia.model.DataBase.DbNotification;
import com.delarosa.notimedia.model.Entitys.NotificationManagerEntity;
import com.delarosa.notimedia.model.Entitys.ServiceRest;
import com.delarosa.notimedia.model.Json.SyncNotification;
import com.delarosa.notimedia.model.Services.HttpClient;
import com.delarosa.notimedia.model.Services.HttpInterface;
import com.delarosa.notimedia.model.Utils.UtilClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NotificationManagerFragment extends Fragment implements NotificationManagerAdapter.ClickListener, HttpInterface {

    ArrayList<NotificationManagerEntity> notificationManagerEntityArrayList;
    NotificationManagerAdapter notificationManagerAdapter;
    View rootView;
    DbNotification dbNotification;
    SyncNotification syncNotification;
    UtilClass utilClass;

    @BindView(R.id.RvNotification)
    RecyclerView RvNotification;
    @BindView(R.id.swipeRefreshLayoutNotification)
    SwipeRefreshLayout refreshLayout;

    public static NotificationManagerFragment newInstance() {
        return new NotificationManagerFragment();
    }

    public NotificationManagerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManagerEntityArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_notification_manager, container, false);
        ButterKnife.bind(this, rootView);
        dbNotification = new DbNotification(rootView.getContext());
        syncNotification = new SyncNotification(rootView.getContext());
        utilClass = new UtilClass(rootView.getContext());
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new HackingBackgroundTask().execute();
                    }
                }
        );

         getNotificationFromServer();
        return rootView;
    }

    //ask for the data to the server
    private void getNotificationFromServer() {
        if (utilClass.checkInternet(rootView.getContext())) {
            ServiceRest serviceRest = new ServiceRest();
            JSONObject jsonParams = new JSONObject();
            serviceRest.setJsonParams(jsonParams);
            serviceRest.setType_request(HttpClient.TypeHttpRequest.TYPE_GET);
            serviceRest.setAsync(true);
            serviceRest.setMetodo("GET");
            serviceRest.setUrl(getResources().getString(R.string.url));
            try {
                consumeService(serviceRest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            utilClass.AlertNoInternet(rootView.getContext());
        }

    }

    public void consumeService(ServiceRest serviceRest) throws JSONException {
        HttpClient httpClient;
        httpClient = new HttpClient(this, rootView.getContext());
        try {
            httpClient.httpRequestJson(serviceRest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get the data
    public void listNotification() {
        try {


            notificationManagerEntityArrayList = dbNotification.getNotifications();
            if (notificationManagerEntityArrayList.size() == 0) {
                showPopUpEmpty();
            }
            notificationManagerAdapter = new NotificationManagerAdapter(notificationManagerEntityArrayList, this, getActivity().getApplicationContext());
            RvNotification.setAdapter(notificationManagerAdapter);
            RvNotification.setHasFixedSize(true);
            RvNotification.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            RvNotification.setItemAnimator(new DefaultItemAnimator());
            notificationManagerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //method to show a pop-up if there aren´t data
    private void showPopUpEmpty() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(rootView.getContext());
        dialog.setTitle(getResources().getString(R.string.tittle_dialog));
        dialog.setMessage(getResources().getString(R.string.message_dialog));
        dialog.setCancelable(false);
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface _dialog, int which) {
                        _dialog.dismiss();
                    }
                });
        dialog.show();

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //delete just the notification selected
    @Override
    public void itemClick(View view, int position) {
        final int id = notificationManagerEntityArrayList.get(position).getId();
        AlertDialog.Builder dialog = new AlertDialog.Builder(rootView.getContext());
        dialog.setTitle("Atención");
        dialog.setMessage("Eliminar notificación?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface _dialog, int which) {
                        syncNotification.deleteNotification(id);
                        dbNotification.deleteNotification(id);
                        listNotification();
                        _dialog.dismiss();
                    }
                });
        dialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface _dialog, int which) {
                        _dialog.dismiss();
                    }
                });
        dialog.show();


    }

    @Override
    public void onProgress(long progress) {

    }

    //get the data from the server and show the list of them
    @Override
    public void onSuccess(String method, JSONArray response) {
        try {
            JSONArray jsonArray = response;
            ArrayList<NotificationManagerEntity> listNotification;
            listNotification = new ArrayList<>();


            if (jsonArray.length() > 0) {
                for (int x = 0; x < jsonArray.length(); x++) {

                    JSONObject jsonNotifica = jsonArray.getJSONObject(x);
                    NotificationManagerEntity notificationManager = new NotificationManagerEntity();

                    notificationManager.setId(jsonNotifica.getInt("NotificationId"));
                    notificationManager.setDate(jsonNotifica.getString("Date"));
                    notificationManager.setDuration(jsonNotifica.getInt("Duration"));
                    if (jsonNotifica.getInt("Duration") == Integer.parseInt(getResources().getString(R.string.initialDuration))) {
                        notificationManager.setStatus(1);
                    } else {
                        notificationManager.setStatus(2);
                    }

                    listNotification.add(notificationManager);
                    dbNotification.updateNotification(notificationManager);

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listNotification();
    }

    @Override
    public void onSuccess(String method, JSONObject response) {

    }

    @Override
    public void onFailed(String method, JSONObject errorResponse) {

    }

    //refresh the recyclerview swiping
    private class HackingBackgroundTask extends AsyncTask<Void, Void, List<NotificationManagerEntity>> {

        static final int DURACION = 3 * 1000; // 3 segundos de carga

        @Override
        protected List doInBackground(Void... params) {
            try {
                Thread.sleep(DURACION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return notificationManagerEntityArrayList;
        }

        @Override
        protected void onPostExecute(List result) {
            super.onPostExecute(result);

            notificationManagerAdapter.clear();
            listNotification();
            refreshLayout.setRefreshing(false);
        }
    }

}
