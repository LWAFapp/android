package com.Zakovskiy.lwaf.models;

import org.json.*;

import com.Zakovskiy.lwaf.models.enums.Sex;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User
{

    @JsonProperty("uid")
    public String user_id = "";
    @JsonProperty("n")
    public String nickname = "";
    @JsonProperty("s")
    public Sex sex = Sex.MALE;
    @JsonProperty("r")
    public Integer role = 0;
    @JsonProperty("ls")
    public Long lastSeen = 0L;
    @JsonProperty("ava")
    public String avatar = "";
    @JsonProperty("wc")
    public Integer wheelCount = 0;
    @JsonProperty("wts")
    public Integer wheelTimestamp = 0;
    @JsonProperty("tb")
    public Long timeBan = 0L;
    @JsonProperty("ftr")
    public String favoriteTrack = "";
    @JsonProperty("ltr")
    public List<String> lastTracks = new ArrayList<>();
    @JsonProperty("a")
    public String about = "";
    @JsonProperty("li")
    public Integer likes = 0;
    @JsonProperty("sli")
    public Integer superLikes = 0;
    @JsonProperty("di")
    public Integer dislikes = 0;
    @JsonProperty("tr")
    public Integer tracks = 0;
    @JsonProperty("b")
    public Integer balance = 0;
    @JsonProperty("hb")
    public Boolean hidedBalance = false;
    @JsonProperty("hlt")
    public Boolean hidedLastTracks = false;
    @JsonProperty("hf")
    public Boolean hidedFavorites = false;
    @JsonProperty("at")
    public String accessToken = "";
    @JsonProperty("vt")
    public String vkontakteToken = "";
    @JsonProperty("did")
    public String deviceId = "";
    @JsonProperty("vid")
    public Integer vkontakteId = 0;
    @JsonProperty("ma")
    public String mail = "";
}
