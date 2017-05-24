package com.isoma.homiladavridoctor.Entity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by developer on 21.04.2017.
 */

public class UserStatus {
    Achivments achivments;
    @Exclude
    private String userUID;
    private String avatar;
    private long lastseen;
    private String nickName;
    private boolean online;
    private int week;
    private int age;
    public UserStatus(){

    }
    public UserStatus(Achivments achivments, String avatar, String nickName, boolean online, int week, int age) {
        this.achivments = achivments;

        this.avatar = avatar;
        this.nickName = nickName;
        this.online = online;
        this.week = week;
        this.age = age;

    }

    public Achivments getAchivments() {
        return achivments;
    }

    public void setAchivments(Achivments achivments) {
        this.achivments = achivments;
    }
    @Exclude
    public String getUserUID() {
        return userUID;
    }
    @Exclude
    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Map<String, String> getLastseen() {
        return ServerValue.TIMESTAMP;
    }

    public void setLastseen(long lastseen) {
        this.lastseen = lastseen;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Exclude
    public Long getLastseenLong() {
        return  lastseen;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("achivments",achivments.toMap());
        result.put("avatar",avatar);
        result.put("lastseen", ServerValue.TIMESTAMP);
        result.put("nickName",nickName);
        result.put("online",online);
        result.put("week",week);
        result.put("age",age);
        return result;
    }
    @Exclude
    public void setFromSnapshot(DataSnapshot dataSnapshot){
        userUID = dataSnapshot.getKey();
        avatar = dataSnapshot.child("avatar").getValue(String.class);
        lastseen = dataSnapshot.child("lastseen").getValue(Long.class);
        nickName = dataSnapshot.child("nickName").getValue(String.class);
        online = dataSnapshot.child("online").getValue(Boolean.class);
        week = dataSnapshot.child("week").getValue(Integer.class);
        age = (dataSnapshot.child("age").getValue(Integer.class)==null)?0:dataSnapshot.child("age").getValue(Integer.class);
        //TODO hamma primitivlani tewirib chiqish kere nullga
        achivments = new Achivments();
        achivments.setFromSnapshot(dataSnapshot.child("achivments"));

    }
}
