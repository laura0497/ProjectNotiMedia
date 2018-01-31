package com.delarosa.notimedia.controller.activitys;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.delarosa.notimedia.MainActivity;
import com.delarosa.notimedia.R;
import com.delarosa.notimedia.controller.fragments.NotificationManagerFragment;
import com.delarosa.notimedia.model.DataBase.DbNotification;
import com.delarosa.notimedia.model.Entitys.BuilderManagerButton;
import com.delarosa.notimedia.model.Entitys.NotificationManagerEntity;
import com.delarosa.notimedia.model.Json.SyncNotification;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.OnBoomListenerAdapter;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationManagerActivity extends AppCompatActivity {
    DbNotification dbNotification;
    SyncNotification syncNotification;
    @BindView(R.id.bmb)
    BoomMenuButton bmb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_notification_manager);
        ButterKnife.bind(this);
        dbNotification = new DbNotification(NotificationManagerActivity.this);
        syncNotification = new SyncNotification(NotificationManagerActivity.this);

        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_3);
        bmb.addBuilder(BuilderManagerButton.getHamButtonBuilder(getResources().getString(R.string.deleteNot), "")
                .normalColor(ContextCompat.getColor(this, R.color.ligth_blue)));
        bmb.addBuilder(BuilderManagerButton.getHamButtonBuilder(getResources().getString(R.string.deleteVideo), "")
                .normalColor(ContextCompat.getColor(this, R.color.colorAccent)));
        bmb.addBuilder(BuilderManagerButton.getHamButtonBuilder(getResources().getString(R.string.home), getResources().getString(R.string.sub_home))
                .normalColor(ContextCompat.getColor(this, R.color.green)));

        bmb.setOnBoomListener(new OnBoomListenerAdapter() {
            @Override
            public void onClicked(int index, BoomButton boomButton) {
                super.onClicked(index, boomButton);
                if (index == 0) {
                    deleteNotificationsPopUp();
                } else if (index == 1) {
                    deleteVideoPopUp();
                } else if (index == 2) {
                    home();
                }

            }
        });

       // syncNotification.getNotification();




        passtoFragment();
    }

    //method to pass to the fragment
    public void passtoFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, NotificationManagerFragment.newInstance()).commit();
    }

    //method to delete the video
    public void deleteVideo() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Arkbox.mp4");
        file.delete();
    }

    //validate the answer of the user and delete
    public void deleteVideoPopUp() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(NotificationManagerActivity.this);
        dialog.setTitle("Atención");
        dialog.setMessage("Eliminar video?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface _dialog, int which) {
                        deleteVideo();
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

    //method to delete all the notifications
    private void deleteNotifications() {
        ArrayList<NotificationManagerEntity> notificationList = dbNotification.getNotifications();
        for (NotificationManagerEntity notification : notificationList) {
            int id = notification.getId();
            int status = notification.getStatus();
            if (status == 2) {
                syncNotification.deleteNotification(id);

                dbNotification.deleteNotification(id);
            }
        }
        passtoFragment();
    }

    //validate the answer of the user and delete
    public void deleteNotificationsPopUp() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(NotificationManagerActivity.this);
        dialog.setTitle("Atención");
        dialog.setMessage("Eliminar Notificaciones?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface _dialog, int which) {
                        deleteNotifications();
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

    //method to go home and do more downloads
    public void home() {
        Intent i = new Intent(NotificationManagerActivity.this, MainActivity.class);
        startActivity(i);
    }


}
