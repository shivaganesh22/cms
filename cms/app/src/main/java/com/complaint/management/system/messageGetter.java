package com.complaint.management.system;

public class messageGetter {
    String senderId;
    String timeSent;
    String message;
    public  messageGetter(){

    }

    public messageGetter(String senderId, String timeSent, String message) {
        this.senderId = senderId;
        this.timeSent = timeSent;
        this.message = message;
    }

    public String getSenderId() {

        return senderId;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public String getMessage() {
        return message;
    }
}
