package com.Zakovskiy.lwaf.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserStats {
    @JsonProperty("likes")
    public Integer likes;
    @JsonProperty("dislikes")
    public Integer dislikes;
    @JsonProperty("superlikes")
    public Integer superLikes;
    @JsonProperty("tracks")
    public Integer tracks;
}
