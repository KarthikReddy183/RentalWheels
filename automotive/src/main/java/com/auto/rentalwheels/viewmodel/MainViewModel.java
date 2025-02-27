package com.auto.rentalwheels.viewmodel;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.car.Car;
import android.car.VehicleAreaType;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModel;

import com.auto.rentalwheels.common.FireBaseDbRepository;
import com.auto.rentalwheels.data.UserDetails;
import com.auto.rentalwheels.ui.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainViewModel extends ViewModel {
    private Car car;
    private CarPropertyManager carPropertyManager;
    private FireBaseDbRepository fireBaseDbRepository = new FireBaseDbRepository();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();



    public void initCar(Context context) {
        car = Car.createCar(context, null, Car.CAR_WAIT_TIMEOUT_WAIT_FOREVER, (car, ready) -> {
            if (!ready) {
                Log.w("CarHelper", "Car service is not ready");
                return;
            }
            carPropertyManager = (CarPropertyManager) car.getCarManager(Car.PROPERTY_SERVICE);
        });
    }

    //Added this as in real time we use this VHAL property to read the speed of the vehicle
    public String getCarSpeedPropertyValue() {
        try {
            CarPropertyValue<Object> value = (carPropertyManager.getProperty(VehiclePropertyIds.PERF_VEHICLE_SPEED, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL));
            Log.i("Data", value.getValue().toString());
            return value.getValue().toString();
        } catch (Exception e) {
            Log.i("IllegalArgumentException", e.toString());
            return null;
        }
    }

    public void speedLimit(String userId, Context context) {
        int currentSpeed = Integer.parseInt(getCarSpeedPropertyValue());
        int maxSpeedLimit = 120;

        if (currentSpeed > maxSpeedLimit) {
            speedAlertDialog(context);
            sendNotification(userId, context);
        }
    }


    private void sendNotification(String title, Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String channelId = "fcm_default_channel";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setContentTitle(title)
                        .setContentText("Your car has exceeded the speed limit")
                        .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) (context.getSystemService(NOTIFICATION_SERVICE));
        NotificationChannel channel = new NotificationChannel(channelId,
                "Channel",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public void speedAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Warning!! Maximum speed exceeded")
                .setCancelable(false)
                .setPositiveButton("OK", (DialogInterface dialog, int id) -> {
                    dialog.cancel();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
