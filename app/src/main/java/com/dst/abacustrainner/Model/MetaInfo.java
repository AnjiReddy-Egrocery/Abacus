package com.dst.abacustrainner.Model;

import com.google.gson.annotations.SerializedName;

public class MetaInfo {

    @SerializedName("udf1")
    private String udf1;

    @SerializedName("udf2")
    private String udf2;

    public MetaInfo(String udf1, String udf2) {
        this.udf1 = udf1;
        this.udf2 = udf2;
    }
}
