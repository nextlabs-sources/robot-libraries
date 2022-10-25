package com.nextlabs.qa.keywords.pdp.json.beans;

import java.util.List;

/**
 * Created by sduan on 17/12/2015.
 */
public class Result {

    private String Decision;
    private Status Status;
    private List<Obligation> Obligations;

    public String getDecision() {
        return Decision;
    }

    public void setDecision(String decision) {
        Decision = decision;
    }

    public Status getStatus() {
        return Status;
    }

    public void setStatus(Status status) {
        Status = status;
    }

    public List<Obligation> getObligations() {
        return Obligations;
    }

    public void setObligations(List<Obligation> obligations) {
        Obligations = obligations;
    }
}
