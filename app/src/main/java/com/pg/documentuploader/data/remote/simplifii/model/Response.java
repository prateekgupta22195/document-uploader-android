package com.pg.documentuploader.data.remote.simplifii.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {

    @SerializedName("data")
    private List<Datum> data;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}