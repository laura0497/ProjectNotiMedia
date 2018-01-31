package com.delarosa.notimedia.model.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.delarosa.notimedia.MainActivity;
import com.delarosa.notimedia.R;
import com.delarosa.notimedia.controller.activitys.VideoActivity;
import com.delarosa.notimedia.model.DataBase.DbNotification;
import com.delarosa.notimedia.model.Entitys.Download;
import com.delarosa.notimedia.model.Entitys.NotificationManagerEntity;
import com.delarosa.notimedia.model.Json.SyncNotification;
import com.delarosa.notimedia.model.Utils.PreferencesAdapter;
import com.delarosa.notimedia.model.Utils.UtilClass;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class DownloadService extends IntentService {

    public DownloadService() {
        super("Download Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;
    DbNotification dbNotification;
    PreferencesAdapter preferencesAdapter;
    SyncNotification syncNotification;
    UtilClass utilClass;


    @Override
    protected void onHandleIntent(Intent intent) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

        initDownload();

    }

    // start the download with retrofit
    private void initDownload() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://s3.amazonaws.com/").build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<ResponseBody> request = retrofitInterface.downloadFile();
        try {

            downloadFile(request.execute().body());

        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    //save the file into the device
    private void downloadFile(ResponseBody body) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Arkbox.mp4");
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        onDownloadComplete();
        output.flush();
        output.close();
        bis.close();

    }

    //start the notification
    private void sendNotification(Download download) {

        sendIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        // notificationBuilder.setContentText(getResources().getString(R.string.downloaded)+  String.format("(%d/%d) MB", download.getCurrentFileSize(), download.getTotalFileSize()));
        notificationBuilder.setContentText(getResources().getString(R.string.downloaded) + String.format("(%d)", download.getProgress()) + "%");
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendIntent(Download download) {

        Intent intent = new Intent(MainActivity.MESSAGE_PROGRESS);
        intent.putExtra("Descarga", download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete() {

        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText(getResources().getString(R.string.file_download_complete));
        notificationManager.notify(0, notificationBuilder.build());
        updateNotification();

        PlayVideo();

    }

    //sync the data and get de duration
    private void updateNotification() {
        try {

            InitInstances();

            int id = preferencesAdapter.getPreferenceInt("idNotificationtoUpdtade");
            String InitialHour = dbNotification.getHourNotifications(id);
            int time;
            Date fin = new Date();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date inicio = df.parse(InitialHour);

            long diff = fin.getTime() - inicio.getTime();
            long segundos = diff / 1000;
            long minutos = segundos / 60;

            time = (int) minutos;
            NotificationManagerEntity notification = new NotificationManagerEntity();
            notification.setId(id);
            notification.setDuration(time);
            notification.setDate(utilClass.getDate());
            syncNotification.updateNotification(notification);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //go to the class video to play the downloaded video
    private void PlayVideo() {
        Intent i = new Intent(DownloadService.this, VideoActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void InitInstances() {
        dbNotification = new DbNotification(this);
        preferencesAdapter = new PreferencesAdapter(this);
        syncNotification = new SyncNotification(this);
        utilClass = new UtilClass(this);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}
