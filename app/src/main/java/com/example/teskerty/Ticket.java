package com.example.teskerty;

public class Ticket {
    private int id;
    private String stadiumName;
    private String matchTime;
    private String leftLogo;
    private String rightLogo;

    // Constructor
    public Ticket(int id, String stadiumName, String matchTime, String leftLogo, String rightLogo) {
        this.id = id;
        this.stadiumName = stadiumName;
        this.matchTime = matchTime;
        this.leftLogo = leftLogo;
        this.rightLogo = rightLogo;
    }

    // Getter and Setter for ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for Stadium Name
    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    // Getter and Setter for Match Time
    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    // Getter and Setter for Left Logo
    public String getLeftLogo() {
        return leftLogo;
    }

    public void setLeftLogo(String leftLogo) {
        this.leftLogo = leftLogo;
    }

    // Getter and Setter for Right Logo
    public String getRightLogo() {
        return rightLogo;
    }

    public void setRightLogo(String rightLogo) {
        this.rightLogo = rightLogo;
    }
}
