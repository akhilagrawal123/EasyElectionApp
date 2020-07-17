package android.example.easyelectionapp;

import android.widget.ImageView;

public class AllUsers {

    String userName;
    ImageView profilepic;

    public AllUsers()
    {
        //empty constructor needed.........
    }

    public AllUsers(String userName, ImageView profilepic) {
        this.userName = userName;
        this.profilepic = profilepic;
    }

    public String getUserName() {
        return userName;
    }

    public ImageView getProfilepic() {
        return profilepic;
    }
}
