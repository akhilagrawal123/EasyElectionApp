package android.example.easyelectionapp;

public class Result {

    public String name;
    public String vote;

    public Result(String name, String vote) {
        this.name = name;
        this.vote = vote;
    }

    public String getName() {
        return name;
    }

    public String getVote() {
        return vote;
    }
}

