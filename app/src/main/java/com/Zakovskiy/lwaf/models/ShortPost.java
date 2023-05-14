package com.Zakovskiy.lwaf.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortPost {
    @JsonProperty("pid")
    public String id;
    @JsonProperty("pt")
    public String title;
    @JsonProperty("ptc")
    public Long time;
    @JsonProperty("pc")
    public String content;
    @JsonProperty("pa")
    public ShortUser author;
    @JsonProperty("pl")
    public Integer likes;
    @JsonProperty("pd")
    public Integer dislikes;
}
