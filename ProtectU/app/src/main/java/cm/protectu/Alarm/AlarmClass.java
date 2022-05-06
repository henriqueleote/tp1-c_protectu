package cm.protectu.Alarm;

import java.util.Date;

public class AlarmClass {
    private boolean alarm;
    private Date time;
    private String message;
    private String subMessage;

    public AlarmClass(){}

    public AlarmClass(boolean alarm, Date time, String message, String subMessage) {
        this.alarm = alarm;
        this.time = time;
        this.message = message;
        this.subMessage = subMessage;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubMessage() {
        return subMessage;
    }

    public void setSubMessage(String subMessage) {
        this.subMessage = subMessage;
    }
}
