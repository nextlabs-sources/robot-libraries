package com.nextlabs.qa.keywords.pdp.json.beans;

import java.util.List;

/**
 * Created by sduan on 17/12/2015.
 */
public class Response {
    private List<Result> Result;

    public List<Result> getResult() {
        return Result;
    }

    public void setResult(List<Result> result) {
        Result = result;
    }
}
