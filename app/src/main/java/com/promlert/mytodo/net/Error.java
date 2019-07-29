package com.promlert.mytodo.net;

import com.google.gson.annotations.SerializedName;

public class Error {

    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("log")
    private String log;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
