package android.example.easyelectionapp;

public class Notifications {

    String roomId;
    String senderId;
    String senderName;
    String id;

    public Notifications(String roomId, String senderId, String senderName, String id) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }
}
