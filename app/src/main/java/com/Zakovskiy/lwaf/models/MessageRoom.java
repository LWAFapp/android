package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.models.abc.ABCMessage;
import com.Zakovskiy.lwaf.models.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageRoom extends ABCMessage {
    @JsonProperty("m")
    public String message;
    @JsonProperty("t")
    public MessageType type = MessageType.TEXT;
    @JsonProperty("ts")
    public Long timeSend = 0L;
    @JsonProperty("u")
    public ShortUser user = new ShortUser();
}
