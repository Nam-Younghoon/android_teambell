package com.example.teambell_3;

public class GroupData {
    private String GTitle;
    private String GLeader;
    private String GIndex;
    private String GNumber;
    private String GInfo;

    public GroupData(String GTitle, String GNumber, String GLeader, String GIndex, String GInfo){
        this.GTitle = GTitle;
        this.GNumber = GNumber;
        this.GLeader = GLeader;
        this.GIndex = GIndex;
        this.GInfo = GInfo;
    }

    public String getGTitle(){ return this.GTitle; }

    public String getGNumber(){ return this.GNumber;}

    public String getGLeader(){ return this.GLeader;}

    public String getGIndex(){ return this.GIndex; }

    public String getGInfo(){ return this.GInfo; }

}
