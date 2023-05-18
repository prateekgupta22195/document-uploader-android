package com.pg.documentuploader.data.remote.simplifii.model;

import com.google.gson.annotations.SerializedName;

public class FilePostingResponse {

    @SerializedName("response")
    private Response response;
    @SerializedName("msg")
    private String msg;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}