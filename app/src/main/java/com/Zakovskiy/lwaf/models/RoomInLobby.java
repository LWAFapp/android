package com.Zakovskiy.lwaf.models;


import com.Zakovskiy.lwaf.models.enums.RoomType;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomInLobby implements Serializable {

    @JsonProperty(PacketDataKeys.ROOM_IDENTIFICATOR)
    public String roomId = "";
    @JsonProperty(PacketDataKeys.LOTO_ENABLED)
    public Boolean lotoEnabled = false;
    @JsonProperty(PacketDataKeys.ROOM_TYPE)
    public RoomType roomType = RoomType.VK;
    @JsonProperty(PacketDataKeys.ROOM_NAME)
    public String title = "";
    @JsonProperty(PacketDataKeys.ROOM_PASSWORD)
    public Boolean password = false;
    @JsonProperty(PacketDataKeys.PLAYERS_COUNT_SIZE)
    public Integer playersCountSize = 0;
    @JsonProperty(PacketDataKeys.PLAYERS)
    public List<Player> players = new ArrayList<>();
    @JsonProperty(PacketDataKeys.TRACKS)
    public List<Track> tracks = new ArrayList<>();
}
