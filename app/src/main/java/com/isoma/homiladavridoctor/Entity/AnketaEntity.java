package com.isoma.homiladavridoctor.Entity;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by developer on 24.03.2017.
 */

public class AnketaEntity {
    private String myDoctorname;
    private String myDoctorNumber;
    private String myPoliklinika;
    private String myQongruh;
    private String myManzil;
    private String myhusband;
    private String myhusbandNumber;

    public String getMyDoctorname() {
        return myDoctorname;
    }

    public void setMyDoctorname(String myDoctorname) {
        this.myDoctorname = myDoctorname;
    }

    public String getMyDoctorNumber() {
        return myDoctorNumber;
    }

    public void setMyDoctorNumber(String myDoctorNumber) {
        this.myDoctorNumber = myDoctorNumber;
    }

    public String getMyPoliklinika() {
        return myPoliklinika;
    }

    public void setMyPoliklinika(String myPoliklinika) {
        this.myPoliklinika = myPoliklinika;
    }

    public String getMyQongruh() {
        return myQongruh;
    }

    public void setMyQongruh(String myQongruh) {
        this.myQongruh = myQongruh;
    }

    public String getMyManzil() {
        return myManzil;
    }

    public void setMyManzil(String myManzil) {
        this.myManzil = myManzil;
    }

    public String getMyhusband() {
        return myhusband;
    }

    public void setMyhusband(String myhusband) {
        this.myhusband = myhusband;
    }

    public String getMyhusbandNumber() {
        return myhusbandNumber;
    }

    public void setMyhusbandNumber(String myhusbandNumber) {
        this.myhusbandNumber = myhusbandNumber;
    }

    public AnketaEntity(String myDoctorname, String myDoctorNumber, String myPoliklinika, String myQongruh, String myManzil, String myhusband, String myhusbandNumber) {
        this.myDoctorname = myDoctorname;
        this.myDoctorNumber = myDoctorNumber;
        this.myPoliklinika = myPoliklinika;
        this.myQongruh = myQongruh;
        this.myManzil = myManzil;
        this.myhusband = myhusband;
        this.myhusbandNumber = myhusbandNumber;
    }
    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("myDoctorname", myDoctorname);
        result.put("myDoctorNumber", myDoctorNumber);
        result.put("myPoliklinika", myPoliklinika);
        result.put("myQongruh",myQongruh );
        result.put("myManzil", myManzil);
        result.put("myhusband", myhusband);
        result.put("myhusbandNumber", myhusbandNumber);

        return result;
    }
}
