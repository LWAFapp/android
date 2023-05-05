package com.Zakovskiy.lwaf.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Track implements Serializable {
    @JsonProperty("tt")
    public String title = "";
    @JsonProperty("i")
    public String icon = "";
    @JsonProperty("u")
    public ShortUser user = new ShortUser();
    @JsonProperty("du")
    public Integer duration = 0;
    @JsonProperty("k")
    public String key = "";
    @JsonProperty("ts")
    public Integer timeStamp = 0;
    @JsonProperty("l")
    public Integer likes = 0;
    @JsonProperty("di")
    public Integer dislikes = 0;
    @JsonProperty("sli")
    public Integer superLikes = 0;
}
