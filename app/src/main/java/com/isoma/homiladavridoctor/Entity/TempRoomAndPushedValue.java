package com.isoma.homiladavridoctor.Entity;

/**
 * Created by developer on 30.04.2017.
 */

public class TempRoomAndPushedValue {
    private String roomId;
    private String pushedValue;

    public TempRoomAndPushedValue(String roomId, String pushedValue) {
        this.roomId = roomId;
        this.pushedValue = pushedValue;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getPushedValue() {
        return pushedValue;
    }

    public void setPushedValue(String pushedValue) {
        this.pushedValue = pushedValue;
    }
}
