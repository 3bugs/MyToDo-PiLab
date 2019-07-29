package com.promlert.mytodo.net;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("error")
    public Error error;
}
