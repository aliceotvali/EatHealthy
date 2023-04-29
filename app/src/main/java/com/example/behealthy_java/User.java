package com.example.behealthy_java;

public class User extends RegistrationAnketaActivity{
    private String gender, purpose, name;
    private Integer weight, height, age, ActivityNum, FatsNorm, ProteinsNorm, CarboNorm, CaloriesNorm, BMR;

    public User(String name, String gender, String purpose, int weight, int height, int age){
        this.name = name;
        this.gender = gender;
        this.purpose = purpose;
        this.weight = weight;
        this.height = height;
        this.age = age;
    }

}
