package nz.co.redice.azansilenttime.services.notification_service;

import androidx.core.app.NotificationCompat;

import io.reactivex.subjects.BehaviorSubject;
import nz.co.redice.azansilenttime.services.alarm_service.OnNewAlarmListener;

public interface NotificationService extends OnNewAlarmListener {
    NotificationCompat.Builder getNotificationBuilder();

    BehaviorSubject<String> getNextAlarmTextTimeBehaviorSubject();

}
