package com.example.behealthy_java;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class User implements Serializable {
    public String gender, name, password, activityName, purposeName;
    public Integer ActivityNum, PurposeNum, UserID, age, weight, height;
    private double FatsNorm;
    private double ProteinsNorm;
    private double CarboNorm;
    private double CaloriesNorm;
    private Integer BMR;
    public transient SQLiteDatabase db;

    public String getUserName(){
        return (this.name);
    }

    public String getGender(){ return (this.gender); }

    public Integer getAge(){
        return (this.age);
    }

    public Integer getWeight(){
        return (this.weight);
    }

    public Integer getHeight(){
        return (this.height);
    }

    public String getActivity(){ return (this.activityName); }

    public String getPurpose(){ return (this.purposeName); }

    public void setGender(){
        String select_string = "SELECT Gender FROM USERS_INFO WHERE _id='" + this.UserID +"'";
        Cursor c2 = db.rawQuery(select_string, null);
        while(c2.moveToNext()){
            this.gender = c2.getString(c2.getColumnIndexOrThrow("Gender"));
        }
    }
    public void setAge() {
        String select_string = "SELECT Age FROM USERS_INFO WHERE _id='" + this.UserID +"'";
        Cursor c2 = db.rawQuery(select_string, null);
        while(c2.moveToNext()){
            this.age = Integer.valueOf(c2.getString(c2.getColumnIndexOrThrow("Age")));
        }
    }

    public void setHeight() {
        String select_string = "SELECT Height FROM USERS_INFO WHERE _id='" + this.UserID +"'";
        Cursor c2 = db.rawQuery(select_string, null);
        while(c2.moveToNext()){
            this.height = Integer.valueOf(c2.getString(c2.getColumnIndexOrThrow("Height")));
        }
    }

    public void setWeight() {
        String select_string = "SELECT Weight FROM USERS_INFO WHERE _id='" + this.UserID +"'";
        Cursor c2 = db.rawQuery(select_string, null);
        while(c2.moveToNext()){
            this.weight = Integer.valueOf(c2.getString(c2.getColumnIndexOrThrow("Weight")));
        }
    }

    public void setActivity() {
        String select_string = "SELECT ActivityNum FROM USERS_INFO WHERE _id='" + this.UserID +"'";
        Cursor c2 = db.rawQuery(select_string, null);
        while(c2.moveToNext()){
            this.ActivityNum = Integer.valueOf(c2.getString(c2.getColumnIndexOrThrow("ActivityNum")));
        }
    }

    public void setPurpose() {
        String select_string = "SELECT Purpose2 FROM USERS_INFO WHERE _id='" + this.UserID +"'";
        Cursor c2 = db.rawQuery(select_string, null);
        while(c2.moveToNext()){
            this.PurposeNum = Integer.valueOf(c2.getString(c2.getColumnIndexOrThrow("Purpose2")));
        }
    }

    public void setActivityName() {
        String select_string = "SELECT Description FROM ACTIVITY WHERE _IdActivity='" + this.ActivityNum +"'";
        System.out.println(select_string);
        Cursor c2 = db.rawQuery(select_string, null);
        while(c2.moveToNext()){
            this.activityName = c2.getString(c2.getColumnIndexOrThrow("Description"));
        }
    }

    public void setPurposeName() {
        String select_string = "SELECT Description FROM PURPOSE WHERE _idPurpose='" + this.PurposeNum +"'";
        Cursor c2 = db.rawQuery(select_string, null);
        while(c2.moveToNext()){
            this.purposeName = c2.getString(c2.getColumnIndexOrThrow("Description"));
        }
    }

    public void updateCaloriesNorm() {
        String select_string = "UPDATE USERS_INFO SET CaloriesNorm ='" + this.CaloriesNorm + "' WHERE _id = '" + this.UserID + "'";
        db.execSQL(select_string);
    }

    public void updateProteinsNorm() {
        String select_string = "UPDATE USERS_INFO SET ProteinsNorm ='" + this.ProteinsNorm + "' WHERE _id = '" + this.UserID + "'";
        db.execSQL(select_string);
    }

    public void updateFatsNorm() {
        String select_string = "UPDATE USERS_INFO SET FatsNorm ='" + this.FatsNorm + "' WHERE _id = '" + this.UserID + "'";
        db.execSQL(select_string);
    }

    public void updateCarboNorm() {
        String select_string = "UPDATE USERS_INFO SET CarboNorm ='" + this.CarboNorm + "' WHERE _id = '" + this.UserID + "'";
        db.execSQL(select_string);
    }

    public void setCFPC(){
        //(10 x вес (кг) + 6.25 x рост (см) – 5 x возраст (г) + 5) x A - для мужчин;
        // (10 x вес (кг) + 6.25 x рост (см) – 5 x возраст (г) - 161) x A - для женщин.
        double A = 0;
        switch (this.ActivityNum) {
            case 1:
                A = 1.2;
                break;
            case 2:
                A = 1.375;
                break;
            case 3:
                A = 1.55;
                break;
            case 4:
                A = 1.7;
                break;
            case 5:
                A = 1.9;
                break;
            default:
                System.out.println("Нет такой активности, невозможная ошибка");}
        this.CaloriesNorm = Math.round(countCalories(A) * 100.00)/100.00;
        this.countPFC();
        this.updateCaloriesNorm();
        this.updateProteinsNorm();
        this.updateFatsNorm();
        this.updateCarboNorm();
    }

    public double countCalories(double A){
        if (this.getGender().equals("Мужской")){
            if (this.PurposeNum!=1){
                return ((10*this.getAge()+6.25*this.getHeight()-5*this.getAge()+5) * A);}
            else {
                return (((10*this.getAge()+6.25*this.getHeight()-5*this.getAge()+5) * A) * 0.9);
            }
        }
        else {if (this.PurposeNum!=1){
            return ((10*this.getAge()+6.25*this.getHeight()-5*this.getAge()-161) * A);}
        else {
            return (((10*this.getAge()+6.25*this.getHeight()-5*this.getAge()-161) * A) * 0.9);
        }}
    }

    public void countPFC(){
        NumberFormat f= NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        switch (this.PurposeNum) {
            case 1:
                this.ProteinsNorm = (Math.round(this.CaloriesNorm*0.3/4 * 100.00))/100.00;
                this.FatsNorm = (Math.round(this.CaloriesNorm*0.2/9 * 100.00))/100.00;
                this.CarboNorm = (Math.round(this.CaloriesNorm*0.5/4 * 100.00))/100.00;
                break;
            case 2:
                this.ProteinsNorm = (Math.round(this.CaloriesNorm*0.3/4 * 100.00))/100.00;
                this.FatsNorm = (Math.round(this.CaloriesNorm*0.3/9 * 100.00))/100.00;
                this.CarboNorm = (Math.round(this.CaloriesNorm*0.4/4 * 100.00))/100.00;
                break;
            case 3:
                this.ProteinsNorm = (Math.round(this.CaloriesNorm*0.3/4 * 100.00))/100.00;
                this.FatsNorm = (Math.round(this.CaloriesNorm*0.3/9 * 100.00))/100.00;
                this.CarboNorm = (Math.round(this.CaloriesNorm*0.4/4 * 100.00))/100.00;
                break;
        }}

    public void updateGender() {
        String select_string = "UPDATE USERS_INFO SET Gender ='" + this.gender + "' WHERE _id = '" + this.UserID + "'";
        System.out.println(select_string);
        db.execSQL(select_string);
    }

    public void updateAge() {
        String select_string = "UPDATE USERS_INFO SET Age ='" + this.age + "' WHERE _id = '" + this.UserID + "'";
        db.execSQL(select_string);
    }

    public void updateWeight() {
        String select_string = "UPDATE USERS_INFO SET Weight ='" + this.weight + "' WHERE _id = '" + this.UserID + "'";
        db.execSQL(select_string);
    }

    public void updateHeight() {
        String select_string = "UPDATE USERS_INFO SET Height ='" + this.height + "' WHERE _id = '" + this.UserID + "'";
        db.execSQL(select_string);
    }

    public void updateActivity() {
        String select_string = "UPDATE USERS_INFO SET ActivityNum ='" + this.ActivityNum + "' WHERE _id = '" + this.UserID + "'";
        db.execSQL(select_string);
    }

    public void updatePurpose() {
        String select_string = "UPDATE USERS_INFO SET Purpose2 ='" + this.PurposeNum + "' WHERE _id = '" + this.UserID + "'";
        db.execSQL(select_string);
    }
}
