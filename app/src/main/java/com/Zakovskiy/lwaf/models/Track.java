package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Track implements Serializable {
    @JsonProperty(PacketDataKeys.TRACK_TITLE)
    public String title = "";
    @JsonProperty(PacketDataKeys.ICON)
    public String icon = "";
    @JsonProperty(PacketDataKeys.USER)
    public ShortUser user = new ShortUser();
    @JsonProperty(PacketDataKeys.DURATION)
    public Integer duration = 0;
    @JsonProperty(PacketDataKeys.KEY)
    public String key = "";
    @JsonProperty(PacketDataKeys.TRACK_TIME_SET)
    public Integer timeStamp = 0;
    @JsonProperty(PacketDataKeys.LIKES)
    public Integer likes = 0;
    @JsonProperty(PacketDataKeys.DISLIKES)
    public Integer dislikes = 0;
    @JsonProperty(PacketDataKeys.SUPER_LIKES)
    public Integer superLikes = 0;
}
