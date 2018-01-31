package com.delarosa.notimedia.model.DataBase;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.delarosa.notimedia.model.Entitys.NotificationManagerEntity;

import java.util.ArrayList;

public class DbNotification extends Activity {

    public static final String DB_NAME = "NotiMedia.db";
    public static final int DB_VERSION = 1;

    static final String T_NOTIFICATION = "tbl_notification";

    public SQLiteDatabase db;
    Context context;
    private DBHelper dbHelper;


    public DbNotification(Context mContext) {

        context = mContext;
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    public DbNotification open() throws SQLException {
        try {

            db = dbHelper.getWritableDatabase();
            db.execSQL("PRAGMA automatic_index = false;");

        } catch (Exception e) {
            Log.d("Depuracion", "Error al abrir la base de datos " + e);
            dbHelper.close();
        }
        return this;
    }

    //get all the notifications
    public ArrayList<NotificationManagerEntity> getNotifications() {
        ArrayList<NotificationManagerEntity> notificationList = new ArrayList<>();
        Cursor cursor;
        try {
            open();
            cursor = db.rawQuery("select * from tbl_notification", null);

            if (cursor.moveToFirst()) {
                do {
                    NotificationManagerEntity notification = new NotificationManagerEntity();
                    notification.setId(cursor.getInt(0));
                    notification.setDuration(cursor.getInt(1));
                    notification.setDate(cursor.getString(2));
                    notification.setStatus(cursor.getInt(3));

                    notificationList.add(notification);
                } while (cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notificationList;
    }

    //get hour of the notification selected
    public String getHourNotifications(int id) {
        String date = "";
        try {
            open();
            Cursor c = db.rawQuery("select date from " + T_NOTIFICATION + " where id='" + id + "' ", null);
            if (c.moveToFirst()) {
                date = c.getString(0);
            }
            if (!c.isClosed()) {
                c.close();
            }
        } catch (SQLException ex) {

            Log.d("Base de Datos", "getHourNotifications: " + ex.toString());
        }
        return date;
    }

    //save the notification into the db
    public void saveNotification(NotificationManagerEntity notification) {

        ContentValues newValues = new ContentValues();
        newValues.put("id", notification.getId());
        newValues.put("duration", notification.getDuration());
        newValues.put("date", notification.getDate());
        newValues.put("status", notification.getStatus());
        try {
            open();
            db.insert(T_NOTIFICATION, null, newValues);
            String sql = "select id from " + T_NOTIFICATION + " order by id desc limit 1";


        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }
    }

    //update the notification into the db
    public void updateNotification(NotificationManagerEntity notification) {

        ContentValues newValues = new ContentValues();
        newValues.put("id", notification.getId());
        newValues.put("duration", notification.getDuration());
        newValues.put("date", notification.getDate());
        newValues.put("status", notification.getStatus());

        try {

            open();
            db.update(T_NOTIFICATION, newValues, "id=" + notification.getId(), null);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }

    }

    //delete the notification into the db
    public void deleteNotification(int id) {
        try {
            open();
            db.delete(T_NOTIFICATION, "id=" + id, null);
        } catch (SQLException ex) {
            Log.i("Base de datos: ", " deleteNotification " + ex);
        } finally {
            db.close();
        }
    }


    static final String CREATE_TABLE_NOTIFICATION = "create table if not exists "
            + T_NOTIFICATION
            + "("
            + "id                           INTEGER ,"
            + "duration                     INTEGER,"
            + "date                         TEXT,"
            + "status                       INTEGER);";


}
