package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.models.enums.FriendType;
import com.Zakovskiy.lwaf.models.enums.Sex;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends ShortUser{
    @JsonProperty("wc")
    public Integer wheelCount = 0;
    @JsonProperty("wts")
    public Integer wheelTimestamp = 0;
    @JsonProperty("tb")
    public Long banTime = 0L;
    @JsonProperty("ftr")
    public FavoriteTrack favoriteTrack = null;
    @JsonProperty("ltr")
    public List<LastTrack> lastTracks = new ArrayList<>();
    @JsonProperty("a")
    public String about = "";
    @JsonProperty("tr")
    public Integer tracks = 0;
    @JsonProperty("li")
    public Integer likes = 0;
    @JsonProperty("di")
    public Integer dislikes = 0;
    @JsonProperty("sli")
    public Integer superLikes = 0;
    @JsonProperty("balance")
    public Integer balance = 0;

    @JsonProperty("at")
    public String accessToken = "";
    @JsonProperty("vt")
    public String vkontakteToken = "";
    @JsonProperty("vs")
    public String vkontakteSecret = "";
    @JsonProperty("did")
    public String deviceId = "";
    @JsonProperty("vid")
    public Integer vkontakteId = 0;
    @JsonProperty("rs")
    public List<Rank> ranks = new ArrayList<Rank>();
    @JsonProperty("ft")
    public FriendType friendType = FriendType.REMOVE_FRIEND;
    @JsonProperty("fid")
    public String friendId = "";
}
