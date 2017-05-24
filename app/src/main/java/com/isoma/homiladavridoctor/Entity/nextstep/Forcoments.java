package com.isoma.homiladavridoctor.Entity.nextstep;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
 * Created by developer on 17.05.2016.
 */
public class Forcoments {
    private String glavText;
    private String whoIm;
    private Long createAt;

    public Forcoments() {

    }

    public Forcoments(String glavText, String whoIm) {
        this.glavText = glavText;
        this.whoIm = whoIm;
    }

    public Forcoments(String glavText, String whoIm, Long createAt) {
        this.glavText = glavText;
        this.whoIm = whoIm;
        this.createAt = createAt;
    }

    public String getGlavText() {
        return glavText;
    }

    public void setGlavText(String glavText) {
        this.glavText = glavText;
    }

    public String getWhoIm() {
        return whoIm;
    }

    public void setWhoIm(String whoIm) {
        this.whoIm = whoIm;
    }

    public Map<String, String> getCreateAt() {

        return ServerValue.TIMESTAMP;
    }
    @Exclude
    public long getCreareTimeLong() {

        return createAt;
    }
}
