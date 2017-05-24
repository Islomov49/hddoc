package com.isoma.homiladavridoctor.Entity;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by developer on 13.03.2016.
 */
public class UserInfo {
    private String countryCodeSim;
    private String countryCode;
    private Boolean isPrivate;
    private String nickName;
    private String myLongName;
    private String myage;
    private String language;
    @Exclude
    private String avatarUrl;
    @Exclude
    Long avatarLastChange;
    private AnketaEntity anketa;

    private String whoIM;
    private int week;
    private HashMap<String, Object> dateCreated;
    private HashMap<String, Object> dateLastChanged;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMyLongName() {
        return myLongName;
    }

    public void setMyLongName(String myLongName) {
        this.myLongName = myLongName;
    }

    public String getMyage() {
        return myage;
    }

    public void setMyage(String myage) {
        this.myage = myage;
    }

    @Exclude
    public String getAvatarUrl() {
        return avatarUrl;
    }
    @Exclude
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    @Exclude
    public Long getAvatarLastChange() {
        return avatarLastChange;
    }
    @Exclude
    public void setAvatarLastChange(Long avatarLastChange) {
        this.avatarLastChange = avatarLastChange;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }



    public UserInfo(String language,String nickName, String countryCode, Boolean isPrivate, String myLongName, String myage, String whoIM, int week, HashMap<String, Object> dateCreated, AnketaEntity anketa) {
        this.language = language;
        this.anketa =anketa;
        this.countryCodeSim = countryCode;
        this.nickName = nickName;
        this.isPrivate = isPrivate;
        this.myLongName = myLongName;
        this.myage = myage;
        this.whoIM = whoIM;
        this.week = week;
        this.dateCreated = dateCreated;

        //Date last changed will always be set to ServerValue.TIMESTAMP
        HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
        dateLastChangedObj.put("date", ServerValue.TIMESTAMP);
        this.dateLastChanged = dateLastChangedObj;

    }
    public UserInfo(String countryCode, Boolean isPrivate, String myLongName, String myage,  String whoIM, int week, HashMap<String, Object> dateCreated,AnketaEntity anketa) {
        this.anketa = anketa;
        this.countryCodeSim = countryCode;
        this.isPrivate = isPrivate;
        this.myLongName = myLongName;
        this.myage = myage;

        this.whoIM = whoIM;
        this.week = week;
        this.dateCreated = dateCreated;

        HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
        dateLastChangedObj.put("date", ServerValue.TIMESTAMP);
        this.dateLastChanged = dateLastChangedObj;

    }

    public String getWhoIM() {
        return whoIM;
    }

    public void setWhoIM(String whoIM) {
        this.whoIM = whoIM;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    /**
     * Required public constructor
     */
    public UserInfo() {
    }

    public UserInfo(String countryCode, int week, Boolean isprivate, String whoim, HashMap<String,Object> dateCreated,AnketaEntity anketa) {
        this.anketa = anketa;
        this.countryCodeSim = countryCode;
        this.week = week;
        this.isPrivate = isprivate;
        this.whoIM = whoim;
        this.dateCreated = dateCreated;

        //Date last changed will always be set to ServerValue.TIMESTAMP
        HashMap<String, Object> dateLastChangedObj = new HashMap<String, Object>();
        dateLastChangedObj.put("date", ServerValue.TIMESTAMP);
        this.dateLastChanged = dateLastChangedObj;
    }

    public String getcountryCode() {
        return countryCodeSim;
    }

    public int getWeek() {
        return week;
    }

    public HashMap<String, Object> getDateLastChanged() {
        return dateLastChanged;
    }

    public HashMap<String, Object> getDateCreated() {
        //If there is a dateCreated object already, then return that
        if (dateCreated != null) {
            return dateCreated;
        }
        //Otherwise make a new object set to ServerValue.TIMESTAMP
        HashMap<String, Object> dateCreatedObj = new HashMap<String, Object>();
        dateCreatedObj.put("date", ServerValue.TIMESTAMP);
        return dateCreatedObj;
    }

    // Use the method described in http://stackoverflow.com/questions/25500138/android-chat-crashes-on-datasnapshot-getvalue-for-timestamp/25512747#25512747
// to get the long values from the date object.
    @Exclude
    public long getDateLastChangedLong() {

        return (long)dateLastChanged.get("date");
    }

    @Exclude
    public long getDateCreatedLong() {
        return (long)dateCreated.get("date");
    }
    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nickName", nickName);
        result.put("myLongName", myLongName);
        result.put("isPrivate", isPrivate);
        result.put("myage", myage);
        result.put("myAnketa", anketa.toMap() );
        result.put("countryCodeSim", countryCodeSim);
        result.put("language", language);
        result.put("whoIM", whoIM);
        result.put("dateCreated", dateCreated);
        result.put("dateLastChanged", dateLastChanged);
        return result;
    }
    @Exclude
    public void setFromSnapshot(DataSnapshot dataSnapshot){
        nickName = dataSnapshot.child("nickName").getValue(String.class);
        myLongName = dataSnapshot.child("myLongName").getValue(String.class);
        isPrivate = dataSnapshot.child("isPrivate").getValue(Boolean.class);
        myage = dataSnapshot.child("myage").getValue(String.class);
        anketa = new AnketaEntity(
                dataSnapshot.child("myAnketa").child("myDoctorNumber").getValue(String.class),
                dataSnapshot.child("myAnketa").child("myDoctorname").getValue(String.class),
                dataSnapshot.child("myAnketa").child("myManzil").getValue(String.class),
                dataSnapshot.child("myAnketa").child("myPoliklinika").getValue(String.class),
                dataSnapshot.child("myAnketa").child("myQongruh").getValue(String.class),
                dataSnapshot.child("myAnketa").child("myhusband").getValue(String.class),
                dataSnapshot.child("myAnketa").child("myhusbandNumber").getValue(String.class)
        );
        countryCodeSim = dataSnapshot.child("countryCodeSim").getValue(String.class);
        language = dataSnapshot.child("language").getValue(String.class);
        for(DataSnapshot dataSnapshot1: dataSnapshot.child("avatar").getChildren()) {
            avatarUrl = dataSnapshot1.getKey();
            avatarLastChange = dataSnapshot1.getValue(Long.class);
        }
        whoIM = dataSnapshot.child("whoIM").getValue(String.class);
        dateCreated = new HashMap<>();
        dateCreated.put("date", dataSnapshot.child("dateCreated").child("date").getValue(Long.class));
        dateLastChanged = new HashMap<>();
        dateLastChanged.put("date", dataSnapshot.child("dateLastChanged").child("date").getValue(Long.class));

    }
}