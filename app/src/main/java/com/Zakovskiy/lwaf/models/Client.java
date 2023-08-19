package com.Zakovskiy.lwaf.models;

import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Client {
    @JsonProperty(PacketDataKeys.IP)
    public String ip = "";
    @JsonProperty(PacketDataKeys.PORT)
    public Integer port = 0;
    @JsonProperty(PacketDataKeys.USER_ID)
    public String userId = "";
    @JsonProperty(PacketDataKeys.NICKNAME)
    public String nickname = "";
}
