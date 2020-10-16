package com.example.teambell_3;

public class GroupLocationData {
    private String groupIdx;
    private String name;
    private String userIdx;
    private String latitude;
    private String longitude;

    public GroupLocationData(String groupIdx, String name, String latitude, String longitude){
        this.groupIdx = groupIdx;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getGroupIdx(){ return this.groupIdx; }

    public String getName(){ return this.name;}

    public String getLatitude(){ return this.latitude; }

    public String getLongitude(){ return this.longitude; }

}
