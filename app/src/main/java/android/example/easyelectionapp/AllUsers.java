package android.example.easyelectionapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class AllUsers implements Parcelable {

    String userName;
    String profilePic;
    String uid;

    public AllUsers()
    {
        //empty constructor needed.........
    }

    public AllUsers(String userName, String profilePic, String uid) {
        this.userName = userName;
        this.profilePic = profilePic;
        this.uid = uid;
    }

    protected AllUsers(Parcel in) {
        userName = in.readString();
        profilePic = in.readString();
        uid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(profilePic);
        dest.writeString(uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AllUsers> CREATOR = new Creator<AllUsers>() {
        @Override
        public AllUsers createFromParcel(Parcel in) {
            return new AllUsers(in);
        }

        @Override
        public AllUsers[] newArray(int size) {
            return new AllUsers[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getUid() {
        return uid;
    }
}
