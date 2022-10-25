package com.nextlabs.qa.keywords.pdp.json.beans;

/**
 * Created by sduan on 17/12/2015.
 */
public class Status {
    private String StatusMessage;
    private StatusCode StatusCode;

    public String getStatusMessage() {
        return StatusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        StatusMessage = statusMessage;
    }

    public StatusCode getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        StatusCode = statusCode;
    }
}
