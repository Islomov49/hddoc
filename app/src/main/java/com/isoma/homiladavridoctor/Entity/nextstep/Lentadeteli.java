package com.isoma.homiladavridoctor.Entity.nextstep;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
 * Created by developer on 08.04.2016.
 */
public class Lentadeteli {

    private String Text;
    private Long Likes;
    private String WriteBy;
    private String Photo;
    private String KEY;
    private  Long creatAt;

    public Lentadeteli() {
    }

    public Lentadeteli( String text, Long likes, String writeBy, String photo) {

        Text = text;
        Likes = likes;
        WriteBy = writeBy;
        Photo=photo;
    }

    public Lentadeteli( String key, String text, Long likes, String writeBy, String photo, Long CreatAt) {
        Text = text;
        Likes = likes;
        WriteBy = writeBy;
        Photo=photo;
        KEY=key;
        creatAt=CreatAt;
    }
    public String getKEY() {
        return KEY;
    }

    public void setKEY(String KEY) {
        this.KEY = KEY;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }



    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public Long getLikes() {
        return Likes;
    }

    public void setLikes(Long likes) {
        Likes = likes;
    }

    public String getWriteBy() {
        return WriteBy;
    }

    public void setWriteBy(String writeBy) {
        WriteBy = writeBy;
    }

    public Map<String, String> getCreatAt() {

        return ServerValue.TIMESTAMP;
    }


    @Exclude
    public long getDateLastChangedLong() {

        return creatAt;
    }
}
