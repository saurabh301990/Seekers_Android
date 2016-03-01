package com.tv.seekers.pushnotification;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.tv.seekers.R;
import com.tv.seekers.activities.PostDetailsTextImg;
import com.tv.seekers.menu.MainActivity;

import java.util.List;


/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {

    public static final String TAG = "PushNotifTest";
    private String senderID;
    private String date;
    private String msgID;
    private String type = "";
    private String _badgeCount = "";
    private String alert = "";
    private String message = "";
    private String postId = "";
    private String lang = "";
    private boolean inForground= false;
    Bundle extras;


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            /*
             * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */

            if (messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
                Log.i(TAG, "**** Push Notification Received from GCM *****");
                Log.i(TAG, "Received: " + extras.toString());

                String notifiction_id = extras.getString("notification_id");
                String keyword = extras.getString("keyword");
                String game_id = extras.getString("game_id");
                String key = extras.getString("key");


                message = extras.getString("message");
                alert = extras.getString("alert");
                type = extras.getString("type");
                postId = extras.getString("postId");


                ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
                if (tasks != null) {
                    for (ActivityManager.RunningAppProcessInfo appProcess : tasks) {
                        String s = appProcess.processName;


                        if (appProcess.importance ==
                                ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                                && appProcess.processName.contains("com.tv.seekers")) {
                            inForground = true;

                            if (extras.containsKey("type")) {
                                if (type != null & !type.equalsIgnoreCase("")) {
                                    if (extras.containsKey("badge") && type.equalsIgnoreCase("UsePromotion")) {
                                        _badgeCount = extras.getString("badge");


                                    } else {

                                    }



                                    }


                            }
//                         GcmCommunicator.getInstance().handle(message,notifiction_id,keyword,key,game_id);

//                            break;
                        } else {
                            inForground = false;
                        }


                        sendNotification("Seeker Notifications", alert, message);


                        break;
                        // }
                    }
                }
            } else {
                sendNotification("ERROR", "Notif ERROR", "Something is wrong, but I don't know what it is!");
            }
        }


        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /**
     * Puts a notification as example.
     */
    private void sendNotification(String ticker, String title, String text) {
        int notifId = (int) (Math.random() * 1000) + 1000;
        // Constant.notiID.add(notifId);

        Intent i = null;
        i = new Intent(this, PostDetailsTextImg.class);
        i.putExtra("POSTID",postId);

//		////System.out.println("From Notification");
        PendingIntent contentIntent = PendingIntent.getActivity(this, notifId, i,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.app_icon).setTicker(ticker).setContentTitle(title)
                .setContentText(text).setAutoCancel(true).setSound(Settings.
                        System.DEFAULT_NOTIFICATION_URI)
                .setLights(Color.BLUE, 3000, 3000).setContentIntent(contentIntent);

        NotificationManager notifManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notifManager.notify(notifId, notifBuilder.build());

    }

    // ////////////////clear all notification from bar
    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

}
