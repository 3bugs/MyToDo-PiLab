package com.promlert.mytodo.net;

import com.google.gson.annotations.SerializedName;
import com.promlert.mytodo.db.ToDo;

import java.util.List;

public class GetToDoResponse extends BaseResponse {

    @SerializedName("data")
    public List<ToDo> data;
}
