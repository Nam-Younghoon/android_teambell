package com.example.teambell_3;

public class RecordData {
    private String RDate;
    private String RDistance;
    private String RTime;
    private String RSpeed;
    private String RDep;
    private String RArr;
    private String RIdx;
    private String RGpx;

    public RecordData(String RDate, String RDistance, String RTime, String RSpeed, String RDep, String RArr, String idx, String gpx){
        this.RDate = RDate;
        this.RDistance = RDistance;
        this.RTime = RTime;
        this.RSpeed = RSpeed;
        this.RDep = RDep;
        this.RArr = RArr;
        this.RIdx = idx;
        this.RGpx = gpx;
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

    public String getRDep(){ return  this.RDep;}

    public String getRArr(){
        return this.RArr;
    }

    public String getRIdx(){
        return this.RIdx;
    }

    public String getRGpx(){return this.RGpx;}
}
