package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.models.enums.FriendType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Friend {
    @JsonProperty("ft")
    public FriendType friendType = FriendType.ACCEPT_REQUEST;
    @JsonProperty("fid")
    public String friendId = "";
    @JsonProperty("u")
    public ShortUser user = new ShortUser();
    @JsonProperty("lm")
    public Message lastMessage = new Message();
}
