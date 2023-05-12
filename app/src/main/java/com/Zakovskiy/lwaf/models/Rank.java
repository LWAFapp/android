package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.application.Application;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rank {
    @JsonProperty("rid")
    public Integer rankId = 0;
    @JsonProperty("t")
    public String title = "";
    @JsonProperty("bgc")
    public String backgroundColor = "";
    @JsonProperty("tc")
    public String textColor = "";

    public String getIcon() {
        return Application.lwafServerConfig.imgsPath + "ranks/" + String.valueOf(this.rankId) + ".png";
    }
}
