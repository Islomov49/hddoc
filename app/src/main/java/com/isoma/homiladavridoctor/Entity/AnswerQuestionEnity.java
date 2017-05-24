package com.isoma.homiladavridoctor.Entity;

/**
 * Created by developer on 16.03.2017.
 */

public class AnswerQuestionEnity {
    private String answer;
    private String question;
    private String type;
    private String uid;


    public AnswerQuestionEnity(String answer, String question, String type, String uid) {
        this.answer = answer;
        this.question = question;
        this.type = type;
        this.uid = uid;
    }

    public AnswerQuestionEnity(String answer, String question, String type) {
        this.answer = answer;
        this.question = question;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
