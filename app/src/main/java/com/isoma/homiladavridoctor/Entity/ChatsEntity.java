package com.isoma.homiladavridoctor.Entity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by developer on 27.04.2017.
 */

public class ChatsEntity {
    @Exclude
    String keyCompanion;
    long date;
    long newMessage;
    String roomId;
    public ChatsEntity(){

    }
    public ChatsEntity(String roomId) {
        this.roomId = roomId;

    }
    @Exclude
    public String getKeyCompanion() {
        return keyCompanion;
    }
    @Exclude
    public void setKeyCompanion(String keyCompanion) {
        this.keyCompanion = keyCompanion;
    }

    public Map<String, String> getDate() {
        return ServerValue.TIMESTAMP;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(long newMessage) {
        this.newMessage = newMessage;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    @Exclude
    public long getDateLong() {
        return date;
    }
    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date",date);
        result.put("newMessage",newMessage);
        result.put("roomId",roomId);
        return result;
    }
    @Exclude
    public void setFromSnapshot(DataSnapshot dataSnapshot){
        keyCompanion = dataSnapshot.getKey();
        date = dataSnapshot.child("date").getValue(Long.class);
        if(dataSnapshot.child("newMessages").getValue()!=null)
        newMessage = dataSnapshot.child("newMessages").getValue(Long.class);
        else newMessage = 0;
        roomId = dataSnapshot.child("roomId").getValue(String.class);
     }
}
