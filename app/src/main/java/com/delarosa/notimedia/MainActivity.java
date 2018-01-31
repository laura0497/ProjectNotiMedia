package com.delarosa.notimedia;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delarosa.notimedia.controller.activitys.NotificationManagerActivity;
import com.delarosa.notimedia.model.DataBase.DbNotification;
import com.delarosa.notimedia.model.Entitys.Download;
import com.delarosa.notimedia.model.Entitys.NotificationManagerEntity;
import com.delarosa.notimedia.model.Json.SyncNotification;
import com.delarosa.notimedia.model.Services.DownloadService;
import com.delarosa.notimedia.model.Utils.PreferencesAdapter;
import com.delarosa.notimedia.model.Utils.UtilClass;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    public static final String MESSAGE_PROGRESS = "message_progress";
    private static final int PERMISSION_REQUEST_CODE = 1;
    DbNotification dbNotification;
    PreferencesAdapter preferencesAdapter;
    SyncNotification syncNotification;
    UtilClass utilClass;

    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.progress_text)
    TextView mProgressText;
    @BindView(R.id.btn_download)
    Button btn_download;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initInstances();
        btn_download.setEnabled(true);
        registerReceiver();
    }

    private void initInstances() {
        dbNotification = new DbNotification(MainActivity.this);
        preferencesAdapter = new PreferencesAdapter(MainActivity.this);
        syncNotification = new SyncNotification(MainActivity.this);
        utilClass = new UtilClass(MainActivity.this);
    }

    @OnClick(R.id.btn_download)
    public void downloadFile() {

        if (checkPermission()) {
            startDownload();
        } else {
            requestPermission();
        }
    }

    @OnClick(R.id.btn_manager)
    public void Manager() {
        Intent i = new Intent(MainActivity.this, NotificationManagerActivity.class);
        startActivity(i);
    }

    private void startDownload() {
        saveNotification();
        btn_download.setEnabled(false);
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
    }

    //sync the notification in bd and in server
    private void saveNotification() {
        NotificationManagerEntity notificationManager = new NotificationManagerEntity();
        notificationManager.setDuration(Integer.parseInt(getResources().getString(R.string.initialDuration)));
        notificationManager.setDate(utilClass.getDate());
        notificationManager.setStatus(1);
        syncNotification.SaveNotification(notificationManager);
    }

    private void registerReceiver() {

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MESSAGE_PROGRESS)) {

                Download download = intent.getParcelableExtra("Descarga");
                mProgressBar.setProgress(download.getProgress());
                if (download.getProgress() == 100) {

                    mProgressText.setText(getResources().getString(R.string.file_download_complete));

                } else {
                    // if wants to show the mb of the file
                    // mProgressText.setText(getResources().getString(R.string.downloaded)+String.format(" (%d/%d) MB", download.getCurrentFileSize(), download.getTotalFileSize(),download));
                    mProgressText.setText(getResources().getString(R.string.downloaded) + String.format(" (%d)", download.getProgress()) + " %");

                }
            }
        }
    };


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
        //permission to save the file in the device
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    //result of the answer of the user
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), getResources().getString(R.string.permission_denied), Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

}
