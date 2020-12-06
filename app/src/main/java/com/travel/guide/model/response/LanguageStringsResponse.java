package com.travel.guide.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.travel.guide.helper.language.GlobalLanguages;

public class LanguageStringsResponse {

    @Expose
    @SerializedName("params")
    private GlobalLanguages globalLanguages;

    @Expose
    @SerializedName("status")
    private int status;

    public GlobalLanguages getGlobalLanguages() {
        return globalLanguages;
    }

    public int getStatus() {
        return status;
    }

}
