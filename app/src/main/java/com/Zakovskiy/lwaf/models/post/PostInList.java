package com.Zakovskiy.lwaf.models.post;

import com.Zakovskiy.lwaf.models.ShortUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostInList {
    @JsonProperty("pid")
    public String id = "";
    @JsonProperty("pt")
    public String title = "";
    @JsonProperty("ptc")
    public Long time = 0L;
    @JsonProperty("pcn")
    public String content = "";
    @JsonProperty("pa")
    public ShortUser author = new ShortUser();
    @JsonProperty("pli")
    public Integer likes = 0;
    @JsonProperty("pdi")
    public Integer dislikes = 0;
}
