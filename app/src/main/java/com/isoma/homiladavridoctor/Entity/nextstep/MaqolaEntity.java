package com.isoma.homiladavridoctor.Entity.nextstep;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
 * Created by developer on 28.03.2017.
 */

public class MaqolaEntity {


    private String text;
    private String tema;
    private String writeByUID;
    private String thumbnail;
    //photoid == parentkey
    private String photoId;
    private  Long creatAt;
    //eni bolingan boyi
    private double relations;

    private boolean isItTrusted;
    @Exclude
    private long likeCount;
    @Exclude
    public long getLikeCount() {
        return likeCount;
    }
    @Exclude
    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    @Exclude
    private String writeBy;

    @Exclude
    public String getWriteBy() {
        return writeBy;
    }

    @Exclude
    public void setWriteBy(String writeBy) {
        this.writeBy = writeBy;
    }


    public MaqolaEntity(String text, String tema, String writeByUID, String photoId, Long creatAt, boolean isItTrusted,double relations, String thumbnail) {
        this.thumbnail = thumbnail;
        this.text = text;
        this.tema = tema;
        this.writeByUID = writeByUID;
        this.photoId = photoId;
        this.creatAt = creatAt;
        this.isItTrusted = isItTrusted;
        this.relations = relations;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getText() {
        return text;
    }

    public double getRelations() {
        return relations;
    }

    public void setRelations(double relations) {
        this.relations = relations;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getWriteByUID() {
        return writeByUID;
    }

    public void setWriteByUID(String writeByUID) {
        this.writeByUID = writeByUID;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public void setCreatAt(Long creatAt) {
        this.creatAt = creatAt;
    }

    public boolean isItTrusted() {
        return isItTrusted;
    }

    public void setItTrusted(boolean itTrusted) {
        isItTrusted = itTrusted;
    }

    public Map<String, String> getCreatAt() {

        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public long getDateLastChangedLong() {

        return creatAt;
    }
}
