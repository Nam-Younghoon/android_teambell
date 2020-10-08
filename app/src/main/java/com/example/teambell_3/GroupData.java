package com.example.teambell_3;

public class GroupData {
    private String GTitle;
    private String GLocation;
    private String GLeader;
    private String GDate;
    private int GNumber;
    private String GDistance;

    public GroupData(String GTitle, int GNumber, String GLocation, String GDistance, String GLeader, String GDate){
       this.GTitle = GTitle;
       this.GNumber = GNumber;
       this.GLocation = GLocation;
       this.GDistance = GDistance;
       this.GLeader = GLeader;
       this.GDate = GDate;
    }

    public String getGTitle(){
        return this.GTitle;
    }

    public int getGNumber(){ return this.GNumber;}

    public String getGLocation(){
        return  this.GLocation;
    }

    public String getGDistance(){
        return this.GDistance;
    }

    public String getGLeader(){ return this.GLeader;}

    public String getGDate(){ return this.GDate;}
}

