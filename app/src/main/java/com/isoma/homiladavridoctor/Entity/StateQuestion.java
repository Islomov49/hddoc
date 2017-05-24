package com.isoma.homiladavridoctor.Entity;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by developer on 11.04.2017.
 */

public class StateQuestion {
    private int answer;
    private int likes;
    private int subsribers;
    private int views;
    public StateQuestion(){
        answer = 0;
        likes = 0;
        subsribers = 0;
        views = 0;
    }
    public StateQuestion(int answer, int likes, int subsribers, int views) {
        this.answer = answer;
        this.likes = likes;
        this.subsribers = subsribers;
        this.views = views;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getSubsribers() {
        return subsribers;
    }

    public void setSubsribers(int subsribers) {
        this.subsribers = subsribers;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("answer", answer);
        result.put("likes", likes);
        result.put("subsribers", subsribers);
        result.put("views", views);
        return result;
    }
}
