package com.developerali.wbjecaclasses.Models;

public class NotificationModel {

    String type, notificationId;
    long notifyAt;
    Boolean seen;

    public NotificationModel(String type, long notifyAt, Boolean seen) {
        this.type = type;
        this.notifyAt = notifyAt;
        this.seen = seen;
    }

    public NotificationModel() {
    }

    public long getNotifyAt() {
        return notifyAt;
    }

    public void setNotifyAt(long notifyAt) {
        this.notifyAt = notifyAt;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}
