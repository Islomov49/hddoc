package com.isoma.homiladavridoctor.Entity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.isoma.homiladavridoctor.utils.StatesLikesSubs;

import java.util.HashMap;
import java.util.Map;


public class Answer {
    private String bodyAnswer;
    private int stateTrusnes;
    private long likes;
    private long priority;
    private long publishedDate = 0;
    private String writerUID;
    @Exclude
    private String keyAnswer;

    public Answer(String bodyAnswer,  long likes,  String writerUID , String toUserUID) {
        this.bodyAnswer = bodyAnswer;
        this.likes = likes;
        this.writerUID = writerUID;
        this.stateTrusnes = StatesLikesSubs.NOT_SELECTED;
    }
    public Answer(){}



    public String getBodyAnswer() {
        return bodyAnswer;
    }

    public void setBodyAnswer(String bodyAnswer) {
        this.bodyAnswer = bodyAnswer;
    }

    public int getStateTrusnes() {
        return stateTrusnes;
    }

    public void setStateTrusnes(int stateTrusnes) {
        this.stateTrusnes = stateTrusnes;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        if(likes<0){
            this.likes = 0;
        }
        else
        this.likes = likes;
    }

    public Map<String, String> getPublishedDate() {
        return  ServerValue.TIMESTAMP;
    }

    public Map<String, String> getPriority() {
        return  ServerValue.TIMESTAMP;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public void setPublishedDate(long publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getWriterUID() {
        return writerUID;
    }

    public void setWriterUID(String writerUID) {
        this.writerUID = writerUID;
    }

    @Exclude
    public long getPublishedDateLong() {
        return publishedDate;
    }
    @Exclude
    public long getPriorityDateLong() {
        return priority;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("bodyAnswer",bodyAnswer);
        result.put("stateTrusnes", stateTrusnes);
        result.put("likes",likes);
        result.put("publishedDate",publishedDate);
        result.put("writerUID",writerUID);
        result.put("priority",priority);
        return result;
    }
    @Exclude
    public String getKeyAnswer() {
        return keyAnswer;
    }
    @Exclude
    public void setKeyAnswer(String keyAnswer) {
        this.keyAnswer = keyAnswer;
    }


    @Exclude
    public void setFromSnapshot(DataSnapshot dataSnapshot){
        keyAnswer = dataSnapshot.getKey();
        bodyAnswer = dataSnapshot.child("bodyAnswer").getValue(String.class);
        stateTrusnes = dataSnapshot.child("stateTrusnes").getValue(Integer.class);
        likes = dataSnapshot.child("likes").getValue(Long.class);
        publishedDate = dataSnapshot.child("publishedDate").getValue(Long.class);
        priority = dataSnapshot.child("priority").getValue(Long.class);
        writerUID = dataSnapshot.child("writerUID").getValue(String.class);
    }
}
