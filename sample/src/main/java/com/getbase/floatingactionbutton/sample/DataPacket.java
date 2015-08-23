package com.getbase.floatingactionbutton.sample;

/**
 * Created by sidharthvinod on 23/08/15.
 */
public class DataPacket{
    public String lat,lon,date,dt,speed,image,type,status,repid,severity;

    public DataPacket(String... x){
        lat=x[0];
        lon=x[1];
        date=x[2].replaceAll(" ","_");//.replaceAll(":","_");
        dt=x[3];
        speed=x[4];
        image=x[5];
        type=x[6];
        status=x[7];
        repid=x[8];
        severity=x[9];

    }

}
