package com.isoma.homiladavridoctor.Entity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by developer on 22.04.2017.
 */

public class MessageChating {
    private Long date;
    private String message;
    private String writerUID;
    private String toUserUID;

    @Exclude
    private String pushValue;
    public MessageChating(){

    }
    public MessageChating( String message, String writerUID,String toUserUID) {
        this.message = message;
        this.writerUID = writerUID;
        this.toUserUID = toUserUID;

    }

    public String getToUserUID() {
        return toUserUID;
    }

    public void setToUserUID(String toUserUID) {
        this.toUserUID = toUserUID;
    }

    public Map<String, String> getDate() {
        return ServerValue.TIMESTAMP;
    }

    public void setDate(Long date) {
        this.date = date;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWriterUID() {
        return writerUID;
    }

    public void setWriterUID(String writerUID) {
        this.writerUID = writerUID;
    }
    @Exclude
    public long getDateLong(){
        return date;
    }
    @Exclude
    public void isReaded(DatabaseReference databaseReference, String roomId){
        databaseReference.child("Rooms/"+roomId+"/"+pushValue+"/delevered").setValue(true);
    }
    @Exclude
    public String getPushValue() {
        return pushValue;
    }

    @Exclude
    public void setPushValue(String pushValue) {
        this.pushValue = pushValue;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date",date);
        result.put("message",message);
        result.put("writerUID ",writerUID);
        result.put("toUserUID ",toUserUID);
        return result;
    }
    @Exclude
    public void setFromSnapshot(DataSnapshot dataSnapshot){
        pushValue = dataSnapshot.getKey();
        date = dataSnapshot.child("date").getValue(Long.class);
        message = dataSnapshot.child("message").getValue(String.class);
        writerUID = dataSnapshot.child("writerUID").getValue(String.class);
        toUserUID = dataSnapshot.child("toUserUID").getValue(String.class);

    }
}
