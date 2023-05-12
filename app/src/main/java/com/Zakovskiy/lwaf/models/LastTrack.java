package com.Zakovskiy.lwaf.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LastTrack implements Serializable {
    @JsonProperty("tt")
    public String title = "";
    @JsonProperty("i")
    public String icon = "";
    @JsonProperty("du")
    public Integer duration = 0;
    @JsonProperty("tid")
    public Integer trackId = 0;
    @JsonProperty("oid")
    public Integer ownerId = 0;
}
