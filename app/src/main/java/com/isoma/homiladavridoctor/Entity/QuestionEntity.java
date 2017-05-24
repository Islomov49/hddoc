package com.isoma.homiladavridoctor.Entity;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by developer on 11.04.2017.
 */

public class QuestionEntity {
    private String condisionRuleCode;
    private String country;
    private boolean isClosedQuestion;
    private String language;
    private String photoID;
    private  Long publishedDate;
    private String questionText;
    private StateQuestion stateQuestion;
    private String writerUID;
    private String thumbnail;
    @Exclude
    private String keyQuestion;
    public String getCondisionRuleCode() {
        return condisionRuleCode;
    }

    public void setCondisionRuleCode(String condisionRuleCode) {
        this.condisionRuleCode = condisionRuleCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isClosedQuestion() {
        return isClosedQuestion;
    }

    public void setClosedQuestion(boolean closedQuestion) {
        isClosedQuestion = closedQuestion;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public Map<String, String> getPublishedDate() {
        return  ServerValue.TIMESTAMP;
    }

    public void setPublishedDate(Long publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public StateQuestion getStateQuestion() {
        return stateQuestion;
    }

    public void setStateQuestion(StateQuestion stateQuestion) {
        this.stateQuestion = stateQuestion;
    }

    public String getWriterUID() {
        return writerUID;
    }
    public QuestionEntity(){}
    public QuestionEntity(String condisionRuleCode, String country, String language, String photoID, String questionText, StateQuestion stateQuestion, String writerUID,String thumbnail) {
        this.condisionRuleCode = condisionRuleCode;
        this.country = country;
        this.language = language;
        this.photoID = photoID;
        this.questionText = questionText;
        this.stateQuestion = stateQuestion;
        this.writerUID = writerUID;
        this.isClosedQuestion = false;
        this.thumbnail = thumbnail;
    }

    public void setWriterUID(String writerUID) {
        this.writerUID = writerUID;
    }
    @Exclude
    public Long getPublishedDateLong() {
        return  publishedDate;
    }
    @Exclude
    public String getKeyQuestion() {
        return keyQuestion;
    }
    @Exclude
    public void setKeyQuestion(String keyQuestion) {
        this.keyQuestion = keyQuestion;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("condisionRuleCode", condisionRuleCode);
        result.put("country", country);
        result.put("isClosedQuestion", isClosedQuestion);
        result.put("language", language);
        result.put("photoID", photoID);
        result.put("publishedDate", getPublishedDate());
        result.put("questionText", questionText);
        result.put("state", stateQuestion.toMap() );
        result.put("writerUID", writerUID);
        result.put("thumbnail", thumbnail);
        return result;
    }
    @Exclude
    public void setFromSnapshot(DataSnapshot dataSnapshot){
        keyQuestion = dataSnapshot.getKey();
        condisionRuleCode = dataSnapshot.child("condisionRuleCode").getValue(String.class);
        country = dataSnapshot.child("country").getValue(String.class);
        DataSnapshot isClosedQuestion = dataSnapshot.child("isClosedQuestion");
        if(isClosedQuestion.getValue()!=null)
        this.isClosedQuestion = isClosedQuestion.getValue(Boolean.class);
        else     this.isClosedQuestion = false;

        language = dataSnapshot.child("language").getValue(String.class);
        photoID = dataSnapshot.child("photoID").getValue(String.class);
        publishedDate = dataSnapshot.child("publishedDate").getValue(Long.class);
        questionText = dataSnapshot.child("questionText").getValue(String.class);
        Log.d("TESTTTTE", "setFromSnapshot: "+dataSnapshot.getValue().toString());
        stateQuestion = new StateQuestion(
                dataSnapshot.child("state").child("answer").getValue(Integer.class),
                dataSnapshot.child("state").child("likes").getValue(Integer.class),
                dataSnapshot.child("state").child("subsribers").getValue(Integer.class),
                dataSnapshot.child("state").child("views").getValue(Integer.class)
        );
        writerUID = dataSnapshot.child("writerUID").getValue(String.class);
        thumbnail = dataSnapshot.child("thumbnail").getValue(String.class);
    }
}
