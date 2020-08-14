package android.example.easyelectionapp;

public class Members {

    String name;
    String profilePic;
    String id;

    public Members()
    {
        //empty constructor needed.........
    }


    public Members(String name, String profilePic, String id) {
        this.name = name;
        this.profilePic = profilePic;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getId() {
        return id;
    }
}
