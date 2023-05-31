package com.Zakovskiy.lwaf.models;


import com.Zakovskiy.lwaf.models.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomInLobby implements Serializable {

    @JsonProperty("rid")
    public String roomId = "";
    @JsonProperty("rt")
    public RoomType roomType = RoomType.VK;
    @JsonProperty("rn")
    public String title = "";
    @JsonProperty("pw")
    public Boolean password = false;
    @JsonProperty("pcs")
    public Integer playersCountSize = 0;
    @JsonProperty("p")
    public List<Player> players = new ArrayList<>();
    @JsonProperty("tr")
    public List<Track> tracks = new ArrayList<>();
}
