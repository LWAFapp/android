package com.Zakovskiy.lwaf.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LastTrack implements Serializable {
    @JsonProperty("title")
    public String title = "";
    @JsonProperty("icon")
    public String icon = "";
    @JsonProperty("duration")
    public Integer duration = 0;
    @JsonProperty("track_id")
    public Integer trackId = 0;
    @JsonProperty("owner_id")
    public Integer ownerId = 0;
}
