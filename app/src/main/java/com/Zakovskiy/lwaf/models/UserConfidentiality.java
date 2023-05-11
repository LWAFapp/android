package com.Zakovskiy.lwaf.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserConfidentiality {
    @JsonProperty("hide_balance")
    public Integer hideBalance;
    @JsonProperty("hide_last_tracks")
    public Integer hideLastTracks;
}
