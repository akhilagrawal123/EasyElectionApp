package android.example.easyelectionapp;

public class MyRooms {

    String roomName;
    String status;

    public MyRooms()
    {

    }

    public MyRooms(String roomName,String status) {
        this.roomName = roomName;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getRoomName() {
        return roomName;
    }
}
