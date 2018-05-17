package com.adans.app_10;

import android.app.Application;

/**
 * Created by eap_0 on 01/02/2018.
 */

public class GlobalVars extends Application{
    private String name, email, pass, birth,gender,country, hours_ex, image;
    private int deviceS, deviceH, deviceB, days_ex, ex_int, weight, height;


    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name=name;
    }
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public String getPass(){
        return this.pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    public String getBirth(){
        return this.birth;
    }
    public void setBirth(String birth) {
        this.birth = birth;
    }
    public String getGender(){
        return this.gender;
    }
    public void setGender(String gender){
        this.gender=gender;
        if(gender.equals("1")){
            this.gender="Male";
        }
        if(gender.equals("2")){
            this.gender="Female";
        }
    }
    public String getCountry(){
        return this.country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public int getDeviceS() {
        return deviceS;
    }

    public void setDeviceS(int deviceS) {
        this.deviceS = deviceS;
    }

    public int getDeviceH() {
        return deviceH;
    }

    public void setDeviceH(int deviceH) {
        this.deviceH = deviceH;
    }

    public int getDeviceB() {
        return deviceB;
    }

    public void setDeviceB(int deviceB) {
        this.deviceB = deviceB;
    }

    public int getDays_ex() {
        return days_ex;
    }

    public void setDays_ex(int days_ex) {
        this.days_ex = days_ex;
    }

    public String getHours_ex() {
        return hours_ex;
    }

    public void setHours_ex(String hours_ex) {
        this.hours_ex = hours_ex;
    }

    public int getEx_int() {
        return ex_int;
    }

    public void setEx_int(int ex_int) {
        this.ex_int = ex_int;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    public String getImage() {
        return this.image;
    }
    public void setImage(String image) {
        this.image=image;
    }

}
