package com.dst.abacustrainner.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.dst.abacustrainner.Activity.AssignmentListActivity;
import com.dst.abacustrainner.Activity.CartActivity;
import com.dst.abacustrainner.Activity.CoursesActivity;
import com.dst.abacustrainner.Activity.PurchasedActivity;
import com.dst.abacustrainner.Activity.TopicListActivity;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.SpalashActivity;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.User.UserCreateActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d("FCM_TOKEN", token);

        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM_TOPIC", "Subscribed to all_users");
                    } else {
                        Log.d("FCM_TOPIC", "Subscription failed");
                    }
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("message");
            String type = remoteMessage.getData().get("notificationFor");

            Intent intent;

            switch (type) {

                case "topic":
                    intent = new Intent(this, TopicListActivity.class);
                    String studentId = remoteMessage.getData().get("studentId");
                    String dateId = remoteMessage.getData().get("dateId");

                    if (studentId != null && dateId != null) {
                        studentId = studentId.replace("'", "").trim(); // 🔥 fix here also
                        dateId = dateId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean studentId: " + studentId);
                    Log.d("FCM_DEBUG", "Clean dateId: " + dateId);

                    intent.putExtra("studentId", studentId);
                    intent.putExtra("dateId", dateId);


                    break;

                case "assignment":
                    intent = new Intent(this, AssignmentListActivity.class);
                    String studentId1 = remoteMessage.getData().get("studentId");
                    String dateId1 = remoteMessage.getData().get("dateId");

                    if (studentId1 != null && dateId1 != null) {
                        studentId1 = studentId1.replace("'", "").trim(); // 🔥 fix here also
                        dateId1 = dateId1.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean studentId: " + studentId1);
                    Log.d("FCM_DEBUG", "Clean dateId: " + dateId1);

                    intent.putExtra("studentId", studentId1);
                    intent.putExtra("dateId", dateId1);


                    break;
                case "subscriptionExpired":
                    intent = new Intent(this, PurchasedActivity.class);
                    String purchasedStudentId = remoteMessage.getData().get("studentId");

                    if (purchasedStudentId != null ) {
                        purchasedStudentId = purchasedStudentId.replace("'", "").trim(); // 🔥 fix here also

                    }
                    Log.d("FCM_DEBUG", "Clean studentId: " + purchasedStudentId);
                    intent.putExtra("studentId", purchasedStudentId);
                    break;

                case "cart":

                    intent = new Intent(this,CartActivity.class);

                    String worksheetRnm = remoteMessage.getData().get("worksheetRnm");

                    if (worksheetRnm != null ) {
                        worksheetRnm = worksheetRnm.replace("'", "").trim(); // 🔥 fix here also

                    }
                    Log.d("FCM_DEBUG", "Clean worksheetRnm: " + worksheetRnm);

                    intent.putExtra("WorkSheetRnm", worksheetRnm );

                    break;

                case "schedules":

                    intent = new Intent(this, HomeActivity.class);

                    String schedulestudentId =
                            remoteMessage.getData().get("studentId");

                    if (schedulestudentId != null) {
                        schedulestudentId = schedulestudentId.replace("'", "").trim();
                    }

                    intent.putExtra("openFragment", "schedules");
                    intent.putExtra("studentId", schedulestudentId);

                    break;

                default:
                    intent = new Intent(this, UserCreateActivity.class);
                    break;
            }

            showNotification(title, body, intent);
        }
    }

        private void showNotification(String title, String body, Intent intent) {


            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String channelId = "fcm_channel";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "FCM Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                manager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, channelId)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setSmallIcon(R.drawable.abacus_logo)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

            manager.notify(0, builder.build());
        }

}
