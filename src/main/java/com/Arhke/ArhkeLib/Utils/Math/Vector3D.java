package com.Arhke.ArhkeLib.Utils.Math;

import com.Arhke.ArhkeLib.FileIO.DataManager;
import com.Arhke.ArhkeLib.FileIO.YamlSerializable;

public class Vector3D implements YamlSerializable {
    public double x, y, z;
    public Vector3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }
    public double getZ(){
        return this.z;
    }
    public void setX(double x){
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }
    public void setZ(double z){
        this.z = z;
    }
    public static String xKey = "x", yKey = "y", zKey = "z";
    @Override
    public void load(DataManager dataManager) {
        x = dataManager.getDouble(0, xKey);
        y = dataManager.getDouble(0, yKey);
        z = dataManager.getDouble(0, zKey);
    }

    @Override
    public void write(DataManager dataManager) {
        dataManager.set(x, xKey);
        dataManager.set(y, yKey);
        dataManager.set(z, zKey);
    }
}
