package com.isoma.homiladavridoctor.Entity;

/**
 * Created by developer on 26.04.2017.
 */

public class QuestionKeyWithEvent {
    String questionKey;
    long newEvent;

    public QuestionKeyWithEvent(String questionKey, long newEvent) {
        this.questionKey = questionKey;
        this.newEvent = newEvent;
    }

    public String getQuestionKey() {
        return questionKey;
    }

    public void setQuestionKey(String questionKey) {
        this.questionKey = questionKey;
    }

    public long getNewEvent() {
        return newEvent;
    }

    public void setNewEvent(int newEvent) {
        this.newEvent = newEvent;
    }
}
