package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.models.abc.ABCMessage;
import com.Zakovskiy.lwaf.models.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    @JsonProperty("mid")
    public String messageId = "";
    @JsonProperty("m")
    public String message = "";
    @JsonProperty("t")
    public MessageType type = MessageType.TEXT;
    @JsonProperty("ts")
    public Long timeSend = 0L;
    @JsonProperty("uid")
    public String userId = "";
    @JsonProperty("u")
    public ShortUser user = new ShortUser();
    @JsonProperty("rm")
    public Message replyMessage = null;
    @JsonProperty("b")
    public Bubble bubble = new Bubble();
}
