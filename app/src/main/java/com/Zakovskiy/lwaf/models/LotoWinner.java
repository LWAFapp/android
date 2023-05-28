package com.Zakovskiy.lwaf.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LotoWinner {
    @JsonProperty("u")
    public ShortUser user = new ShortUser();
    @JsonProperty("lws")
    public Integer winSize = 0;
    @JsonProperty("ln")
    public List<String> lotoNumbers = new ArrayList<>();
}
