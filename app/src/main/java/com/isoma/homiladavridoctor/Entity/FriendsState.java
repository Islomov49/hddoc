package com.isoma.homiladavridoctor.Entity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;


public class FriendsState {
    private long date;
    private long newMessages;
    private String roomID;
    @Exclude
    private String friendUID;

    public FriendsState(){

    }

    public Map<String, String> getDate() {
        return ServerValue.TIMESTAMP;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getNewMessages() {
        return newMessages;
    }

    public void setNewMessages(long newMessages) {
        this.newMessages = newMessages;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    @Exclude
    public String getFriendUID() {
        return friendUID;
    }
    @Exclude
    public void setFriendUID(String friendUID) {
        this.friendUID = friendUID;
    }

    @Exclude
    public long getLastChangedDate() {
        return date;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", ServerValue.TIMESTAMP);
        result.put("newMessages",newMessages);
        result.put("roomID",roomID);
        return result;
    }

    @Exclude
    public void setFromSnapshot(DataSnapshot dataSnapshot){
        date = dataSnapshot.child("date").getValue(Long.class);
        newMessages = dataSnapshot.child("newMessages").getValue(Long.class);
        roomID = dataSnapshot.child("roomID").getValue(String.class);
        friendUID =  dataSnapshot.getKey();
    }

}
