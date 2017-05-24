package com.isoma.homiladavridoctor.Entity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by developer on 21.04.2017.
 */

public class Achivments {
    private int answers;
    private int correctly;
    private int exp;
    private int question;
    public Achivments(){}

    public Achivments(int answers, int correctly, int exp, int question) {
        this.answers = answers;
        this.correctly = correctly;
        this.exp = exp;
        this.question = question;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    public int getCorrectly() {
        return correctly;
    }

    public void setCorrectly(int correctly) {
        this.correctly = correctly;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getQuestion() {
        return question;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("answers", answers);
        result.put("correctly", correctly);
        result.put("exp", exp);
        result.put("question", question);

        return result;
    }
    @Exclude
    public void setFromSnapshot(DataSnapshot dataSnapshot){
        answers = (dataSnapshot.child("answers").getValue(Integer.class)==null)?0:dataSnapshot.child("answers").getValue(Integer.class);
        correctly = (dataSnapshot.child("correctly").getValue(Integer.class)==null)?0:dataSnapshot.child("correctly").getValue(Integer.class);
        exp = (dataSnapshot.child("exp").getValue(Integer.class)==null)?0:dataSnapshot.child("exp").getValue(Integer.class);
        question = (dataSnapshot.child("question").getValue(Integer.class)==null)?0:dataSnapshot.child("question").getValue(Integer.class);
    }
}
