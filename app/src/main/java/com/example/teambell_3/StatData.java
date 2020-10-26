package com.example.teambell_3;

public class StatData {
    private String RDate;
    private String RDistance;
    private String RTime;
    private String RSpeed;
    private String RCount;


    public StatData(String RDate, String RDistance, String RTime, String RSpeed, String RCount){
        this.RDate = RDate;
        this.RDistance = RDistance;
        this.RTime = RTime;
        this.RSpeed = RSpeed;
        this.RCount = RCount;
    }

    public String getRDate(){
        return this.RDate;
    }

    public String getRDistance(){
        return this.RDistance;
    }

    public String getRTime(){
        return  this.RTime;
    }

    public String getRSpeed(){
        return this.RSpeed;
    }

    public String getRCount(){ return this.RCount; }
}