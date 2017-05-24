package com.isoma.homiladavridoctor.Entity;

/**
 * Created by developer on 15.04.2017.
 */

public class EventMessage {
    private Object objectForTransfer;
    private String status;
    private String forClass;

    public EventMessage(Object objectForTransfer, String status, String forClass) {
        this.objectForTransfer = objectForTransfer;
        this.status = status;
        this.forClass = forClass;
    }

    public Object getObjectForTransfer() {
        return objectForTransfer;
    }

    public void setObjectForTransfer(Object objectForTransfer) {
        this.objectForTransfer = objectForTransfer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getForClass() {
        return forClass;
    }

    public void setForClass(String forClass) {
        this.forClass = forClass;
    }
}
