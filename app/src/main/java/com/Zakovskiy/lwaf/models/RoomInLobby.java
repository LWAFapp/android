package com.Zakovskiy.lwaf.models;


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
    public String roomId;
    @JsonProperty("rn")
    public String title;
    @JsonProperty("pw")
    public Boolean password;
    @JsonProperty("pcs")
    public Integer playersCountSize;
    @JsonProperty("p")
    public List<ShortUser> players = new ArrayList<>();
}
