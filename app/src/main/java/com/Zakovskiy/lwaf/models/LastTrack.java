package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LastTrack implements Serializable {
    @JsonProperty(PacketDataKeys.TRACK_TITLE)
    public String title = "";
    @JsonProperty(PacketDataKeys.TRACK_ARTIST)
    public String artist = "";
    @JsonProperty(PacketDataKeys.ICON)
    public String icon = "";
    @JsonProperty(PacketDataKeys.DURATION)
    public Integer duration = 0;
    @JsonProperty(PacketDataKeys.TRACK_ID)
    public Integer trackId = 0;
    @JsonProperty(PacketDataKeys.OWNER_ID)
    public Integer ownerId = 0;
}
