package com.example.teambell_3;

public class RecordData {
    private String RDate;
    private String RDistance;
    private String RTime;
    private String RSpeed;

    public RecordData(String RDate, String RDistance, String RTime, String RSpeed){
        this.RDate = RDate;
        this.RDistance = RDistance;
        this.RTime = RTime;
        this.RSpeed = RSpeed;
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
}
