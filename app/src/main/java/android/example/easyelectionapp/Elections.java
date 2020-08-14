package android.example.easyelectionapp;

public class Elections {

    String id;
    String roomId;
    String ownerId;
    String startTime;
    String endTime;
    String title;
    String purpose;

    public Elections()
    {
        //elections construction needed.
    }

    public Elections(String id, String roomId, String ownerId, String startTime, String endTime, String title, String purpose) {
        this.id = id;
        this.roomId = roomId;
        this.ownerId = ownerId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.purpose = purpose;
    }

    public String getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getTitle() {
        return title;
    }

    public String getPurpose() {
        return purpose;
    }
}
