package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.models.enums.FriendType;
import com.Zakovskiy.lwaf.models.enums.NewsType;
import com.Zakovskiy.lwaf.models.enums.Sex;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends ShortUser{
    @JsonProperty(PacketDataKeys.WHEEL_COUNT)
    public Integer wheelCount = 0;
    @JsonProperty(PacketDataKeys.WHEEL_TIMESTAMP)
    public Integer wheelTimestamp = 0;
    @JsonProperty(PacketDataKeys.NEWS)
    public NewsType news = NewsType.UNCHEKED;
    @JsonProperty(PacketDataKeys.FRIENDS_NEWS)
    public Integer friendsNews = 0;
    @JsonProperty(PacketDataKeys.TIME_BAN)
    public Long banTime = 0L;
    @JsonProperty(PacketDataKeys.FREE_NICKNAME)
    public Boolean freeNickname;
    @JsonProperty(PacketDataKeys.FAVORITE_TRACK)
    public FavoriteTrack favoriteTrack = null;
    @JsonProperty(PacketDataKeys.LAST_TRACKS)
    public List<LastTrack> lastTracks = new ArrayList<>();
    @JsonProperty(PacketDataKeys.ABOUT)
    public String about = "";
    @JsonProperty(PacketDataKeys.TRACKS)
    public Integer tracks = 0;
    @JsonProperty(PacketDataKeys.LIKES)
    public Integer likes = 0;
    @JsonProperty(PacketDataKeys.DISLIKES)
    public Integer dislikes = 0;
    @JsonProperty(PacketDataKeys.SUPER_LIKES)
    public Integer superLikes = 0;
    @JsonProperty(PacketDataKeys.BALANCE)
    public Integer balance = 0;

    @JsonProperty(PacketDataKeys.ACCESS_TOKEN)
    public String accessToken = "";
    @JsonProperty(PacketDataKeys.VK_TOKEN)
    public String vkontakteToken = "";
    @JsonProperty(PacketDataKeys.VK_SECRET)
    public String vkontakteSecret = "";
    @JsonProperty(PacketDataKeys.DEVICE)
    public String deviceId = "";
    @JsonProperty(PacketDataKeys.VK_ID)
    public Integer vkontakteId = 0;
    @JsonProperty(PacketDataKeys.RANKS)
    public List<Rank> ranks = new ArrayList<Rank>();
    @JsonProperty(PacketDataKeys.FRIEND_TYPE)
    public FriendType friendType = FriendType.REMOVE_FRIEND;
    @JsonProperty(PacketDataKeys.FRIEND_ID)
    public String friendId = "";
    // confidentiality
    @JsonProperty(PacketDataKeys.HIDEN_BALANCE)
    public Boolean hidenBalance = false;
    @JsonProperty(PacketDataKeys.HIDEN_LAST_TRACKS)
    public Boolean hidenLastTracks = false;
    @JsonProperty(PacketDataKeys.HIDEN_FRIENDS)
    public Boolean hidenFriends = false;
}
