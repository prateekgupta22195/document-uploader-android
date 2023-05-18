package com.pg.documentuploader.data.remote.simplifii.model;


import com.google.gson.annotations.SerializedName;

public class Datum {
    @SerializedName("original_filename")
    private String originalFilename;
    @SerializedName("modified_filename")
    private String modifiedFilename;
    @SerializedName("url")
    private String url;

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getModifiedFilename() {
        return modifiedFilename;
    }

    public void setModifiedFilename(String modifiedFilename) {
        this.modifiedFilename = modifiedFilename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}