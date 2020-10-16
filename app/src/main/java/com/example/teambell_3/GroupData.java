package com.example.teambell_3;

public class GroupData {
    private String GTitle;
    private String GLeader;
    private String GIndex;
    private String GNumber;

    public GroupData(String GTitle, String GNumber, String GLeader, String GIndex){
        this.GTitle = GTitle;
        this.GNumber = GNumber;
        this.GLeader = GLeader;
        this.GIndex = GIndex;
    }

    public String getGTitle(){ return this.GTitle; }

    public String getGNumber(){ return this.GNumber;}

    public String getGLeader(){ return this.GLeader;}

    public String getGIndex(){ return this.GIndex; }

}
